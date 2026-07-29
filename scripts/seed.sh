#!/usr/bin/env bash
# ==========================================================
# Popula o banco de DESENVOLVIMENTO com dados de teste.
#
#   ./protocolo dev seed            usuario + protocolos (pula se ja existirem)
#   ./protocolo dev seed --force    insere mesmo que ja existam registros
#   ./protocolo dev seed --reset    APAGA todos os protocolos e recria
#   ./protocolo dev seed --user     cria apenas o usuario
#   ./protocolo dev seed --list     lista o que esta no banco
#
# Os dados entram pela propria API (POST /protocolo), entao passam
# pelas mesmas validacoes de um cadastro feito na tela.
#
# BLOQUEADO fora do ambiente de desenvolvimento.
# ==========================================================

source "$(dirname "${BASH_SOURCE[0]}")/lib/common.sh"

SEED_USER="daniel"
SEED_PASS="admingeral"

COOKIE_JAR=""
cleanup() { [[ -n "$COOKIE_JAR" && -f "$COOKIE_JAR" ]] && rm -f "$COOKIE_JAR"; return 0; }
trap cleanup EXIT

api_url() {
  local port
  port="$(env_get "$ENV_FILE" AVC_BACK_PORT 2>/dev/null || printf 8080)"
  printf 'http://localhost:%s' "$port"
}

# ---- datas relativas a hoje, para o seed nunca ficar "velho" ----
dia() { date -d "$1 days ago" +%Y-%m-%d; }

wait_api() {
  local api tries=0
  api="$(api_url)"
  info "Aguardando a API em $api ..."
  while (( tries < 60 )); do
    [[ "$(http_status "$api/v3/api-docs")" != "000" ]] && { ok "API respondendo"; return 0; }
    sleep 3
    tries=$((tries + 1))
  done
  die "A API nao respondeu. Suba o ambiente antes: ./protocolo dev up"
}

# ---- usuario -----------------------------------------------------------------
criar_usuario() {
  local api code
  api="$(api_url)"
  code="$(curl -s -o /dev/null -w '%{http_code}' -X POST "$api/auth/register" \
    -H 'Content-Type: application/json' \
    -d "{\"username\":\"$SEED_USER\",\"password\":\"$SEED_PASS\"}")"

  case "$code" in
    200) ok "Usuario '$SEED_USER' criado (senha: $SEED_PASS)" ;;
    409) info "Usuario '$SEED_USER' ja existe" ;;
    *)   die "Falha ao criar usuario (HTTP $code)" ;;
  esac
}

login() {
  local api code
  api="$(api_url)"
  COOKIE_JAR="$(mktemp)"
  code="$(curl -s -o /dev/null -w '%{http_code}' -c "$COOKIE_JAR" -X POST "$api/auth/login" \
    -H 'Content-Type: application/json' \
    -d "{\"username\":\"$SEED_USER\",\"password\":\"$SEED_PASS\"}")"
  [[ "$code" == "200" ]] || die "Login do usuario de seed falhou (HTTP $code)"
}

total_protocolos() {
  local api
  api="$(api_url)"
  curl -s -b "$COOKIE_JAR" "$api/protocolo?page=0&size=1" | jq -r '.totalElements // 0' 2>/dev/null || printf 0
}

# ---- os registros ------------------------------------------------------------
# Cada bloco e' um cenario clinico diferente. Os "motivos" seguem exatamente as
# regras de vite-protocolo/src/helpers/ParecerFinal.ts, para o parecer exibido no
# painel bater com o que a tela calcularia.
emit_registro() {
  case "$1" in

  # 1 - ELEGIVEL. Caso classico: janela curta, um sinal neurologico alterado.
  1) cat <<JSON
{
  "LinhaDoTempoSection": {
    "numeroOcorrencia": "2026001/1",
    "municipio": "nova iguacu",
    "aberturaChamado": "$(dia 1)T06:40:00",
    "chegadaCena": "$(dia 1)T07:30:00",
    "ultimoHorarioVistoBem": "$(dia 1)T06:10:00",
    "janelaEstimada": "1h 20min"
  },
  "AvaliacaoNeurologicaSection": {
    "desvioFacial": "alterado", "quedaBraco": "alterado", "falaAnormal": "normal",
    "eyes": "normal", "balance": "normal"
  },
  "ParametrosClinicosSection": { "glicemia": 112, "pressaoArterial": "160/90", "saturacao": 96 },
  "HistoriaClinicaSection": {
    "idade": 67, "uso_coagulante_em_48h": false,
    "doencas": { "Hipertensão": true, "Diabetes": false, "AVC Prévio (< 3 meses)": false, "Cirurgias de grande porte (< 3 semanas)": false },
    "medicamentos": []
  },
  "UnidadeReferenciaSection": {
    "unidadeReferenciaEleita": "Hospital Geral de Nova Iguaçu",
    "horarioNotificacaoUnidade": "$(dia 1)T07:52:00"
  },
  "DesfechoCenaSection": {
    "horarioSaidaCena": "$(dia 1)T08:05:00",
    "horarioChegadaHospital": "$(dia 1)T08:34:00"
  },
  "ParecerFinalSection": { "elegibilidade": "elegivel", "motivos": [] }
}
JSON
  ;;

  # 2 - ELEGIVEL. Usa AAS, que NAO conta como anticoagulante impeditivo.
  2) cat <<JSON
{
  "LinhaDoTempoSection": {
    "numeroOcorrencia": "2026002/1",
    "municipio": "duque de caxias",
    "aberturaChamado": "$(dia 2)T13:40:00",
    "chegadaCena": "$(dia 2)T14:50:00",
    "ultimoHorarioVistoBem": "$(dia 2)T13:05:00",
    "janelaEstimada": "1h 45min"
  },
  "AvaliacaoNeurologicaSection": {
    "desvioFacial": "normal", "quedaBraco": "alterado", "falaAnormal": "alterado",
    "eyes": "normal", "balance": "normal"
  },
  "ParametrosClinicosSection": { "glicemia": 98, "pressaoArterial": "145/85", "saturacao": 97 },
  "HistoriaClinicaSection": {
    "idade": 58, "uso_coagulante_em_48h": false,
    "doencas": { "Hipertensão": true, "Diabetes": true, "AVC Prévio (< 3 meses)": false, "Cirurgias de grande porte (< 3 semanas)": false },
    "medicamentos": ["AAS"]
  },
  "UnidadeReferenciaSection": {
    "unidadeReferenciaEleita": "Hospital Municipalizado Adão Pereira Nunes",
    "horarioNotificacaoUnidade": "$(dia 2)T15:10:00"
  },
  "DesfechoCenaSection": {
    "horarioSaidaCena": "$(dia 2)T15:20:00",
    "horarioChegadaHospital": "$(dia 2)T15:58:00"
  },
  "ParecerFinalSection": { "elegibilidade": "elegivel", "motivos": [] }
}
JSON
  ;;

  # 3 - INELEGIVEL: janela de 6h10, acima do limite de 4h30.
  3) cat <<JSON
{
  "LinhaDoTempoSection": {
    "numeroOcorrencia": "2026003/1",
    "municipio": "belford roxo",
    "aberturaChamado": "$(dia 3)T06:35:00",
    "chegadaCena": "$(dia 3)T07:15:00",
    "ultimoHorarioVistoBem": "$(dia 3)T01:05:00",
    "janelaEstimada": "6h 10min"
  },
  "AvaliacaoNeurologicaSection": {
    "desvioFacial": "alterado", "quedaBraco": "normal", "falaAnormal": "alterado",
    "eyes": "alterado", "balance": "normal"
  },
  "ParametrosClinicosSection": { "glicemia": 134, "pressaoArterial": "175/95", "saturacao": 94 },
  "HistoriaClinicaSection": {
    "idade": 71, "uso_coagulante_em_48h": false,
    "doencas": { "Hipertensão": true, "Diabetes": false, "AVC Prévio (< 3 meses)": false, "Cirurgias de grande porte (< 3 semanas)": false },
    "medicamentos": []
  },
  "UnidadeReferenciaSection": {
    "unidadeReferenciaEleita": "Upa",
    "horarioNotificacaoUnidade": "$(dia 3)T07:40:00"
  },
  "DesfechoCenaSection": {
    "horarioSaidaCena": "$(dia 3)T07:55:00",
    "horarioChegadaHospital": "$(dia 3)T08:40:00"
  },
  "ParecerFinalSection": {
    "elegibilidade": "inelegivel",
    "motivos": ["Tempo entre Visto Bem e Chegada na Cena superior a 4h30."]
  }
}
JSON
  ;;

  # 4 - INELEGIVEL: familia nao soube informar o LKW (ultimoHorarioVistoBem nulo).
  4) cat <<JSON
{
  "LinhaDoTempoSection": {
    "numeroOcorrencia": "2026004/2",
    "municipio": "japeri",
    "aberturaChamado": "$(dia 5)T09:12:00",
    "chegadaCena": "$(dia 5)T09:48:00",
    "ultimoHorarioVistoBem": null,
    "janelaEstimada": "Não informado"
  },
  "AvaliacaoNeurologicaSection": {
    "desvioFacial": "alterado", "quedaBraco": "alterado", "falaAnormal": "alterado",
    "eyes": "normal", "balance": "alterado"
  },
  "ParametrosClinicosSection": { "glicemia": 156, "pressaoArterial": "190/110", "saturacao": 92 },
  "HistoriaClinicaSection": {
    "idade": 79, "uso_coagulante_em_48h": false,
    "doencas": { "Hipertensão": true, "Diabetes": true, "AVC Prévio (< 3 meses)": false, "Cirurgias de grande porte (< 3 semanas)": false },
    "medicamentos": []
  },
  "UnidadeReferenciaSection": {
    "unidadeReferenciaEleita": "Outros Hospitais não elegíveis trombólise",
    "horarioNotificacaoUnidade": "$(dia 5)T10:15:00"
  },
  "DesfechoCenaSection": {
    "horarioSaidaCena": "$(dia 5)T10:30:00",
    "horarioChegadaHospital": "$(dia 5)T11:02:00"
  },
  "ParecerFinalSection": {
    "elegibilidade": "inelegivel",
    "motivos": ["Horário 'Visto Bem' não informado."]
  }
}
JSON
  ;;

  # 5 - INELEGIVEL: menor de 18 anos. Mesma data do registro 4, para testar
  #     o filtro por data trazendo mais de um resultado.
  5) cat <<JSON
{
  "LinhaDoTempoSection": {
    "numeroOcorrencia": "2026005/1",
    "municipio": "mesquita",
    "aberturaChamado": "$(dia 5)T18:55:00",
    "chegadaCena": "$(dia 5)T19:20:00",
    "ultimoHorarioVistoBem": "$(dia 5)T18:30:00",
    "janelaEstimada": "0h 50min"
  },
  "AvaliacaoNeurologicaSection": {
    "desvioFacial": "alterado", "quedaBraco": "normal", "falaAnormal": "normal",
    "eyes": "normal", "balance": "normal"
  },
  "ParametrosClinicosSection": { "glicemia": 88, "pressaoArterial": "120/70", "saturacao": 99 },
  "HistoriaClinicaSection": {
    "idade": 16, "uso_coagulante_em_48h": false,
    "doencas": { "Hipertensão": false, "Diabetes": false, "AVC Prévio (< 3 meses)": false, "Cirurgias de grande porte (< 3 semanas)": false },
    "medicamentos": []
  },
  "UnidadeReferenciaSection": {
    "unidadeReferenciaEleita": "Hospital Municipal São João de Meriti",
    "horarioNotificacaoUnidade": "$(dia 5)T19:45:00"
  },
  "DesfechoCenaSection": {
    "horarioSaidaCena": "$(dia 5)T19:58:00",
    "horarioChegadaHospital": "$(dia 5)T20:26:00"
  },
  "ParecerFinalSection": {
    "elegibilidade": "inelegivel",
    "motivos": ["Paciente menor de 18 anos."]
  }
}
JSON
  ;;

  # 6 - INELEGIVEL: nenhuma avaliacao neurologica alterada.
  6) cat <<JSON
{
  "LinhaDoTempoSection": {
    "numeroOcorrencia": "2026006/1",
    "municipio": "nilopolis",
    "aberturaChamado": "$(dia 8)T08:05:00",
    "chegadaCena": "$(dia 8)T08:35:00",
    "ultimoHorarioVistoBem": "$(dia 8)T07:40:00",
    "janelaEstimada": "0h 55min"
  },
  "AvaliacaoNeurologicaSection": {
    "desvioFacial": "normal", "quedaBraco": "normal", "falaAnormal": "normal",
    "eyes": "normal", "balance": "normal"
  },
  "ParametrosClinicosSection": { "glicemia": 104, "pressaoArterial": "130/80", "saturacao": 98 },
  "HistoriaClinicaSection": {
    "idade": 45, "uso_coagulante_em_48h": false,
    "doencas": { "Hipertensão": false, "Diabetes": false, "AVC Prévio (< 3 meses)": false, "Cirurgias de grande porte (< 3 semanas)": false },
    "medicamentos": []
  },
  "UnidadeReferenciaSection": {
    "unidadeReferenciaEleita": "Upa",
    "horarioNotificacaoUnidade": "$(dia 8)T09:00:00"
  },
  "DesfechoCenaSection": {
    "horarioSaidaCena": "$(dia 8)T09:12:00",
    "horarioChegadaHospital": "$(dia 8)T09:44:00"
  },
  "ParecerFinalSection": {
    "elegibilidade": "inelegivel",
    "motivos": ["Necessário pelo menos uma avaliação neurológica 'Alterado'."]
  }
}
JSON
  ;;

  # 7 - INELEGIVEL: anticoagulante oral em uso (Rivaroxabana).
  7) cat <<JSON
{
  "LinhaDoTempoSection": {
    "numeroOcorrencia": "2026007/1",
    "municipio": "sao joao de meriti",
    "aberturaChamado": "$(dia 9)T11:50:00",
    "chegadaCena": "$(dia 9)T12:35:00",
    "ultimoHorarioVistoBem": "$(dia 9)T11:20:00",
    "janelaEstimada": "1h 15min"
  },
  "AvaliacaoNeurologicaSection": {
    "desvioFacial": "alterado", "quedaBraco": "alterado", "falaAnormal": "normal",
    "eyes": "normal", "balance": "alterado"
  },
  "ParametrosClinicosSection": { "glicemia": 143, "pressaoArterial": "168/94", "saturacao": 95 },
  "HistoriaClinicaSection": {
    "idade": 74, "uso_coagulante_em_48h": false,
    "doencas": { "Hipertensão": true, "Diabetes": false, "AVC Prévio (< 3 meses)": false, "Cirurgias de grande porte (< 3 semanas)": false },
    "medicamentos": ["Rivaroxabana (Xarelto®)"]
  },
  "UnidadeReferenciaSection": {
    "unidadeReferenciaEleita": "Hospital Municipal São João de Meriti",
    "horarioNotificacaoUnidade": "$(dia 9)T12:55:00"
  },
  "DesfechoCenaSection": {
    "horarioSaidaCena": "$(dia 9)T13:08:00",
    "horarioChegadaHospital": "$(dia 9)T13:47:00"
  },
  "ParecerFinalSection": {
    "elegibilidade": "inelegivel",
    "motivos": ["Uso de Anticoagulantes."]
  }
}
JSON
  ;;

  # 8 - INELEGIVEL: uso de anticoagulante nas ultimas 48h.
  8) cat <<JSON
{
  "LinhaDoTempoSection": {
    "numeroOcorrencia": "2026008/3",
    "municipio": "queimados",
    "aberturaChamado": "$(dia 12)T06:20:00",
    "chegadaCena": "$(dia 12)T06:58:00",
    "ultimoHorarioVistoBem": "$(dia 12)T05:50:00",
    "janelaEstimada": "1h 08min"
  },
  "AvaliacaoNeurologicaSection": {
    "desvioFacial": "normal", "quedaBraco": "alterado", "falaAnormal": "alterado",
    "eyes": "alterado", "balance": "normal"
  },
  "ParametrosClinicosSection": { "glicemia": 121, "pressaoArterial": "155/88", "saturacao": 96 },
  "HistoriaClinicaSection": {
    "idade": 63, "uso_coagulante_em_48h": true,
    "doencas": { "Hipertensão": true, "Diabetes": true, "AVC Prévio (< 3 meses)": false, "Cirurgias de grande porte (< 3 semanas)": false },
    "medicamentos": ["Enoxaparina (Lovenox®)"]
  },
  "UnidadeReferenciaSection": {
    "unidadeReferenciaEleita": "Hospital MRJ (SUBPAV)",
    "horarioNotificacaoUnidade": "$(dia 12)T07:20:00"
  },
  "DesfechoCenaSection": {
    "horarioSaidaCena": "$(dia 12)T07:35:00",
    "horarioChegadaHospital": "$(dia 12)T08:10:00"
  },
  "ParecerFinalSection": {
    "elegibilidade": "inelegivel",
    "motivos": ["Uso de Anticoagulantes.", "Fez uso de anticoagulante a menos de 48 horas."]
  }
}
JSON
  ;;

  # 9 - INELEGIVEL: AVC previo ha menos de 3 meses. Mesma data do registro 8.
  9) cat <<JSON
{
  "LinhaDoTempoSection": {
    "numeroOcorrencia": "2026009/1",
    "municipio": "mage",
    "aberturaChamado": "$(dia 12)T20:35:00",
    "chegadaCena": "$(dia 12)T21:10:00",
    "ultimoHorarioVistoBem": "$(dia 12)T20:05:00",
    "janelaEstimada": "1h 05min"
  },
  "AvaliacaoNeurologicaSection": {
    "desvioFacial": "alterado", "quedaBraco": "normal", "falaAnormal": "alterado",
    "eyes": "normal", "balance": "normal"
  },
  "ParametrosClinicosSection": { "glicemia": 167, "pressaoArterial": "182/102", "saturacao": 93 },
  "HistoriaClinicaSection": {
    "idade": 69, "uso_coagulante_em_48h": false,
    "doencas": { "Hipertensão": true, "Diabetes": false, "AVC Prévio (< 3 meses)": true, "Cirurgias de grande porte (< 3 semanas)": false },
    "medicamentos": ["Clopidogrel"]
  },
  "UnidadeReferenciaSection": {
    "unidadeReferenciaEleita": "Hospital Municipalizado Adão Pereira Nunes",
    "horarioNotificacaoUnidade": "$(dia 12)T21:30:00"
  },
  "DesfechoCenaSection": {
    "horarioSaidaCena": "$(dia 12)T21:45:00",
    "horarioChegadaHospital": "$(dia 12)T22:20:00"
  },
  "ParecerFinalSection": {
    "elegibilidade": "inelegivel",
    "motivos": ["AVC Prévio (< 3 meses)"]
  }
}
JSON
  ;;

  # 10 - INELEGIVEL: cirurgia de grande porte ha menos de 3 semanas.
  10) cat <<JSON
{
  "LinhaDoTempoSection": {
    "numeroOcorrencia": "2026010/1",
    "municipio": "itaguai",
    "aberturaChamado": "$(dia 15)T14:55:00",
    "chegadaCena": "$(dia 15)T15:30:00",
    "ultimoHorarioVistoBem": "$(dia 15)T14:25:00",
    "janelaEstimada": "1h 05min"
  },
  "AvaliacaoNeurologicaSection": {
    "desvioFacial": "alterado", "quedaBraco": "alterado", "falaAnormal": "normal",
    "eyes": "alterado", "balance": "normal"
  },
  "ParametrosClinicosSection": { "glicemia": 95, "pressaoArterial": "148/86", "saturacao": 97 },
  "HistoriaClinicaSection": {
    "idade": 61, "uso_coagulante_em_48h": false,
    "doencas": { "Hipertensão": false, "Diabetes": true, "AVC Prévio (< 3 meses)": false, "Cirurgias de grande porte (< 3 semanas)": true },
    "medicamentos": []
  },
  "UnidadeReferenciaSection": {
    "unidadeReferenciaEleita": "Hospital Geral de Nova Iguaçu",
    "horarioNotificacaoUnidade": "$(dia 15)T15:50:00"
  },
  "DesfechoCenaSection": {
    "horarioSaidaCena": "$(dia 15)T16:02:00",
    "horarioChegadaHospital": "$(dia 15)T16:40:00"
  },
  "ParecerFinalSection": {
    "elegibilidade": "inelegivel",
    "motivos": ["Cirurgias de grande porte (< 3 semanas)"]
  }
}
JSON
  ;;

  # 11 - INELEGIVEL por varios motivos ao mesmo tempo (testa lista longa de motivos).
  11) cat <<JSON
{
  "LinhaDoTempoSection": {
    "numeroOcorrencia": "2026011/2",
    "municipio": "seropedica",
    "aberturaChamado": "$(dia 18)T08:30:00",
    "chegadaCena": "$(dia 18)T09:05:00",
    "ultimoHorarioVistoBem": "$(dia 18)T02:10:00",
    "janelaEstimada": "6h 55min"
  },
  "AvaliacaoNeurologicaSection": {
    "desvioFacial": "normal", "quedaBraco": "normal", "falaAnormal": "normal",
    "eyes": "normal", "balance": "normal"
  },
  "ParametrosClinicosSection": { "glicemia": 59, "pressaoArterial": "205/115", "saturacao": 89 },
  "HistoriaClinicaSection": {
    "idade": 15, "uso_coagulante_em_48h": true,
    "doencas": { "Hipertensão": true, "Diabetes": true, "AVC Prévio (< 3 meses)": true, "Cirurgias de grande porte (< 3 semanas)": true },
    "medicamentos": ["Heparina", "Varfarina (Varfine®)"]
  },
  "UnidadeReferenciaSection": {
    "unidadeReferenciaEleita": "Outros Hospitais não elegíveis trombólise",
    "horarioNotificacaoUnidade": "$(dia 18)T09:25:00"
  },
  "DesfechoCenaSection": {
    "horarioSaidaCena": "$(dia 18)T09:40:00",
    "horarioChegadaHospital": "$(dia 18)T10:15:00"
  },
  "ParecerFinalSection": {
    "elegibilidade": "inelegivel",
    "motivos": [
      "Tempo entre Visto Bem e Chegada na Cena superior a 4h30.",
      "Paciente menor de 18 anos.",
      "Necessário pelo menos uma avaliação neurológica 'Alterado'.",
      "AVC Prévio (< 3 meses)",
      "Cirurgias de grande porte (< 3 semanas)",
      "Uso de Anticoagulantes.",
      "Fez uso de anticoagulante a menos de 48 horas."
    ]
  }
}
JSON
  ;;

  # 12 - ELEGIVEL no limite: janela de 4h20, logo abaixo do corte de 4h30.
  12) cat <<JSON
{
  "LinhaDoTempoSection": {
    "numeroOcorrencia": "2026012/1",
    "municipio": "paracambi",
    "aberturaChamado": "$(dia 20)T07:25:00",
    "chegadaCena": "$(dia 20)T08:00:00",
    "ultimoHorarioVistoBem": "$(dia 20)T03:40:00",
    "janelaEstimada": "4h 20min"
  },
  "AvaliacaoNeurologicaSection": {
    "desvioFacial": "normal", "quedaBraco": "normal", "falaAnormal": "normal",
    "eyes": "normal", "balance": "alterado"
  },
  "ParametrosClinicosSection": { "glicemia": 118, "pressaoArterial": "138/82", "saturacao": 98 },
  "HistoriaClinicaSection": {
    "idade": 82, "uso_coagulante_em_48h": false,
    "doencas": { "Hipertensão": true, "Diabetes": false, "AVC Prévio (< 3 meses)": false, "Cirurgias de grande porte (< 3 semanas)": false },
    "medicamentos": ["AAS", "Clopidogrel"]
  },
  "UnidadeReferenciaSection": {
    "unidadeReferenciaEleita": "Hospital Geral de Nova Iguaçu",
    "horarioNotificacaoUnidade": "$(dia 20)T08:20:00"
  },
  "DesfechoCenaSection": {
    "horarioSaidaCena": "$(dia 20)T08:35:00",
    "horarioChegadaHospital": "$(dia 20)T09:10:00"
  },
  "ParecerFinalSection": { "elegibilidade": "elegivel", "motivos": [] }
}
JSON
  ;;
  esac
}

TOTAL_REGISTROS=12

inserir_registros() {
  local api i code criados=0 falhas=0
  api="$(api_url)"

  section "Inserindo $TOTAL_REGISTROS protocolos"
  for (( i = 1; i <= TOTAL_REGISTROS; i++ )); do
    # O id do protocolo e' gerado como timestamp em segundos + 2 digitos
    # aleatorios (Protocolo.gerarIdSeNaoExistir), entao insercoes em rajada
    # colidem na chave primaria de vez em quando. Tentamos de novo no segundo
    # seguinte em vez de perder o registro.
    local tentativa=0
    code=""
    while (( tentativa < 3 )); do
      code="$(emit_registro "$i" | curl -s -o /dev/null -w '%{http_code}' -X POST "$api/protocolo" \
        -H 'Content-Type: application/json' --data-binary @-)"
      [[ "$code" == "200" ]] && break
      tentativa=$((tentativa + 1))
      sleep 1
    done

    if [[ "$code" == "200" ]]; then
      criados=$((criados + 1))
      printf '  %s[ok]%s   registro %2d\n' "$C_GREEN" "$C_RESET" "$i"
    else
      falhas=$((falhas + 1))
      printf '  %s[X]%s    registro %2d recusado pela API (HTTP %s)\n' "$C_RED" "$C_RESET" "$i" "$code"
    fi
  done

  [[ $falhas -eq 0 ]] || warn "$falhas registro(s) nao foram inseridos"
  ok "$criados protocolo(s) inseridos"
}

apagar_protocolos() {
  local api ids total
  api="$(api_url)"
  total="$(total_protocolos)"
  [[ "$total" -gt 0 ]] || { info "Nao ha protocolos para apagar"; return 0; }

  warn "Isso apaga os $total protocolos do banco de DESENVOLVIMENTO."
  confirm "Confirma?" || { info "Cancelado."; return 1; }

  ids="$(curl -s -b "$COOKIE_JAR" "$api/protocolo?page=0&size=1000" | jq -r '.content[].id')"
  local id n=0
  for id in $ids; do
    curl -s -o /dev/null -X DELETE -b "$COOKIE_JAR" "$api/protocolo/$id"
    n=$((n + 1))
  done
  ok "$n protocolo(s) removidos"
}

listar() {
  local api
  api="$(api_url)"
  section "Protocolos no banco"
  curl -s -b "$COOKIE_JAR" "$api/protocolo?page=0&size=100&sort=id" \
    | jq -r '
      "  total: \(.totalElements)\n",
      (["N OCORRENCIA","MUNICIPIO","ABERTURA","UNIDADE","PARECER"] | @tsv),
      (.content[] | [
        .LinhaDoTempoSection.numeroOcorrencia,
        .LinhaDoTempoSection.municipio,
        (.LinhaDoTempoSection.aberturaChamado | split("T")[0]),
        .UnidadeReferenciaSection.unidadeReferenciaEleita,
        .ParecerFinalSection.elegibilidade
      ] | @tsv)' | column -t -s $'\t' | sed 's/^/  /'
}

resumo() {
  section "Como usar no painel"
  log "  Login          usuario: $SEED_USER   senha: $SEED_PASS"
  log ""
  log "  Filtro por unidade (busca parcial, LIKE):"
  log "    'Hospital'      -> traz varias unidades"
  log "    'Nova Iguaçu'   -> 3 registros"
  log "    'Upa'           -> 2 registros"
  log ""
  log "  Filtro por data de abertura (busca exata):"
  printf '    %-12s -> 2 registros no mesmo dia\n' "$(dia 5)"
  printf '    %-12s -> 2 registros no mesmo dia\n' "$(dia 12)"
  log ""
  log "  Filtro por N. ocorrencia (busca exata):"
  log "    2026001/1  (elegivel)   2026011/2  (inelegivel, 7 motivos)"
  log ""
  local total paginas
  total="$(total_protocolos)"
  paginas=$(( (total + 4) / 5 ))
  log "  Paginacao: $total registro(s) no banco, pagina de 5 = $paginas pagina(s)"
  log "  Pareceres: 3 elegiveis e 9 inelegiveis, cada um por um motivo diferente"
}

main() {
  local opt="${1:-}"

  require_env_file
  assert_mode dev
  command -v jq >/dev/null 2>&1 || die "jq e' necessario para o seed. Instale com: sudo apt install jq"

  case "$opt" in
    -h|--help) sed -n '2,16p' "${BASH_SOURCE[0]}" | sed 's/^# \{0,1\}//'; return 0 ;;
  esac

  wait_api
  criar_usuario
  [[ "$opt" == "--user" ]] && { ok "Somente o usuario foi criado"; return 0; }

  login

  case "$opt" in
    --list)
      listar
      return 0
      ;;
    --reset)
      apagar_protocolos || return 0
      inserir_registros
      ;;
    --force)
      inserir_registros
      ;;
    *)
      local total; total="$(total_protocolos)"
      if [[ "$total" -gt 0 ]]; then
        warn "Ja existem $total protocolo(s) no banco. Nada foi inserido."
        log "  Use --force para inserir mesmo assim, ou --reset para recriar do zero."
        return 0
      fi
      inserir_registros
      ;;
  esac

  listar
  resumo
}

main "$@"
