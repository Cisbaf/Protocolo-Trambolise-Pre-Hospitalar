import { z } from "zod"

export const AvaliacaoNeurologicaSchema = z.object({
  balance: z.enum(["normal", "alterado"], "Selecione Balance"),
  eyes: z.enum(["normal", "alterado"], "Selecione Eyes"),
  desvioFacial: z.enum(["normal", "alterado"], "Selecione Desvio Facial"),
  quedaBraco: z.enum(["normal", "alterado"], "Selecione Queda do Braço"),
  falaAnormal: z.enum(["normal", "alterado"], "Selecione Fala Anormal"),
});