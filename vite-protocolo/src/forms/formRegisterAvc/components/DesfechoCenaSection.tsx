"use client"

import {
  Box,
  Grid,
  Text,
  Input,
  Field,
} from "@chakra-ui/react"
import { useAvcFormContext } from "../../../context/FormAvcContext"


export function DesfechoCenaSection() {
  const { form } = useAvcFormContext()
  
  const {
    formState: { errors },
  } = form

  return (
    <Box w="100%">
      <Grid
        templateColumns={{
          base: "1fr",
          md: "240px 1fr",
        }}
        gap={4}
        alignItems="start"
        mb={8}
        w="100%"
      >
        {/* ================= Saída da Cena ================= */}
        <Text fontWeight="medium">
          Horário de Saída da Cena
        </Text>

        <Field.Root
          w="100%"
          invalid={
            !!errors.DesfechoCenaSection?.horarioSaidaCena
          }
        >
          <Input
            type="datetime-local"
            size="sm"
            w="100%"
            {...form.register(
              "DesfechoCenaSection.horarioSaidaCena"
            )}
          />
          <Field.ErrorText>
            {
              errors.DesfechoCenaSection
                ?.horarioSaidaCena?.message
            }
          </Field.ErrorText>
        </Field.Root>

        {/* ================= Chegada Hospital ================= */}
        <Text fontWeight="medium">
          Horário de Chegada ao Hospital
        </Text>

        <Field.Root
          w="100%"
          invalid={
            !!errors.DesfechoCenaSection
              ?.horarioChegadaHospital
          }
        >
          <Input
            type="datetime-local"
            size="sm"
            w="100%"
            {...form.register(
              "DesfechoCenaSection.horarioChegadaHospital"
            )}
          />
          <Field.ErrorText>
            {
              errors.DesfechoCenaSection
                ?.horarioChegadaHospital?.message
            }
          </Field.ErrorText>
        </Field.Root>
      </Grid>
    </Box>
  )
}