#!/usr/bin/env bash
# ==========================================================
# Logs unificados (detecta o ambiente pelo .env).
#
#   ./protocolo logs               todos os servicos
#   ./protocolo logs back          apenas o backend
#   ./protocolo logs front         apenas o frontend
#   ./protocolo logs db            apenas o MySQL (dev)
#   ./protocolo logs erros         so as linhas de WARN/ERROR do backend
#   ./protocolo logs arquivo       tail do arquivo de log do Spring
# ==========================================================

source "$(dirname "${BASH_SOURCE[0]}")/lib/common.sh"

resolve_mode() {
  require_env_file
  local m; m="$(current_mode)"
  [[ "$m" == "desconhecido" ]] && m=dev
  printf '%s' "$m"
}

main() {
  local alvo="${1:-todos}" mode
  mode="$(resolve_mode)"

  case "$alvo" in
    todos|'')  require_docker; dc "$mode" logs -f --tail=200 ;;
    back|api|spring)  require_docker; dc "$mode" logs -f --tail=200 spring-protocolo ;;
    front|web|vite)   require_docker; dc "$mode" logs -f --tail=200 vite-protocolo ;;
    db|mysql)
      [[ "$mode" == "dev" ]] || die "O banco de producao e' externo ao compose; nao ha logs aqui."
      require_docker; dc "$mode" logs -f --tail=200 mysql-db
      ;;
    erros|errors)
      require_docker
      dc "$mode" logs --tail=1000 spring-protocolo 2>&1 | grep -E 'WARN|ERROR|Exception|Caused by' || log "Nenhum WARN/ERROR nas ultimas 1000 linhas."
      ;;
    arquivo|file)
      local f="$PROJECT_ROOT/spring-protocolo/logs/warn-error.log"
      [[ -f "$f" ]] || die "Arquivo nao encontrado: $f"
      tail -f -n 200 "$f"
      ;;
    -h|--help) sed -n '2,11p' "${BASH_SOURCE[0]}" | sed 's/^# \{0,1\}//' ;;
    *) die "Alvo desconhecido: '$alvo'. Use: todos | back | front | db | erros | arquivo" ;;
  esac
}

main "$@"
