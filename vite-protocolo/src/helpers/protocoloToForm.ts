import type { AvcDataValues } from "../forms/formPaginationAvc/schemas/AvcData";
import type { AvcFormValues } from "../forms/formRegisterAvc/schemas/AvcFormSchema";
import { lista_doencas } from "../forms/formRegisterAvc/utils/labels";

/**
 * O backend devolve LocalDateTime ("2026-07-28T06:40:00") e os inputs
 * datetime-local só aceitam "2026-07-28T06:40".
 */
function paraDatetimeLocal(valor?: string | null): string {
  if (!valor) return "";
  return valor.slice(0, 16);
}

/**
 * Converte um registro vindo da API no formato que o formulário espera.
 *
 * Dois campos não existem no backend e precisam ser derivados:
 *  - naoSoubeInformarLKW: verdadeiro quando não há horário "visto bem"
 *  - doencas: o formulário exige as quatro chaves sempre presentes
 */
export function protocoloToFormValues(item: AvcDataValues): AvcFormValues {
  const linhaDoTempo = item.LinhaDoTempoSection;
  const historia = item.HistoriaClinicaSection;

  const vistoBem = paraDatetimeLocal(linhaDoTempo?.ultimoHorarioVistoBem);

  const doencas = Object.fromEntries(
    lista_doencas.map((doenca) => [doenca, !!historia?.doencas?.[doenca]])
  ) as AvcFormValues["HistoriaClinicaSection"]["doencas"];

  return {
    LinhaDoTempoSection: {
      numeroOcorrencia: linhaDoTempo?.numeroOcorrencia ?? "",
      municipio: linhaDoTempo?.municipio ?? "",
      aberturaChamado: paraDatetimeLocal(linhaDoTempo?.aberturaChamado),
      chegadaCena: paraDatetimeLocal(linhaDoTempo?.chegadaCena),
      ultimoHorarioVistoBem: vistoBem,
      naoSoubeInformarLKW: !vistoBem,
      janelaEstimada: linhaDoTempo?.janelaEstimada ?? "",
    },

    AvaliacaoNeurologicaSection: {
      balance: item.AvaliacaoNeurologicaSection?.balance ?? "normal",
      eyes: item.AvaliacaoNeurologicaSection?.eyes ?? "normal",
      desvioFacial: item.AvaliacaoNeurologicaSection?.desvioFacial ?? "normal",
      quedaBraco: item.AvaliacaoNeurologicaSection?.quedaBraco ?? "normal",
      falaAnormal: item.AvaliacaoNeurologicaSection?.falaAnormal ?? "normal",
    },

    ParametrosClinicosSection: {
      glicemia: item.ParametrosClinicosSection?.glicemia ?? 0,
      pressaoArterial: item.ParametrosClinicosSection?.pressaoArterial ?? "",
      saturacao: item.ParametrosClinicosSection?.saturacao ?? 0,
    },

    HistoriaClinicaSection: {
      idade: historia?.idade ?? 0,
      uso_coagulante_em_48h: !!historia?.uso_coagulante_em_48h,
      doencas,
      medicamentos: historia?.medicamentos ?? [],
    },

    UnidadeReferenciaSection: {
      unidadeReferenciaEleita:
        item.UnidadeReferenciaSection?.unidadeReferenciaEleita ?? "",
      horarioNotificacaoUnidade: paraDatetimeLocal(
        item.UnidadeReferenciaSection?.horarioNotificacaoUnidade
      ),
    },

    DesfechoCenaSection: {
      horarioSaidaCena: paraDatetimeLocal(
        item.DesfechoCenaSection?.horarioSaidaCena
      ),
      horarioChegadaHospital: paraDatetimeLocal(
        item.DesfechoCenaSection?.horarioChegadaHospital
      ),
    },

    ParecerFinalSection: {
      elegibilidade: item.ParecerFinalSection?.elegibilidade,
      motivos: item.ParecerFinalSection?.motivos ?? [],
    },
  };
}
