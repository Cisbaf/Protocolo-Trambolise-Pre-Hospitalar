"use client"

import {
  Box,
  Grid,
  Text,
  Input,
  Field,
} from "@chakra-ui/react"

import { useGroupContext } from "../context/GroupContext"

export function DesfechoCenaSection() {
  const { form } = useGroupContext();
  const { formState: { errors } } = form;

  return (
    <Box>
      {/* Horários */}
      <Grid
        templateColumns="260px 1fr"
        gap={4}
        alignItems="start"
        mb={8}
      >
        <Text fontWeight="medium">
          Horário de Saída da Cena
        </Text>

        <Field.Root 
          invalid={!!errors.DesfechoCenaSection?.horarioSaidaCena}
          width="200px"
        >
          <Input 
            type="datetime-local" 
            size="sm" 
            {...form.register("DesfechoCenaSection.horarioSaidaCena")}
          />
          <Field.ErrorText>
            {errors.DesfechoCenaSection?.horarioSaidaCena?.message}
          </Field.ErrorText>
        </Field.Root>

        <Text fontWeight="medium">
          Horário de Chegada ao Hospital
        </Text>

        <Field.Root 
          invalid={!!errors.DesfechoCenaSection?.horarioChegadaHospital}
          width="200px"
        >
          <Input 
            type="datetime-local" 
            size="sm" 
            {...form.register("DesfechoCenaSection.horarioChegadaHospital")}
          />
          <Field.ErrorText>
            {errors.DesfechoCenaSection?.horarioChegadaHospital?.message}
          </Field.ErrorText>
        </Field.Root>
      </Grid>

    </Box>
  )
}