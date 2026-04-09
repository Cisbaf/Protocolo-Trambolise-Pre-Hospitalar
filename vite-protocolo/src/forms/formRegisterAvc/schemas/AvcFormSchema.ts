import { z } from "zod"
import { LinhaDoTempoSchema } from "./LinhaDoTempoSchema";
import { AvaliacaoNeurologicaSchema } from "./AvaliacaoNeurologicaSchema";
import { ParametrosClinicosSchema } from "./ParametrosClinicosSchema";
import { HistoriaClinicaSchema } from "./HistoriaClinicaSchema";
import { UnidadeReferenciaSchema } from "./UnidadeReferenciaSchema";
import { DesfechoCenaSchema } from "./DesfechoCenaSchema";
import { ParecerFinalSchema } from "./ParecerFinalSchema";


export const AvcFormSchema = z.object({
  LinhaDoTempoSection: LinhaDoTempoSchema,
  AvaliacaoNeurologicaSection: AvaliacaoNeurologicaSchema,
  ParametrosClinicosSection: ParametrosClinicosSchema,
  HistoriaClinicaSection: HistoriaClinicaSchema,
  UnidadeReferenciaSection: UnidadeReferenciaSchema,
  DesfechoCenaSection: DesfechoCenaSchema,
  ParecerFinalSection: ParecerFinalSchema
})

export type AvcFormValues = z.infer<typeof AvcFormSchema>