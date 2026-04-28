import type { AvcDataValues } from "../forms/formPaginationAvc/schemas/AvcData";
import { formatDateTimeBR } from "../utils/dateUtils";

export function mapAvcFullToPlanilha(data: AvcDataValues) {

    function montarHistoricoDoencas(doencas: Record<string, boolean>): string {
        return Object.entries(doencas)
            .filter(([_, valor]) => valor)
            .map(([nome]) => nome)
            .join(", ");
        }

    function montarListaMedicamentos(lista: string[]): string {
        if (!lista || lista.length === 0) return "";
        return lista.join(", ");
    }

    return {
        "numero_ocorrencia": data.LinhaDoTempoSection.numeroOcorrencia,
        "municipio": data. LinhaDoTempoSection.municipio,
        "abertura_chamado": formatDateTimeBR(data.LinhaDoTempoSection.aberturaChamado),
        "chegada_cena": formatDateTimeBR(data.LinhaDoTempoSection.chegadaCena),
        "ultimo_horario_visto_bem": data.LinhaDoTempoSection.ultimoHorarioVistoBem? formatDateTimeBR(data.LinhaDoTempoSection.ultimoHorarioVistoBem): "",
        "janela_estimada": data.LinhaDoTempoSection.janelaEstimada,
        "balance": data.AvaliacaoNeurologicaSection.balance,
        "eyes": data.AvaliacaoNeurologicaSection.eyes,
        "desvio_facial": data.AvaliacaoNeurologicaSection.desvioFacial,
        "queda_braco": data.AvaliacaoNeurologicaSection.quedaBraco,
        "fala_anormal": data.AvaliacaoNeurologicaSection.falaAnormal,
        "glicemia": data.ParametrosClinicosSection.glicemia,
        "pressao_arterial": data.ParametrosClinicosSection.pressaoArterial,
        "saturacao": data.ParametrosClinicosSection.saturacao,
        "idade": data.HistoriaClinicaSection.idade,
        "historico_doencas": montarHistoricoDoencas(data.HistoriaClinicaSection.doencas),
        "medicamentos": montarListaMedicamentos(data.HistoriaClinicaSection.medicamentos),
        "unidade_referencia_eleita": data.UnidadeReferenciaSection.unidadeReferenciaEleita,
        "horario_notificacao_unidade": formatDateTimeBR(data.UnidadeReferenciaSection.horarioNotificacaoUnidade),
        "horario_saida_cena": formatDateTimeBR(data.DesfechoCenaSection.horarioSaidaCena),
        "horario_chegada_hospital": formatDateTimeBR(data.DesfechoCenaSection.horarioChegadaHospital),
        "elegibilidade": data.ParecerFinalSection.elegibilidade,
        "motivo_elegibilidade":  data.ParecerFinalSection.motivos? montarListaMedicamentos( data.ParecerFinalSection.motivos) : ""
    }

}
