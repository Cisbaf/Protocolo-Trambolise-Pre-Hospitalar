#!/usr/bin/env bash
# ==========================================================
# Configuracao do ambiente: cria e valida o .env.
#
#   ./protocolo setup dev      cria/troca o .env para desenvolvimento
#   ./protocolo setup prod     cria/troca o .env para producao
#   ./protocolo setup show     mostra a configuracao atual (segredos mascarados)
#   ./protocolo setup check    valida o .env do ambiente atual
#   ./protocolo setup secret   gera um JWT_SECRET novo e imprime
#   ./protocolo setup edit     abre o .env no $EDITOR
# ==========================================================

source "$(dirname "${BASH_SOURCE[0]}")/lib/common.sh"

gen_secret() {
  if command -v openssl >/dev/null 2>&1; then
    openssl rand -base64 64 | tr -d '\n=' | cut -c1-88
  else
    head -c 64 /dev/urandom | base64 | tr -d '\n=' | cut -c1-88
  fi
}

backup_env() {
  local stamp backup
  stamp="$(date +%Y%m%d-%H%M%S)"
  backup="$ENV_FILE.bak-$stamp"
  cp "$ENV_FILE" "$backup"
  info "Backup do .env anterior em: ${backup#"$PROJECT_ROOT"/}"
}

setup_mode() {
  local mode="$1" template
  case "$mode" in
    dev)  template="$TEMPLATE_DEV" ;;
    prod) template="$TEMPLATE_PROD" ;;
    *)    die "Ambiente invalido: '$mode' (use dev ou prod)" ;;
  esac
  [[ -f "$template" ]] || die "Template nao encontrado: $template"

  if [[ -f "$ENV_FILE" ]]; then
    local atual; atual="$(current_mode)"
    warn "Ja existe um .env (ambiente: $atual)."
    confirm "Sobrescrever com o template de '$mode'?" || { info "Nada alterado."; return 0; }
    backup_env
  fi

  cp "$template" "$ENV_FILE"
  ok ".env criado a partir de $(basename "$template")"

  if [[ "$mode" == "prod" ]]; then
    local secret; secret="$(gen_secret)"
    # substitui apenas a linha do JWT_SECRET, preservando o resto
    local tmp; tmp="$(mktemp)"
    awk -v s="$secret" '/^JWT_SECRET=/ { print "JWT_SECRET=" s; next } { print }' "$ENV_FILE" > "$tmp"
    mv "$tmp" "$ENV_FILE"
    chmod 600 "$ENV_FILE"
    ok "JWT_SECRET gerado automaticamente (permissao do .env ajustada para 600)"

    section "Falta preencher"
    log "Edite o .env e ajuste:"
    log "  - DB_PROTOCOL_USER / DB_PROTOCOL_PASSWORD  (credenciais do MySQL externo)"
    log "  - DB_PROTOCOL_URL                          (host/porta do banco)"
    log "  - AVC_FRONT_PORT / AVC_BACK_PORT           (portas publicadas)"
    log ""
    log "Depois valide com:  ./protocolo setup check"
  else
    ok "Ambiente de desenvolvimento pronto para uso: ./protocolo dev up"
  fi
}

show_config() {
  require_env_file
  local mode; mode="$(current_mode)"
  section "Configuracao atual (.env)"
  log "Ambiente: ${C_BOLD}${mode}${C_RESET}"
  log ""
  local key value
  while IFS= read -r key; do
    value="$(env_get "$ENV_FILE" "$key" || true)"
    if is_secret "$key"; then
      printf '  %-26s %s\n' "$key" "$(mask "$value")"
    else
      printf '  %-26s %s\n' "$key" "${value:-(vazio)}"
    fi
  done < <(env_keys "$ENV_FILE")
}

check_config() {
  require_env_file
  local mode rc=0
  mode="$(current_mode)"
  [[ "$mode" == "desconhecido" ]] && mode="dev"

  section "Validando .env (ambiente: $mode)"
  if validate_env "$mode"; then
    ok "Todas as variaveis obrigatorias estao preenchidas"
  else
    rc=1
  fi

  if [[ "$mode" == "prod" ]]; then
    if validate_prod_safety; then
      ok "Checagens especificas de producao passaram"
    else
      rc=1
    fi
  fi

  local perm
  perm="$(stat -c '%a' "$ENV_FILE" 2>/dev/null || printf '?')"
  if [[ "$perm" != "600" && "$mode" == "prod" ]]; then
    warn ".env com permissao $perm. Recomendado: chmod 600 .env"
  fi

  if git -C "$PROJECT_ROOT" ls-files --error-unmatch .env >/dev/null 2>&1; then
    err "O .env esta VERSIONADO no git. Remova com: git rm --cached .env"
    rc=1
  else
    ok ".env fora do controle de versao"
  fi

  return $rc
}

main() {
  local cmd="${1:-}"
  case "$cmd" in
    dev|prod)  setup_mode "$cmd" ;;
    show)      show_config ;;
    check)     check_config ;;
    secret)    gen_secret; printf '\n' ;;
    edit)      require_env_file; "${EDITOR:-nano}" "$ENV_FILE" ;;
    ''|-h|--help)
      sed -n '2,15p' "${BASH_SOURCE[0]}" | sed 's/^# \{0,1\}//'
      ;;
    *) die "Subcomando desconhecido: '$cmd'. Use: dev | prod | show | check | secret | edit" ;;
  esac
}

main "$@"
