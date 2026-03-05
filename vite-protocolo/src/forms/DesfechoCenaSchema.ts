import { z } from "zod"

const datetimeLocalRegex =
  /^\d{4}-\d{2}-\d{2}T\d{2}:\d{2}$/

export const DesfechoCenaSchema = z.object({
    horarioSaidaCena: z.string()
    .regex(datetimeLocalRegex, "Data inválida"),
    horarioChegadaHospital: z
    .string()
    .regex(datetimeLocalRegex, "Data inválida"),
})
