import { z } from "zod"
import { AvcDataSchema } from "./AvcData"


export const AvcDataPaginationSchema = z.object({
    number: z.number(),
    size: z.number(),
    totalElements: z.number(),
    totalPages: z.number(),
    content: z.array(AvcDataSchema)
})

export type AvcDataPaginationValues = z.infer<typeof AvcDataPaginationSchema>