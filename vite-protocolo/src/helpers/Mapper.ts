import type { AvcDataValues } from "../forms/formPaginationAvc/schemas/AvcData";

export function mapAvcToPlanilha(data: AvcDataValues) {
  const { LinhaDoTempoSection, HistoriaClinicaSection, UnidadeReferenciaSection, DesfechoCenaSection } = data;

    function formatDate(date?: string) {
        if (!date) return "";

        const d = new Date(date);
        if (isNaN(d.getTime())) return "";

        const pad = (n: number) => String(n).padStart(2, "0");

        const day = pad(d.getDate());
        const month = pad(d.getMonth() + 1);
        const year = d.getFullYear();

        const hours = pad(d.getHours());
        const minutes = pad(d.getMinutes());

        return `${day}.${month}.${year}, ${hours}:${minutes}`;
    }
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
      ? formatDate(LinhaDoTempoSection.ultimoHorarioVistoBem)
      : "Not known";

  return {
    "ID do caso (você pode usar letras iniciais, ou numerar de 1, 2, 3 etc)": LinhaDoTempoSection.numeroOcorrencia,

    "Horário que a ambulância chegou na cena":
      formatDate(LinhaDoTempoSection.chegadaCena),

    "Horário que ambulância deixou a cena":
      formatDate(DesfechoCenaSection.horarioSaidaCena),

    "O Hospital foi pré-notificado?":
      hospitalPreNotificado,

    "Nome do hospital para onde o paciente foi encaminhado":
      UnidadeReferenciaSection.unidadeReferenciaEleita || "",

    "Medicação atual":
      medicacaoAtual,

    "Horário que o paciente foi visto normal pela última vez":
      ultimoVistoBem,

    "Hora de chegada ao hospital":
      formatDate(DesfechoCenaSection.horarioChegadaHospital),
  };
}