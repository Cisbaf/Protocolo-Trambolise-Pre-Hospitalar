#!/usr/bin/env bash
# ==========================================================
# Panorama completo do ambiente atual.
#
#   ./protocolo status          resumo de tudo
#   ./protocolo status --watch  atualiza a cada 5s
# ==========================================================

source "$(dirname "${BASH_SOURCE[0]}")/lib/common.sh"

badge_ok()   { printf '%s%s%s' "$C_GREEN" "$1" "$C_RESET"; }
badge_bad()  { printf '%s%s%s' "$C_RED" "$1" "$C_RESET"; }
badge_warn() { printf '%s%s%s' "$C_YELLOW" "$1" "$C_RESET"; }

# tcp_check <host> <porta>
tcp_check() {
  local host="$1" port="$2"
  timeout 3 bash -c "exec 3<>/dev/tcp/$host/$port" 2>/dev/null
}

# extrai host e porta de uma URL jdbc:mysql://host:porta/base
jdbc_host_port() {
  local url="$1" hostport
  hostport="${url#jdbc:mysql://}"
  hostport="${hostport%%/*}"
  printf '%s' "$hostport"
}

sec_projeto() {
  section "Projeto"
  printf '  %-14s %s\n' "Diretorio" "$PROJECT_ROOT"
  printf '  %-14s %s\n' "Branch" "$(git_branch)"
  printf '  %-14s %s\n' "Commit" "$(git_commit)"
  if git_dirty; then
    printf '  %-14s %s\n' "Working tree" "$(badge_warn "com alteracoes nao commitadas")"
  else
    printf '  %-14s %s\n' "Working tree" "$(badge_ok limpo)"
  fi
}

sec_ambiente() {
  section "Ambiente"
  if [[ ! -f "$ENV_FILE" ]]; then
    printf '  %-14s %s\n' ".env" "$(badge_bad "ausente - rode ./protocolo setup dev")"
    return 1
  fi

  local mode; mode="$(current_mode)"
  case "$mode" in
    dev)  printf '  %-14s %s\n' "Modo" "$(badge_ok desenvolvimento)" ;;
    prod) printf '  %-14s %s\n' "Modo" "$(badge_warn PRODUCAO)" ;;
    *)    printf '  %-14s %s\n' "Modo" "$(badge_bad "desconhecido (APP_ENV ausente)")" ;;
  esac

  printf '  %-14s %s (%s)\n' ".env" "presente" "$(stat -c '%a' "$ENV_FILE" 2>/dev/null || printf '?')"

  local check_mode="$mode"
  [[ "$check_mode" == "desconhecido" ]] && check_mode=dev
  if validate_env "$check_mode" >/dev/null 2>&1; then
    printf '  %-14s %s\n' "Variaveis" "$(badge_ok "completas")"
  else
    printf '  %-14s %s\n' "Variaveis" "$(badge_bad "incompletas - rode ./protocolo setup check")"
  fi
}

sec_config() {
  [[ -f "$ENV_FILE" ]] || return 0
  section "Configuracao"
  local k
  for k in AVC_FRONT_PORT AVC_BACK_PORT VITE_API_URL VITE_MODE DB_PROTOCOL_URL DB_PROTOCOL_USER LOG_PROTOCOL_PATH; do
    printf '  %-22s %s\n' "$k" "$(env_get "$ENV_FILE" "$k" 2>/dev/null || printf '(nao definido)')"
  done
  for k in JWT_SECRET DB_PROTOCOL_PASSWORD; do
    printf '  %-22s %s\n' "$k" "$(mask "$(env_get "$ENV_FILE" "$k" 2>/dev/null || printf '')")"
  done
}

sec_containers() {
  section "Containers"
  if ! command -v docker >/dev/null 2>&1 || ! docker info >/dev/null 2>&1; then
    printf '  %s\n' "$(badge_bad "Docker indisponivel")"
    return 0
  fi
  local mode; mode="$(current_mode)"
  [[ "$mode" == "desconhecido" ]] && mode=dev

  local out
  out="$(dc "$mode" ps --format 'table {{.Service}}\t{{.State}}\t{{.Status}}\t{{.Ports}}' 2>/dev/null || true)"
  if [[ -z "$out" || "$(printf '%s' "$out" | wc -l)" -lt 1 ]]; then
    printf '  %s\n' "$(badge_warn "nenhum container do projeto em execucao")"
  else
    printf '%s\n' "$out" | sed 's/^/  /'
  fi
}

sec_saude() {
  [[ -f "$ENV_FILE" ]] || return 0
  section "Saude dos servicos"
  local front back fs bs
  front="$(env_get "$ENV_FILE" AVC_FRONT_PORT 2>/dev/null || printf 5173)"
  back="$(env_get "$ENV_FILE" AVC_BACK_PORT 2>/dev/null || printf 8080)"

  fs="$(http_status "http://localhost:$front")"
  bs="$(http_status "http://localhost:$back/v3/api-docs")"

  if [[ "$fs" == "000" ]]; then
    printf '  %-12s %s  http://localhost:%s\n' "Frontend" "$(badge_bad "sem resposta")" "$front"
  else
    printf '  %-12s %s  http://localhost:%s\n' "Frontend" "$(badge_ok "HTTP $fs")" "$front"
  fi

  if [[ "$bs" == "000" ]]; then
    printf '  %-12s %s  http://localhost:%s\n' "Backend" "$(badge_bad "sem resposta")" "$back"
  else
    printf '  %-12s %s  http://localhost:%s\n' "Backend" "$(badge_ok "HTTP $bs")" "$back"
  fi
}

sec_banco() {
  [[ -f "$ENV_FILE" ]] || return 0
  section "Banco de dados"
  local url hostport host port
  url="$(env_get "$ENV_FILE" DB_PROTOCOL_URL 2>/dev/null || true)"
  [[ -n "$url" ]] || { printf '  %s\n' "$(badge_bad "DB_PROTOCOL_URL nao definido")"; return 0; }

  hostport="$(jdbc_host_port "$url")"
  host="${hostport%%:*}"
  port="${hostport##*:}"
  [[ "$port" == "$host" ]] && port=3306

  printf '  %-12s %s\n' "Destino" "$hostport"

  # 'mysql-db' so resolve dentro da rede do compose
  if [[ "$host" == "mysql-db" ]]; then
    local mport; mport="$(env_get "$ENV_FILE" DB_PORT 2>/dev/null || printf 3306)"
    if tcp_check 127.0.0.1 "$mport"; then
      printf '  %-12s %s (via localhost:%s)\n' "Conexao" "$(badge_ok alcancavel)" "$mport"
    else
      printf '  %-12s %s\n' "Conexao" "$(badge_bad "container mysql-db fora do ar")"
    fi
  elif tcp_check "$host" "$port"; then
    printf '  %-12s %s\n' "Conexao" "$(badge_ok alcancavel)"
  else
    printf '  %-12s %s\n' "Conexao" "$(badge_bad "inalcancavel a partir desta maquina")"
  fi
}

sec_logs() {
  section "Logs"
  local dir="$PROJECT_ROOT/spring-protocolo/logs"
  if [[ -d "$dir" ]]; then
    printf '  %-14s %s\n' "Diretorio" "${dir#"$PROJECT_ROOT"/}"
    printf '  %-14s %s\n' "Tamanho" "$(du -sh "$dir" 2>/dev/null | cut -f1)"
    local warnfile="$dir/warn-error.log"
    if [[ -f "$warnfile" ]]; then
      local n; n="$(wc -l < "$warnfile" 2>/dev/null || printf 0)"
      printf '  %-14s %s linhas\n' "warn-error" "$n"
    fi
  else
    printf '  %s\n' "$(dim "sem diretorio de logs local")"
  fi
}

run_all() {
  sec_projeto
  sec_ambiente || true
  sec_config
  sec_containers
  sec_saude
  sec_banco
  sec_logs
  printf '\n'
}

main() {
  case "${1:-}" in
    --watch|-w)
      while true; do
        clear
        run_all
        dim "atualizando a cada 5s - Ctrl+C para sair"
        sleep 5
      done
      ;;
    -h|--help) sed -n '2,7p' "${BASH_SOURCE[0]}" | sed 's/^# \{0,1\}//' ;;
    *) run_all ;;
  esac
}

main "$@"
