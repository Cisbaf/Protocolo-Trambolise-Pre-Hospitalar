#!/usr/bin/env bash
# ==========================================================
# Ambiente de DESENVOLVIMENTO (docker-compose.yml + docker-compose.dev.yml).
#
#   ./protocolo dev up [--build]   sobe front + back + mysql
#   ./protocolo dev down           derruba (mantem o volume do banco)
#   ./protocolo dev restart [svc]  reinicia tudo ou um servico
#   ./protocolo dev rebuild [svc]  reconstroi as imagens e sobe
#   ./protocolo dev logs [svc]     acompanha os logs
#   ./protocolo dev ps             lista os containers
#   ./protocolo dev shell <svc>    abre um shell no container
#   ./protocolo dev reset          APAGA o volume do MySQL e sobe do zero
#   ./protocolo dev seed [opcoes]  popula o banco com usuario e dados de teste
#   ./protocolo dev exec <svc> ... executa um comando no container
# ==========================================================

source "$(dirname "${BASH_SOURCE[0]}")/lib/common.sh"

MODE=dev

preflight() {
  require_docker
  require_env_file
  assert_mode dev
  validate_env dev || die "Corrija o .env antes de subir. Use: ./protocolo setup check"

  local front back
  front="$(env_get "$ENV_FILE" AVC_FRONT_PORT || printf 5173)"
  back="$(env_get "$ENV_FILE" AVC_BACK_PORT || printf 8080)"

  local p
  for p in "$front" "$back"; do
    if port_in_use "$p" && ! dc "$MODE" ps --status running 2>/dev/null | grep -q .; then
      warn "A porta $p ja esta em uso por outro processo."
      warn "Troque AVC_FRONT_PORT/AVC_BACK_PORT no .env ou libere a porta."
    fi
  done
}

urls() {
  local front back
  front="$(env_get "$ENV_FILE" AVC_FRONT_PORT || printf 5173)"
  back="$(env_get "$ENV_FILE" AVC_BACK_PORT || printf 8080)"
  section "Enderecos"
  log "  Frontend  http://localhost:$front"
  log "  Backend   http://localhost:$back"
  log "  Swagger   http://localhost:$back/swagger-ui.html"
  log "  MySQL     localhost:$(env_get "$ENV_FILE" DB_PORT || printf 3306)"
}

wait_backend() {
  local back tries=0 max=60 status
  back="$(env_get "$ENV_FILE" AVC_BACK_PORT || printf 8080)"
  info "Aguardando o backend responder (o primeiro start compila o projeto, pode levar alguns minutos)..."
  while (( tries < max )); do
    status="$(http_status "http://localhost:$back/v3/api-docs")"
    if [[ "$status" != "000" ]]; then
      ok "Backend respondendo (HTTP $status)"
      return 0
    fi
    sleep 5
    tries=$((tries + 1))
  done
  warn "O backend ainda nao respondeu. Veja os logs: ./protocolo dev logs spring-protocolo"
  return 0
}

cmd_up() {
  preflight
  local build_args=()
  [[ "${1:-}" == "--build" ]] && build_args=(--build)
  info "Subindo ambiente de desenvolvimento..."
  dc "$MODE" up -d "${build_args[@]}"
  dc "$MODE" ps
  wait_backend
  urls
}

cmd_down() {
  require_docker
  info "Derrubando containers (o volume do banco e' preservado)..."
  dc "$MODE" down
  ok "Ambiente de desenvolvimento parado"
}

cmd_restart() {
  require_docker
  if [[ -n "${1:-}" ]]; then
    dc "$MODE" restart "$1"
    ok "Servico '$1' reiniciado"
  else
    dc "$MODE" restart
    ok "Todos os servicos reiniciados"
  fi
}

cmd_rebuild() {
  preflight
  info "Reconstruindo imagens..."
  if [[ -n "${1:-}" ]]; then
    dc "$MODE" build --no-cache "$1"
    dc "$MODE" up -d "$1"
  else
    dc "$MODE" build --no-cache
    dc "$MODE" up -d
  fi
  dc "$MODE" ps
  ok "Rebuild concluido"
}

cmd_reset() {
  require_docker
  require_env_file
  assert_mode dev
  warn "Isso APAGA o volume do MySQL de desenvolvimento. Todos os dados locais serao perdidos."
  confirm "Confirma o reset do banco de desenvolvimento?" || { info "Cancelado."; return 0; }
  dc "$MODE" down -v
  ok "Volumes removidos"
  cmd_up --build
  # banco vazio recem-criado: repopular e' o que se quer em 99% dos casos
  bash "$SCRIPTS_DIR/seed.sh"
}

cmd_shell() {
  require_docker
  local svc="${1:-}"
  [[ -n "$svc" ]] || die "Informe o servico: vite-protocolo | spring-protocolo | mysql-db"
  # alpine nao tem bash; tenta bash e cai para sh
  dc "$MODE" exec "$svc" bash 2>/dev/null || dc "$MODE" exec "$svc" sh
}

main() {
  local cmd="${1:-up}"; shift || true
  case "$cmd" in
    up)      cmd_up "$@" ;;
    down)    cmd_down ;;
    stop)    require_docker; dc "$MODE" stop ;;
    start)   require_docker; dc "$MODE" start ;;
    restart) cmd_restart "${1:-}" ;;
    rebuild) cmd_rebuild "${1:-}" ;;
    reset)   cmd_reset ;;
    seed)    exec bash "$SCRIPTS_DIR/seed.sh" "$@" ;;
    logs)    require_docker; dc "$MODE" logs -f --tail=200 ${1:+"$1"} ;;
    ps)      require_docker; dc "$MODE" ps ;;
    shell)   cmd_shell "${1:-}" ;;
    exec)    require_docker; dc "$MODE" exec "$@" ;;
    urls)    require_env_file; urls ;;
    -h|--help)
      sed -n '2,17p' "${BASH_SOURCE[0]}" | sed 's/^# \{0,1\}//'
      ;;
    *) die "Subcomando desconhecido: '$cmd'. Rode ./protocolo dev --help" ;;
  esac
}

main "$@"
