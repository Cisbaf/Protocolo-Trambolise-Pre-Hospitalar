import { z } from "zod";

export const ParametrosClinicosSchema = z.object({
  glicemia: z
    .number()
    .min(0, { message: "Glicemia não pode ser negativa" }),

  pressaoArterial: z
    .string()
    .refine((value) => {
      const [sis, dias] = value.split("/").map(Number);

      return (
        !isNaN(sis) &&
        !isNaN(dias) &&
        sis >= 40 &&
        sis <= 300 &&
        dias >= 20 &&
        dias <= 200
      );
    }, {
      message: "Pressão arterial inválida",
    }),

  saturacao: z
    .number()
    .min(0, { message: "Saturação não pode ser negativa" })
    .max(100, { message: "Saturação não pode ser maior que 100" }),
});