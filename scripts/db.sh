#!/usr/bin/env bash
# ==========================================================
# Utilitarios de banco de dados.
#
#   ./protocolo db info               dados da conexao (senha mascarada)
#   ./protocolo db ping               testa se o banco responde
#   ./protocolo db shell              abre o cliente mysql (dev)
#   ./protocolo db dump [arquivo]     gera um dump .sql
#   ./protocolo db restore <arquivo>  restaura um dump (SOMENTE dev)
#
# Em producao o banco e' externo: nenhum comando destrutivo
# esta disponivel aqui.
# ==========================================================

source "$(dirname "${BASH_SOURCE[0]}")/lib/common.sh"

DUMP_DIR="$PROJECT_ROOT/backups"

parse_db() {
  require_env_file
  DB_URL="$(env_get "$ENV_FILE" DB_PROTOCOL_URL || die "DB_PROTOCOL_URL nao definido")"
  DB_USER="$(env_get "$ENV_FILE" DB_PROTOCOL_USER || die "DB_PROTOCOL_USER nao definido")"
  DB_PASS="$(env_get "$ENV_FILE" DB_PROTOCOL_PASSWORD || die "DB_PROTOCOL_PASSWORD nao definido")"

  local rest="${DB_URL#jdbc:mysql://}"
  local hostport="${rest%%/*}"
  local dbpart="${rest#*/}"
  DB_NAME="${dbpart%%\?*}"
  DB_HOST="${hostport%%:*}"
  DB_PORT="${hostport##*:}"
  [[ "$DB_PORT" == "$DB_HOST" ]] && DB_PORT=3306

  MODE="$(current_mode)"
  if [[ "$MODE" == "desconhecido" ]]; then MODE=dev; fi
}

# Executa o cliente mysql: dentro do container em dev, via imagem descartavel fora
mysql_run() {
  if [[ "$MODE" == "dev" && "$DB_HOST" == "mysql-db" ]]; then
    dc dev exec -T -e MYSQL_PWD="$DB_PASS" mysql-db "$@"
  else
    docker run --rm -i -e MYSQL_PWD="$DB_PASS" mysql:8.1 "$@"
  fi
}

cmd_info() {
  parse_db
  section "Banco de dados ($MODE)"
  printf '  %-10s %s\n' "Host" "$DB_HOST"
  printf '  %-10s %s\n' "Porta" "$DB_PORT"
  printf '  %-10s %s\n' "Base" "$DB_NAME"
  printf '  %-10s %s\n' "Usuario" "$DB_USER"
  printf '  %-10s %s\n' "Senha" "$(mask "$DB_PASS")"
}

cmd_ping() {
  parse_db
  require_docker
  if mysql_run mysqladmin ping -h "$DB_HOST" -P "$DB_PORT" -u "$DB_USER" --silent >/dev/null 2>&1; then
    ok "Banco respondendo em $DB_HOST:$DB_PORT"
  else
    die "Sem resposta de $DB_HOST:$DB_PORT (banco no ar? credenciais corretas?)"
  fi
}

cmd_shell() {
  parse_db
  require_docker
  [[ "$MODE" == "dev" ]] || die "Shell interativo liberado apenas em desenvolvimento."
  dc dev exec -e MYSQL_PWD="$DB_PASS" mysql-db mysql -u "$DB_USER" "$DB_NAME"
}

cmd_dump() {
  parse_db
  require_docker
  mkdir -p "$DUMP_DIR"
  local out="${1:-$DUMP_DIR/${DB_NAME}-${MODE}-$(date +%Y%m%d-%H%M%S).sql}"
  info "Gerando dump de $DB_NAME ($DB_HOST)..."
  mysql_run mysqldump -h "$DB_HOST" -P "$DB_PORT" -u "$DB_USER" \
    --single-transaction --routines --triggers "$DB_NAME" > "$out"
  ok "Dump salvo em: ${out#"$PROJECT_ROOT"/} ($(du -h "$out" | cut -f1))"
}

cmd_restore() {
  parse_db
  require_docker
  local file="${1:-}"
  [[ -n "$file" ]] || die "Informe o arquivo: ./protocolo db restore <arquivo.sql>"
  [[ -f "$file" ]] || die "Arquivo nao encontrado: $file"
  [[ "$MODE" == "dev" ]] || die "Restore bloqueado fora do ambiente de desenvolvimento."

  warn "Isso sobrescreve a base '$DB_NAME' de desenvolvimento."
  confirm "Confirma o restore?" || { info "Cancelado."; return 0; }
  mysql_run mysql -h "$DB_HOST" -P "$DB_PORT" -u "$DB_USER" "$DB_NAME" < "$file"
  ok "Restore concluido"
}

main() {
  case "${1:-info}" in
    info)    cmd_info ;;
    ping)    cmd_ping ;;
    shell)   cmd_shell ;;
    dump)    cmd_dump "${2:-}" ;;
    restore) cmd_restore "${2:-}" ;;
    -h|--help) sed -n '2,13p' "${BASH_SOURCE[0]}" | sed 's/^# \{0,1\}//' ;;
    *) die "Subcomando desconhecido: '$1'. Use: info | ping | shell | dump | restore" ;;
  esac
}

main "$@"
