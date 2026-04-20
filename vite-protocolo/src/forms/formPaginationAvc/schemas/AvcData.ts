import { z } from "zod"
import { AvcFormSchema } from "../../formRegisterAvc/schemas/AvcFormSchema"

const datetimeLocalRegex =
  /^\d{4}-\d{2}-\d{2}T\d{2}:\d{2}$/

export const AvcDataSchema = AvcFormSchema.extend({
  id: z.string(),
  dataCriacao: z.string()
    .regex(datetimeLocalRegex, "Data inválida"),
})

export type AvcDataValues = z.infer<typeof AvcDataSchema>