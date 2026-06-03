import { z } from "zod"

const datetimeLocalRegex =
  /^\d{4}-\d{2}-\d{2}T\d{2}:\d{2}$/

export const LinhaDoTempoSchema = z.object({
  numeroOcorrencia: z
    .string()
    .min(1, "Informe o número da ocorrência")
    .regex(/^\d{7}\/\d$/, "Formato inválido. Use: 0000000/0"),

  municipio: z
    .string()
    .min(1, "Selecione um município"),

  aberturaChamado: z
    .string()
    .regex(datetimeLocalRegex, "Data inválida"),

  chegadaCena: z
    .string()
    .regex(datetimeLocalRegex, "Data inválida"),

  naoSoubeInformarLKW: z.boolean().optional(),

  ultimoHorarioVistoBem: z
    .union([
      z.literal(""), // aceita vazio
      z.string().regex(datetimeLocalRegex, "Data inválida"),
    ]),

  janelaEstimada: z
    .string()
    .optional(),
    
}).superRefine((data, ctx) => {
  const { ultimoHorarioVistoBem, naoSoubeInformarLKW } = data;

  // Se NÃO marcou → obrigatório
  if (!naoSoubeInformarLKW && ultimoHorarioVistoBem === "") {
    ctx.addIssue({
      path: ["ultimoHorarioVistoBem"],
      code: z.ZodIssueCode.custom,
      message: "Informe o horário ou marque 'Não soube informar'",
    });
  }
});