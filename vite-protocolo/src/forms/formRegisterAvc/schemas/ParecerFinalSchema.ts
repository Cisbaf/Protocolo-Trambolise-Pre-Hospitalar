import { z } from "zod"

export const ParecerFinalSchema = z.object({
  elegibilidade: z.enum(["elegivel", "inelegivel"]).optional(),
  motivos: z
    .array(z.string()).optional()
})