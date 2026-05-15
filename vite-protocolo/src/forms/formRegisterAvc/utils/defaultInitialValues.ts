import type { AvcFormValues } from "../schemas/AvcFormSchema";
import { lista_doencas } from "./labels";


export const defaultAvcFormInitialValues: AvcFormValues = {
  LinhaDoTempoSection: {
    numeroOcorrencia: "",
    municipio: "",
    aberturaChamado: "",
    chegadaCena: "",
    ultimoHorarioVistoBem: "",
    naoSoubeInformarLKW: false,
    janelaEstimada: "",
  },

  AvaliacaoNeurologicaSection: {
    balance: "normal",
    eyes: "normal",
    desvioFacial: "normal",
    quedaBraco: "normal",
    falaAnormal: "normal",
  },

  ParametrosClinicosSection: {
    glicemia: 0,
    pressaoArterial: "",
    saturacao: 0,
  },

  HistoriaClinicaSection: {
    idade: 0,
    doencas: Object.fromEntries(
      lista_doencas.map((d) => [d, false])
    ) as Record<(typeof lista_doencas)[number], boolean>,
    medicamentos: [],
  },

  UnidadeReferenciaSection: {
    unidadeReferenciaEleita: "",
    horarioNotificacaoUnidade: "",
  },

  DesfechoCenaSection: {
    horarioSaidaCena: "",
    horarioChegadaHospital: "",
  },

  ParecerFinalSection: {
    elegibilidade: undefined,
    motivos: [],
  },
}