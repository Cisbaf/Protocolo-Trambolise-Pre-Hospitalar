#!/usr/bin/env bash
# ==========================================================
# Diagnostico do ambiente: pre-requisitos, configuracao,
# portas, compose e divergencia de infra entre branches.
#
#   ./protocolo doctor
# ==========================================================

source "$(dirname "${BASH_SOURCE[0]}")/lib/common.sh"

PROBLEMS=0
WARNINGS=0

pass() { printf '  %s[ok]%s   %s\n' "$C_GREEN" "$C_RESET" "$*"; }
fail() { printf '  %s[X]%s    %s\n' "$C_RED" "$C_RESET" "$*"; PROBLEMS=$((PROBLEMS + 1)); }
soft() { printf '  %s[!]%s    %s\n' "$C_YELLOW" "$C_RESET" "$*"; WARNINGS=$((WARNINGS + 1)); }

check_prereqs() {
  section "Pre-requisitos"

  if command -v docker >/dev/null 2>&1; then
    pass "docker $(docker --version | sed 's/Docker version //;s/,.*//')"
    if docker info >/dev/null 2>&1; then
      pass "daemon do docker acessivel"
    else
      fail "daemon do docker inacessivel (rodando? usuario no grupo 'docker'?)"
    fi
  else
    fail "docker nao encontrado no PATH"
  fi

  if docker compose version >/dev/null 2>&1; then
    pass "docker compose $(docker compose version --short 2>/dev/null)"
  elif command -v docker-compose >/dev/null 2>&1; then
    soft "usando docker-compose v1 (legado); prefira o plugin 'docker compose'"
  else
    fail "docker compose nao encontrado"
  fi

  command -v git  >/dev/null 2>&1 && pass "git $(git --version | awk '{print $3}')" || fail "git nao encontrado"
  command -v curl >/dev/null 2>&1 && pass "curl disponivel" || soft "curl ausente (healthchecks dos scripts ficam limitados)"
}

check_files() {
  section "Arquivos de infraestrutura"
  local f
  for f in docker-compose.yml docker-compose.dev.yml .env.example .env.prod.example \
           spring-protocolo/Dockerfile vite-protocolo/Dockerfile \
           spring-protocolo/src/main/resources/application.properties \
           spring-protocolo/src/main/resources/application-dev.properties \
           spring-protocolo/src/main/resources/application-prod.properties; do
    if [[ -f "$PROJECT_ROOT/$f" ]]; then
      pass "$f"
    else
      fail "$f ausente"
    fi
  done
}

check_env() {
  section "Configuracao (.env)"
  if [[ ! -f "$ENV_FILE" ]]; then
    fail ".env ausente - rode ./protocolo setup dev"
    return
  fi

  local mode; mode="$(current_mode)"
  if [[ "$mode" == "desconhecido" ]]; then
    soft "APP_ENV nao definido; os scripts nao conseguem distinguir dev de prod"
    mode=dev
  else
    pass "ambiente identificado: $mode"
  fi

  if validate_env "$mode" >/dev/null 2>&1; then
    pass "variaveis obrigatorias preenchidas"
  else
    fail "variaveis obrigatorias faltando (detalhes em ./protocolo setup check)"
  fi

  if git -C "$PROJECT_ROOT" ls-files --error-unmatch .env >/dev/null 2>&1; then
    fail ".env esta versionado no git - rode: git rm --cached .env"
  else
    pass ".env fora do controle de versao"
  fi

  if [[ "$mode" == "prod" ]]; then
    if validate_prod_safety >/dev/null 2>&1; then
      pass "checagens de seguranca de producao"
    else
      fail "checagens de producao falharam (detalhes em ./protocolo setup check)"
    fi
    local perm; perm="$(stat -c '%a' "$ENV_FILE" 2>/dev/null || printf '?')"
    [[ "$perm" == "600" ]] && pass ".env com permissao 600" || soft ".env com permissao $perm (recomendado 600)"
  fi
}

check_ports() {
  [[ -f "$ENV_FILE" ]] || return 0
  section "Portas"
  local mode; mode="$(current_mode)"
  [[ "$mode" == "desconhecido" ]] && mode=dev

  local name port
  for name in AVC_FRONT_PORT AVC_BACK_PORT; do
    port="$(env_get "$ENV_FILE" "$name" 2>/dev/null || true)"
    [[ -n "$port" ]] || { soft "$name nao definido"; continue; }
    if port_in_use "$port"; then
      if dc "$mode" ps --format '{{.Service}}' 2>/dev/null | grep -q .; then
        pass "$name=$port em uso (containers do projeto estao de pe)"
      else
        soft "$name=$port ocupado por outro processo"
      fi
    else
      pass "$name=$port livre"
    fi
  done
}

check_compose() {
  section "Docker Compose"
  command -v docker >/dev/null 2>&1 && docker info >/dev/null 2>&1 || { soft "docker indisponivel, pulando"; return 0; }
  [[ -f "$ENV_FILE" ]] || { soft ".env ausente, pulando"; return 0; }

  if dc prod config -q >/dev/null 2>&1; then
    pass "docker-compose.yml (producao) valido"
  else
    fail "docker-compose.yml invalido: $(dc prod config -q 2>&1 | head -3)"
  fi

  if dc dev config -q >/dev/null 2>&1; then
    pass "docker-compose.yml + docker-compose.dev.yml (dev) valido"
  else
    fail "override de dev invalido: $(dc dev config -q 2>&1 | head -3)"
  fi
}

check_branches() {
  section "Divergencia de infra entre branches"
  git -C "$PROJECT_ROOT" rev-parse --git-dir >/dev/null 2>&1 || { soft "nao e' um repositorio git"; return 0; }

  local infra=(
    docker-compose.yml
    docker-compose.dev.yml
    spring-protocolo/Dockerfile
    vite-protocolo/Dockerfile
    spring-protocolo/src/main/resources/application.properties
    spring-protocolo/src/main/resources/application-prod.properties
    .env.example
    .env.prod.example
  )

  local here other f diverged=0
  here="$(git_branch)"
  for other in master origin/master dev origin/dev; do
    git -C "$PROJECT_ROOT" rev-parse --verify --quiet "$other" >/dev/null || continue
    [[ "$other" == "$here" ]] && continue
    diverged=0
    for f in "${infra[@]}"; do
      if ! git -C "$PROJECT_ROOT" diff --quiet "$other" -- "$f" 2>/dev/null; then
        diverged=$((diverged + 1))
      fi
    done
    if [[ $diverged -eq 0 ]]; then
      pass "$other: infra identica"
    else
      soft "$other: $diverged arquivo(s) de infra divergentes (um merge pode sobrescrever a config de producao)"
    fi
  done

  # o erro que ja aconteceu antes: prod.properties sumir numa branch
  for other in dev origin/dev master origin/master; do
    git -C "$PROJECT_ROOT" rev-parse --verify --quiet "$other" >/dev/null || continue
    if ! git -C "$PROJECT_ROOT" cat-file -e "$other:spring-protocolo/src/main/resources/application-prod.properties" 2>/dev/null; then
      fail "$other NAO tem application-prod.properties - merge dessa branch derruba producao"
    fi
  done

  # .env versionado em alguma branch
  for other in dev origin/dev master origin/master; do
    git -C "$PROJECT_ROOT" rev-parse --verify --quiet "$other" >/dev/null || continue
    if git -C "$PROJECT_ROOT" cat-file -e "$other:.env" 2>/dev/null; then
      fail "$other tem .env COMMITADO (credenciais expostas no historico)"
    fi
  done
}

main() {
  check_prereqs
  check_files
  check_env
  check_ports
  check_compose
  check_branches

  section "Resumo"
  if [[ $PROBLEMS -eq 0 && $WARNINGS -eq 0 ]]; then
    ok "Nenhum problema encontrado"
  else
    printf '  %s problema(s), %s aviso(s)\n' "$PROBLEMS" "$WARNINGS"
  fi
  printf '\n'
  [[ $PROBLEMS -eq 0 ]]
}

main "$@"
