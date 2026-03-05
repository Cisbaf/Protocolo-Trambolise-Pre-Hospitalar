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