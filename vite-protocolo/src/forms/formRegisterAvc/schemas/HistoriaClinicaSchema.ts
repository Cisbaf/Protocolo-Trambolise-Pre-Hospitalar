import { z } from "zod"
import { label_nega_comorbidades, lista_comorbidades, lista_doencas } from "../utils/labels"


export const HistoriaClinicaSchema = z.object({
  idade: z
    .number({ message: "Informe a idade do paciente" })
    .positive("Idade deve ser um número positivo"),
  uso_coagulante_em_48h: z.boolean().optional(),
  doencas: z.object(
   Object.fromEntries(
    lista_doencas.map((d) => [d, z.boolean()])
    ) as Record<(typeof lista_doencas)[number], z.ZodBoolean>
  ),
  medicamentos: z
    .array(z.string())
    .min(0, "Selecione pelo menos um medicamento"), // pode ser mínimo 0 se não obrigatório
  usa_outras_medicacoes: z.boolean().optional(),
  outras_medicacoes_descricao: z.string().optional(),
}).superRefine((data, ctx) => {
  /**
   * O bloco de doenças não pode mais ficar em branco: antes, "nada marcado"
   * era indistinguível de "formulário não preenchido" na análise posterior.
   */
  const nega_comorbidades = !!data.doencas?.[label_nega_comorbidades];
  const comorbidades_marcadas = lista_comorbidades.filter(
    (doenca) => data.doencas?.[doenca]
  );

  if (!nega_comorbidades && comorbidades_marcadas.length === 0) {
    ctx.addIssue({
      code: z.ZodIssueCode.custom,
      path: ["doencas"],
      message:
        "Selecione ao menos uma comorbidade ou marque NEGA COMORBIDADES",
    });
  }

  // A UI já impede esse estado; aqui é rede de segurança para dados vindos
  // do banco (registros antigos) ou de payload montado fora do formulário.
  if (nega_comorbidades && comorbidades_marcadas.length > 0) {
    ctx.addIssue({
      code: z.ZodIssueCode.custom,
      path: ["doencas"],
      message:
        "NEGA COMORBIDADES não pode ser marcado junto com outras comorbidades",
    });
  }

  if (data.uso_coagulante_em_48h === undefined) {
    ctx.addIssue({
      code: z.ZodIssueCode.custom,
      path: ["uso_coagulante_em_48h"],
      message: "Selecione se fez uso de anticoagulante a menos de 48h",
    });
  }

  if (data.usa_outras_medicacoes === undefined) {
    ctx.addIssue({
      code: z.ZodIssueCode.custom,
      path: ["usa_outras_medicacoes"],
      message: "Informe se faz uso de outras medicações",
    });
  } else if (data.usa_outras_medicacoes && !data.outras_medicacoes_descricao?.trim()) {
    ctx.addIssue({
      code: z.ZodIssueCode.custom,
      path: ["outras_medicacoes_descricao"],
      message: "Informe quais medicações são utilizadas",
    });
  }
})