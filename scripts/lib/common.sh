#!/usr/bin/env bash
# ==========================================================
# Funcoes e constantes compartilhadas pelos scripts.
# Sempre carregado via `source`.
# ==========================================================

set -euo pipefail

# ---- Caminhos ---------------------------------------------------------------
PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
SCRIPTS_DIR="$PROJECT_ROOT/scripts"
ENV_FILE="$PROJECT_ROOT/.env"
COMPOSE_BASE="$PROJECT_ROOT/docker-compose.yml"
COMPOSE_DEV="$PROJECT_ROOT/docker-compose.dev.yml"
TEMPLATE_DEV="$PROJECT_ROOT/.env.example"
TEMPLATE_PROD="$PROJECT_ROOT/.env.prod.example"

# ---- Variaveis obrigatorias por ambiente ------------------------------------
REQUIRED_COMMON=(
  APP_ENV
  AVC_FRONT_PORT
  AVC_BACK_PORT
  VITE_API_URL
  DB_PROTOCOL_URL
  DB_PROTOCOL_USER
  DB_PROTOCOL_PASSWORD
  LOG_PROTOCOL_PATH
  JWT_SECRET
  JWT_EXPIRATION
)
REQUIRED_DEV=(MYSQL_ROOT_PASSWORD MYSQL_DATABASE VITE_MODE)
REQUIRED_PROD=()

# Variaveis cujo valor nunca deve ser impresso na tela
SECRET_KEYS=(JWT_SECRET DB_PROTOCOL_PASSWORD MYSQL_ROOT_PASSWORD MYSQL_PASSWORD)

# ---- Cores ------------------------------------------------------------------
if [[ -t 1 && -z "${NO_COLOR:-}" ]]; then
  C_RESET=$'\033[0m'; C_BOLD=$'\033[1m'; C_DIM=$'\033[2m'
  C_RED=$'\033[31m'; C_GREEN=$'\033[32m'; C_YELLOW=$'\033[33m'
  C_BLUE=$'\033[34m'; C_CYAN=$'\033[36m'
else
  C_RESET=''; C_BOLD=''; C_DIM=''
  C_RED=''; C_GREEN=''; C_YELLOW=''; C_BLUE=''; C_CYAN=''
fi

section() { printf '\n%s%s== %s%s\n' "$C_BOLD" "$C_BLUE" "$1" "$C_RESET"; }
log()     { printf '%s\n' "$*"; }
info()    { printf '%s->%s %s\n' "$C_CYAN" "$C_RESET" "$*"; }
ok()      { printf '%s[ ok ]%s %s\n' "$C_GREEN" "$C_RESET" "$*"; }
warn()    { printf '%s[aviso]%s %s\n' "$C_YELLOW" "$C_RESET" "$*" >&2; }
err()     { printf '%s[erro]%s %s\n' "$C_RED" "$C_RESET" "$*" >&2; }
die()     { err "$*"; exit 1; }
dim()     { printf '%s%s%s\n' "$C_DIM" "$*" "$C_RESET"; }

# ---- Docker Compose ---------------------------------------------------------
COMPOSE_BIN=()
detect_compose() {
  if [[ ${#COMPOSE_BIN[@]} -gt 0 ]]; then return 0; fi
  if docker compose version >/dev/null 2>&1; then
    COMPOSE_BIN=(docker compose)
  elif command -v docker-compose >/dev/null 2>&1; then
    COMPOSE_BIN=(docker-compose)
  else
    die "Docker Compose nao encontrado. Instale o docker com o plugin compose."
  fi
}

# dc <dev|prod> [args...]
dc() {
  detect_compose
  local mode="$1"; shift
  case "$mode" in
    dev)  "${COMPOSE_BIN[@]}" -f "$COMPOSE_BASE" -f "$COMPOSE_DEV" "$@" ;;
    prod) "${COMPOSE_BIN[@]}" -f "$COMPOSE_BASE" "$@" ;;
    *)    die "Modo invalido para o compose: '$mode' (use dev ou prod)" ;;
  esac
}

require_docker() {
  command -v docker >/dev/null 2>&1 || die "Docker nao encontrado no PATH."
  docker info >/dev/null 2>&1 || die "O daemon do Docker nao esta acessivel. Ele esta rodando? Seu usuario esta no grupo 'docker'?"
  detect_compose
}

# ---- Leitura do .env (sem executar o arquivo) -------------------------------
# env_get_raw <arquivo> <chave>  -> valor literal, como esta no arquivo
env_get_raw() {
  local file="$1" key="$2" line
  [[ -f "$file" ]] || return 1
  line="$(grep -E "^[[:space:]]*${key}[[:space:]]*=" "$file" | tail -n1 || true)"
  [[ -n "$line" ]] || return 1
  printf '%s' "${line#*=}"
}

# env_get <arquivo> <chave> -> valor com ${OUTRA_VAR} ja resolvido,
# do mesmo jeito que o docker compose faz ao ler o .env
env_get() {
  local file="$1" key="$2" value ref refval depth=0
  value="$(env_get_raw "$file" "$key")" || return 1
  while [[ "$value" == *'${'*'}'* && $depth -lt 5 ]]; do
    ref="${value#*\$\{}"; ref="${ref%%\}*}"
    refval="$(env_get_raw "$file" "${ref%%:*}" 2>/dev/null || true)"
    value="${value//\$\{$ref\}/$refval}"
    depth=$((depth + 1))
  done
  printf '%s' "$value"
}

# env_keys <arquivo> -> lista as chaves definidas
env_keys() {
  local file="$1"
  [[ -f "$file" ]] || return 1
  grep -oE '^[[:space:]]*[A-Za-z_][A-Za-z0-9_]*[[:space:]]*=' "$file" \
    | tr -d ' =' | sort -u
}

is_secret() {
  local key="$1" s
  for s in "${SECRET_KEYS[@]}"; do [[ "$key" == "$s" ]] && return 0; done
  return 1
}

# mask <valor> -> mostra so o comeco
mask() {
  local v="$1"
  if [[ -z "$v" ]]; then printf '(vazio)'
  elif [[ ${#v} -le 4 ]]; then printf '****'
  else printf '%s****(%d chars)' "${v:0:3}" "${#v}"
  fi
}

require_env_file() {
  [[ -f "$ENV_FILE" ]] || die ".env nao encontrado. Rode:  ./protocolo setup dev   (ou setup prod)"
}

# current_mode -> dev | prod | desconhecido
current_mode() {
  local v
  v="$(env_get "$ENV_FILE" APP_ENV 2>/dev/null || true)"
  case "$v" in
    dev|prod) printf '%s' "$v"; return 0 ;;
  esac
  # Heuristica para .env antigos, sem APP_ENV
  if [[ "$(env_get "$ENV_FILE" VITE_MODE 2>/dev/null || true)" == "dev" ]]; then
    printf 'dev'
  elif [[ "$(env_get "$ENV_FILE" DB_PROTOCOL_URL 2>/dev/null || true)" == *localhost* ]] \
    || [[ "$(env_get "$ENV_FILE" DB_PROTOCOL_URL 2>/dev/null || true)" == *mysql-db* ]]; then
    printf 'dev'
  else
    printf 'desconhecido'
  fi
}

# assert_mode <esperado> -- aborta se o .env for de outro ambiente
assert_mode() {
  local expected="$1" actual
  actual="$(current_mode)"
  if [[ "$actual" == "$expected" ]]; then return 0; fi
  if [[ "$actual" == "desconhecido" ]]; then
    warn "Nao consegui identificar o ambiente do .env (APP_ENV ausente). Assumindo '$expected'."
    return 0
  fi
  err "O .env atual e' de '$actual' e voce pediu '$expected'."
  err "Rode  ./protocolo setup $expected  para trocar (o .env atual sera salvo em backup)."
  exit 1
}

# validate_env <dev|prod> -- retorna 1 se faltar variavel obrigatoria
validate_env() {
  local mode="$1" missing=() key value required=()
  required=("${REQUIRED_COMMON[@]}")
  if [[ "$mode" == "dev" ]]; then
    required+=("${REQUIRED_DEV[@]}")
  else
    required+=("${REQUIRED_PROD[@]:-}")
  fi

  for key in "${required[@]}"; do
    [[ -n "$key" ]] || continue
    if ! value="$(env_get "$ENV_FILE" "$key")"; then
      missing+=("$key (ausente)")
    elif [[ -z "$value" && "$key" != "VITE_MODE" ]]; then
      missing+=("$key (vazio)")
    elif [[ "$value" == *TROQUE_ME* ]]; then
      missing+=("$key (ainda com TROQUE_ME)")
    fi
  done

  if [[ ${#missing[@]} -gt 0 ]]; then
    err "Variaveis pendentes no .env:"
    printf '        - %s\n' "${missing[@]}" >&2
    return 1
  fi
  return 0
}

# Checagens especificas de producao que nunca devem passar batido
validate_prod_safety() {
  local rc=0 secret vite_mode db_url

  vite_mode="$(env_get "$ENV_FILE" VITE_MODE 2>/dev/null || true)"
  if [[ "$vite_mode" == "dev" ]]; then
    err "VITE_MODE=dev em producao: o front vai apontar para VITE_API_URL em vez da origem da pagina."
    rc=1
  fi

  secret="$(env_get "$ENV_FILE" JWT_SECRET 2>/dev/null || true)"
  if [[ ${#secret} -lt 32 ]]; then
    err "JWT_SECRET tem ${#secret} caracteres; HS256 exige pelo menos 32."
    rc=1
  fi
  if [[ "$secret" == dev* || "$secret" == mySuperSecretKey* ]]; then
    warn "JWT_SECRET parece ser o segredo de exemplo. Gere um proprio: openssl rand -base64 64 | tr -d '\\n'"
  fi

  db_url="$(env_get "$ENV_FILE" DB_PROTOCOL_URL 2>/dev/null || true)"
  if [[ "$db_url" == *mysql-db* ]]; then
    err "DB_PROTOCOL_URL aponta para 'mysql-db' (container de dev). Em producao o banco e' externo."
    rc=1
  fi

  return $rc
}

# ---- Utilidades -------------------------------------------------------------
confirm() {
  local prompt="$1" answer
  if [[ "${ASSUME_YES:-0}" == "1" ]]; then return 0; fi
  printf '%s%s%s [s/N] ' "$C_YELLOW" "$prompt" "$C_RESET"
  read -r answer || true
  [[ "$answer" =~ ^([sS]|[yY])$ ]]
}

port_in_use() {
  local port="$1"
  if command -v ss >/dev/null 2>&1; then
    ss -ltn "( sport = :$port )" 2>/dev/null | tail -n +2 | grep -q .
  elif command -v lsof >/dev/null 2>&1; then
    lsof -iTCP:"$port" -sTCP:LISTEN >/dev/null 2>&1
  else
    return 1
  fi
}

# http_status <url> -> imprime o codigo HTTP (000 se nao respondeu)
http_status() {
  local code=''
  code="$(curl -s -o /dev/null -w '%{http_code}' --max-time 5 "$1" 2>/dev/null)" || code=''
  [[ -n "$code" ]] || code='000'
  printf '%s' "$code"
}

git_branch() { git -C "$PROJECT_ROOT" rev-parse --abbrev-ref HEAD 2>/dev/null || printf '?'; }
git_commit() { git -C "$PROJECT_ROOT" log -1 --format='%h %s' 2>/dev/null || printf '?'; }
git_dirty()  { [[ -n "$(git -C "$PROJECT_ROOT" status --porcelain 2>/dev/null)" ]]; }
