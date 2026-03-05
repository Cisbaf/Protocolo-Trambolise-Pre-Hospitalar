import { z } from "zod"

const datetimeLocalRegex =
  /^\d{4}-\d{2}-\d{2}T\d{2}:\d{2}$/

export const LinhaDoTempoSchema = z.object({
    numeroOcorrencia: z
    .string()
    .min(1, "Informe o número da ocorrência")
    .regex(/^\d+\/\d{4}$/, "Formato inválido. Use: 12345/2026"),

  municipio: z
    .string()
    .min(1, "Selecione um município"),

  aberturaChamado: z
    .string()
    .regex(datetimeLocalRegex, "Data inválida"),

  chegadaCena: z
    .string()
    .regex(datetimeLocalRegex, "Data inválida"),

  ultimoHorarioVistoBem: z
    .string()
    .regex(datetimeLocalRegex, "Data inválida")
    .optional(),

  janelaEstimada: z
    .string()
    .optional(),
})