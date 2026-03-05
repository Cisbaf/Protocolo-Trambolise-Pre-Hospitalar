import { z } from "zod"
import { lista_doencas } from "../utils/labels"

export const HistoriaClinicaSchema = z.object({
  idade: z
    .number({ message: "Informe a idade do paciente" })
    .positive("Idade deve ser um número positivo"),
  doencas: z.object(
   Object.fromEntries(
    lista_doencas.map((d) => [d, z.boolean()])
    ) as Record<(typeof lista_doencas)[number], z.ZodBoolean>
  ),
  medicamentos: z
    .array(z.string())
    .min(0, "Selecione pelo menos um medicamento") // pode ser mínimo 0 se não obrigatório
})