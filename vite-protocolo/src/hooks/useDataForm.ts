"use client"

import { useForm } from "react-hook-form"
import { zodResolver } from "@hookform/resolvers/zod"
import { DataFormSchema } from "../forms/DataForm"
import { z } from "zod"

export type DataFormValues = z.infer<typeof DataFormSchema>

export function useDataForm(
  initialValues?: Partial<DataFormValues>
) {
  return useForm<DataFormValues>({
    resolver: zodResolver(DataFormSchema),

    defaultValues: {
      LinhaDoTempoSection: {
        numeroOcorrencia: "",
        municipio: "",
        atendimentoSamu: "",
        chegadaCena: "",
        ultimoHorarioVistoBem: "",
        janelaEstimada: "",
        ...initialValues?.LinhaDoTempoSection,
      },
      HistoriaClinicaSection: {
        medicamentos: [],
      }
    },
  })
}