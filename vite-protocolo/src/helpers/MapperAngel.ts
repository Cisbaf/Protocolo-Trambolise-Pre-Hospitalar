import type { AvcDataValues } from "../forms/formPaginationAvc/schemas/AvcData";
import { formatDatePontuation } from "../utils/dateUtils";

export function mapAvcToPlanilha(data: AvcDataValues) {
  const { LinhaDoTempoSection, HistoriaClinicaSection, UnidadeReferenciaSection, DesfechoCenaSection } = data;

  // Medicação
  const medicamentos = HistoriaClinicaSection.medicamentos;
  const medicacaoAtual =
    medicamentos && medicamentos.length > 0
      ? medicamentos.join(", ")
      : "No drugs";

  // Pré-notificação
  const hospitalPreNotificado =
    UnidadeReferenciaSection.unidadeReferenciaEleita &&
    UnidadeReferenciaSection.horarioNotificacaoUnidade
      ? "Yes"
      : "No";

  // Último visto bem
  const ultimoVistoBem =
    LinhaDoTempoSection.ultimoHorarioVistoBem
      ? formatDatePontuation(LinhaDoTempoSection.ultimoHorarioVistoBem)
      : "Not known";

  return {
    "ID do caso (você pode usar letras iniciais, ou numerar de 1, 2, 3 etc)": LinhaDoTempoSection.numeroOcorrencia,

    "Horário que a ambulância chegou na cena":
      formatDatePontuation(LinhaDoTempoSection.chegadaCena),

    "Horário que ambulância deixou a cena":
      formatDatePontuation(DesfechoCenaSection.horarioSaidaCena),

    "O Hospital foi pré-notificado?":
      hospitalPreNotificado,

    "Nome do hospital para onde o paciente foi encaminhado":
      UnidadeReferenciaSection.unidadeReferenciaEleita || "",

    "Medicação atual":
      medicacaoAtual,

    "Horário que o paciente foi visto normal pela última vez":
      ultimoVistoBem,

    "Hora de chegada ao hospital":
      formatDatePontuation(DesfechoCenaSection.horarioChegadaHospital),
  };
}