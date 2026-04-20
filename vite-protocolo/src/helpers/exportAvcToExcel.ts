import * as XLSX from "xlsx";

import type { AvcDataValues } from "../forms/formPaginationAvc/schemas/AvcData";
import { mapAvcToPlanilha } from "./Mapper";


export function exportAvcToExcel(list: AvcDataValues[]) {
  const rows = list.map(item =>
    mapAvcToPlanilha(item)
  );

  const columns = [
    "ID do caso (você pode usar letras iniciais, ou numerar de 1, 2, 3 etc)",
    "Horário que a ambulância chegou na cena",
    "Horário que ambulância deixou a cena",
    "O Hospital foi pré-notificado?",
    "Nome do hospital para onde o paciente foi encaminhado",
    "Medicação atual",
    "Horário que o paciente foi visto normal pela última vez",
    "Hora de chegada ao hospital",
  ];

  const worksheet = XLSX.utils.json_to_sheet(rows, {
    header: columns,
  });

  // largura melhorzinha
  worksheet["!cols"] = [
    { wch: 15 },
    { wch: 30 },
    { wch: 30 },
    { wch: 25 },
    { wch: 35 },
    { wch: 25 },
    { wch: 40 },
    { wch: 30 },
  ];

  const workbook = XLSX.utils.book_new();
  XLSX.utils.book_append_sheet(workbook, worksheet, "Relatório AVC");

  XLSX.writeFile(workbook, "relatorio_avc.xlsx");
}