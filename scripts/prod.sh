#!/usr/bin/env bash
# ==========================================================
# Ambiente de PRODUCAO (apenas docker-compose.yml).
#
#   ./protocolo prod deploy    git pull + build + up + verificacao
#   ./protocolo prod up        sobe sem fazer pull
#   ./protocolo prod build     apenas reconstroi as imagens
#   ./protocolo prod restart   reinicia os servicos
#   ./protocolo prod down      derruba os servicos (pede confirmacao)
#   ./protocolo prod logs [s]  acompanha os logs
#   ./protocolo prod ps        lista os containers
#   ./protocolo prod check     valida .env e compose sem alterar nada
#   ./protocolo prod config    imprime o compose ja interpolado
#
# O banco de producao e' EXTERNO ao compose: nenhum comando aqui
# apaga volume ou toca no MySQL.
# ==========================================================

source "$(dirname "${BASH_SOURCE[0]}")/lib/common.sh"

MODE=prod

preflight() {
  require_docker
  require_env_file
  assert_mode prod

  local rc=0
  validate_env prod || rc=1
  validate_prod_safety || rc=1
  [[ $rc -eq 0 ]] || die "Configuracao de producao invalida. Corrija o .env e tente de novo."

  detect_compose
  dc "$MODE" config -q || die "docker-compose.yml invalido."
  ok "Configuracao de producao validada"
}

show_context() {
  section "Contexto"
  log "  Diretorio  $PROJECT_ROOT"
  log "  Branch     $(git_branch)"
  log "  Commit     $(git_commit)"
  log "  Front      porta $(env_get "$ENV_FILE" AVC_FRONT_PORT || printf '?')"
  log "  Back       porta $(env_get "$ENV_FILE" AVC_BACK_PORT || printf '?')"
  log "  Banco      $(env_get "$ENV_FILE" DB_PROTOCOL_URL || printf '?')"
  if git_dirty; then
    warn "Working tree com alteracoes nao commitadas"
  fi
}

verify_running() {
  local front back tries=0 max=36 fs bs
  front="$(env_get "$ENV_FILE" AVC_FRONT_PORT || printf 5173)"
  back="$(env_get "$ENV_FILE" AVC_BACK_PORT || printf 8080)"

  section "Verificacao pos-deploy"
  info "Aguardando os servicos responderem..."
  while (( tries < max )); do
    fs="$(http_status "http://localhost:$front")"
    bs="$(http_status "http://localhost:$back/v3/api-docs")"
    if [[ "$fs" != "000" && "$bs" != "000" ]]; then
      ok "Frontend HTTP $fs  |  Backend HTTP $bs"
      return 0
    fi
    sleep 5
    tries=$((tries + 1))
  done

  err "Servicos nao responderam dentro do tempo esperado (front=$fs back=$bs)."
  err "Investigue com: ./protocolo prod logs"
  return 1
}

cmd_deploy() {
  preflight
  show_context

  if git_dirty && [[ "${ASSUME_YES:-0}" != "1" ]]; then
    warn "Ha alteracoes locais nao commitadas; o 'git pull' pode falhar."
  fi

  confirm "Continuar com o deploy em PRODUCAO?" || { info "Deploy cancelado."; return 0; }

  section "Atualizando codigo"
  if git -C "$PROJECT_ROOT" remote >/dev/null 2>&1 && [[ -n "$(git -C "$PROJECT_ROOT" remote)" ]]; then
    git -C "$PROJECT_ROOT" pull --ff-only || die "git pull falhou. Resolva manualmente e rode de novo."
    ok "Codigo atualizado: $(git_commit)"
  else
    warn "Nenhum remote configurado; pulando o git pull."
  fi

  section "Build"
  dc "$MODE" build

  section "Subindo"
  dc "$MODE" up -d --remove-orphans
  dc "$MODE" ps

  verify_running
}

cmd_down() {
  require_docker
  warn "Isso deixa a aplicacao de PRODUCAO fora do ar."
  confirm "Confirma derrubar os servicos de producao?" || { info "Cancelado."; return 0; }
  dc "$MODE" down
  ok "Servicos de producao parados"
}

main() {
  local cmd="${1:-}"; shift || true
  case "$cmd" in
    deploy)  cmd_deploy ;;
    up)      preflight; dc "$MODE" up -d --remove-orphans; dc "$MODE" ps; verify_running ;;
    build)   preflight; dc "$MODE" build ;;
    restart) require_docker; dc "$MODE" restart ${1:+"$1"}; verify_running ;;
    down)    cmd_down ;;
    stop)    require_docker; dc "$MODE" stop ;;
    start)   require_docker; dc "$MODE" start; verify_running ;;
    logs)    require_docker; dc "$MODE" logs -f --tail=200 ${1:+"$1"} ;;
    ps)      require_docker; dc "$MODE" ps ;;
    check)   preflight; show_context ;;
    config)  require_env_file; dc "$MODE" config ;;
    ''|-h|--help)
      sed -n '2,21p' "${BASH_SOURCE[0]}" | sed 's/^# \{0,1\}//'
      ;;
    *) die "Subcomando desconhecido: '$cmd'. Rode ./protocolo prod --help" ;;
  esac
}

main "$@"
