import { z } from "zod"

const datetimeLocalRegex =
  /^\d{4}-\d{2}-\d{2}T\d{2}:\d{2}$/

export const UnidadeReferenciaSchema = z.object({
    unidadeReferenciaEleita: z.string().min(1, "Informe a unidade"),
    horarioNotificacaoUnidade: z
    .string()
    .regex(datetimeLocalRegex, "Data inválida"),
})
