import type { AvcDataValues } from "../forms/formPaginationAvc/schemas/AvcData"
import { getDataAtualFormatada } from "../utils/dateUtils";
import { mapAvcFullToPlanilha } from "./MapperFull"
import * as XLSX from "xlsx";


export function exportAvcToFull(list: AvcDataValues[]) {
  const rows = list.map(item => mapAvcFullToPlanilha(item));

  const columns = [
      "numero_ocorrencia",
      "municipio",
      "abertura_chamado",
      "chegada_cena",
      "ultimo_horario_visto_bem",
      "janela_estimada",
      "balance",
      "eyes",
      "desvio_facial",
      "queda_braco",
      "fala_anormal",
      "glicemia",
      "pressao_arterial",
      "saturacao",
      "idade",
      "historico_doencas",
      "medicamentos",
      "unidade_referencia_eleita",
      "horario_notificacao_unidade",
      "horario_saida_cena",
      "horario_chegada_hospital",
      "elegibilidade",
      "motivo_elegibilidade"
  ]
  
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
  
    XLSX.writeFile(workbook, `exportacao-completa-${getDataAtualFormatada()}.xlsx`);
}

