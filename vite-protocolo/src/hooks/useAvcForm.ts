"use client"

import { useForm } from "react-hook-form"
import { zodResolver } from "@hookform/resolvers/zod"
import { AvcFormSchema, type AvcFormValues } from "../forms/formRegisterAvc/schemas/AvcFormSchema"
import { defaultAvcFormInitialValues } from "../forms/formRegisterAvc/utils/defaultInitialValues"

export function useAvcForm(
  initialValues?: Partial<AvcFormValues>
) {
  return useForm<AvcFormValues>({
    resolver: zodResolver(AvcFormSchema),
    defaultValues: initialValues? initialValues : defaultAvcFormInitialValues
  })
}