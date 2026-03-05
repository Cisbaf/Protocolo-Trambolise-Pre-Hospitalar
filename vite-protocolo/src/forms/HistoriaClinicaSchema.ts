import { z } from "zod"

export const HistoriaClinicaSchema = z.object({
  idade: z
    .number({ message: "Informe a idade do paciente" })
    .positive("Idade deve ser um número positivo"),

  doencas: z.object({
    "Hipertensão": z.boolean(),
    "Diabetes": z.boolean(),
    "AVC Prévio (< 3 meses)": z.boolean(),
    "Cirurgias (< 3 meses)": z.boolean(),
  }),

  medicamentos: z
    .array(z.string())
    .min(0, "Selecione pelo menos um medicamento") // pode ser mínimo 0 se não obrigatório
})