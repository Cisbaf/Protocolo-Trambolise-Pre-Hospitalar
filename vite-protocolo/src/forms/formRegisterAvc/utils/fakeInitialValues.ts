import type { AvcFormValues } from "../schemas/AvcFormSchema";

export const fakeAvcFormInitialValues: Partial<AvcFormValues> = {
  LinhaDoTempoSection: {
    numeroOcorrencia: "12345/2026",
    municipio: "belford_roxo",
    aberturaChamado: "2026-03-05T04:09",
    chegadaCena: "2026-03-05T05:12",
    naoSoubeInformarLKW: false,
    ultimoHorarioVistoBem: "2026-03-05T07:32",
    janelaEstimada: "2h 20min",
  },
  AvaliacaoNeurologicaSection: {
    desvioFacial: "alterado",
    quedaBraco: "normal",
    eyes: "normal",
    balance: "normal",
    falaAnormal: "normal",
  },

  ParametrosClinicosSection: {
    glicemia: 30,
    pressaoArterial: "190/110",
    saturacao: 80,
  },

  HistoriaClinicaSection: {
    idade: 18,
    uso_coagulante_em_48h: false,
    doencas: {
      "Hipertensão": true,
      "Diabetes": true,
      "AVC Prévio (< 3 meses)": false,
      "Cirurgias de grande porte (< 3 semanas)": false,
    },
    medicamentos: ["Clopidogrel"],
  },

  UnidadeReferenciaSection: {
    unidadeReferenciaEleita: "Hospital Geral de Nova Iguaçu",
    horarioNotificacaoUnidade: "2026-03-05T12:33",
  },

  DesfechoCenaSection: {
    horarioSaidaCena: "2026-03-05T12:50",
    horarioChegadaHospital: "2026-03-05T13:33",
  },

  ParecerFinalSection: {
    elegibilidade: "elegivel",
    motivos: [],
  },
}