import { z } from "zod";

// Regex para validar PA no formato 120/80
const paRegex = /^\d{2,3}\/\d{2,3}$/;

export const ParametrosClinicosSchema = z.object({
  glicemia: z
    .number()
    .min(0, { message: "Glicemia não pode ser negativa" }),

  pressaoArterial: z
    .string()
    .regex(paRegex, { message: "Formato inválido de PA. Use Ex: 120/80" }),

  saturacao: z
    .number()
    .min(0, { message: "Saturação não pode ser negativa" })
    .max(100, { message: "Saturação não pode ser maior que 100" }),
});