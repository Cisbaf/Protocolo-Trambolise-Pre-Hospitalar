"use client"

import { useForm } from "react-hook-form"
import { zodResolver } from "@hookform/resolvers/zod"
import { AvcDataPaginationSchema, type AvcDataPaginationValues } from "../forms/formPaginationAvc/schemas/AvcDataPagination"


export function useAvcDataPaginationForm(
  initialValues?: Partial<AvcDataPaginationValues>
) {
  return useForm<AvcDataPaginationValues>({
    resolver: zodResolver(AvcDataPaginationSchema),
    defaultValues: initialValues? initialValues : { number: 0, size: 5, content: [] }
  })
}