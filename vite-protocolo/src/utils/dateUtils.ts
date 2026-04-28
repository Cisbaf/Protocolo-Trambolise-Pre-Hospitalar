export function parseDatetimeLocal(
  value?: string
): Date | undefined {
  if (!value) return undefined;

  const [date, time] = value.split("T");
  if (!date || !time) return undefined;

  const [year, month, day] = date.split("-").map(Number);
  const [hour, minute] = time.split(":").map(Number);

  return new Date(year, month - 1, day, hour, minute);
}

export default function calcularDiferencaEmHorasEMinutos(
  dataInicial: Date,
  dataFinal: Date
) {
  const diferencaMs = dataFinal.getTime() - dataInicial.getTime();

  // Se quiser sempre valor absoluto (ignorar negativo)
  const diferencaTotalMinutos = Math.floor(Math.abs(diferencaMs) / (1000 * 60));

  const horas = Math.floor(diferencaTotalMinutos / 60);
  const minutos = diferencaTotalMinutos % 60;

  return {
    horas,
    minutos,
    totalMinutos: diferencaTotalMinutos,
    formatado: `${horas}h ${minutos.toString().padStart(2, "0")}min`,
  };
}

export function formatDateTimeBR(dateString: string): string {
  const date = new Date(dateString);

  return date.toLocaleString("pt-BR", {
    timeZone: "America/Sao_Paulo",
    day: "2-digit",
    month: "2-digit",
    year: "numeric",
    hour: "2-digit",
    minute: "2-digit",
  });
}


export function formatDatePontuation(date?: string) {
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

export function getDataAtualFormatada(): string {
    const agora = new Date();

    const dia = String(agora.getDate()).padStart(2, "0");
    const mes = String(agora.getMonth() + 1).padStart(2, "0"); // mês começa em 0
    const ano = agora.getFullYear();
    const hora = String(agora.getHours()).padStart(2, "0");
    const minuto = String(agora.getMinutes()).padStart(2, "0");

    return `${dia}-${mes}-${ano}-${hora}-${minuto}`;
}