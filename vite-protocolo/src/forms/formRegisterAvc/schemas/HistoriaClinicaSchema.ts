import { z } from "zod"
import { lista_doencas } from "../utils/labels"


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