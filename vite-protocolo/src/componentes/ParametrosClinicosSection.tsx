"use client"

import {
  Box,
  VStack,
  Text,
  Input,
  Separator,
  Field,
  SimpleGrid,
} from "@chakra-ui/react"
import { useGroupContext } from "../context/GroupContext"

export function ParametrosClinicosSection() {
  const { form } = useGroupContext()

  return (
    <Box>
      <VStack align="stretch" gap={4}>
        
        {/* Grid Responsivo */}
        <SimpleGrid columns={{ base: 1, md: 3 }} gap={4}>
          
          {/* Glicemia */}
          <Field.Root
            invalid={!!form.formState.errors.ParametrosClinicosSection?.glicemia}
          >
            <Field.Label>Glicemia Capilar (mg/dL)</Field.Label>
            <Input
              type="number"
              placeholder="HGT"
              {...form.register("ParametrosClinicosSection.glicemia", {
                valueAsNumber: true,
              })}
            />
            <Field.ErrorText>
              {form.formState.errors.ParametrosClinicosSection?.glicemia?.message}
            </Field.ErrorText>
          </Field.Root>

          {/* Pressão Arterial */}
          <Field.Root
            invalid={!!form.formState.errors.ParametrosClinicosSection?.pressaoArterial}
          >
            <Field.Label>Pressão Arterial (PA)</Field.Label>
            <Input
              placeholder="Ex: 190/110"
              {...form.register("ParametrosClinicosSection.pressaoArterial")}
            />
            <Field.ErrorText>
              {form.formState.errors.ParametrosClinicosSection?.pressaoArterial?.message}
            </Field.ErrorText>
          </Field.Root>

          {/* Saturação */}
          <Field.Root
            invalid={!!form.formState.errors.ParametrosClinicosSection?.saturacao}
          >
            <Field.Label>Saturação de O₂ (%)</Field.Label>
            <Input
              type="number"
              placeholder="Sat."
              {...form.register("ParametrosClinicosSection.saturacao", {
                valueAsNumber: true,
              })}
            />
            <Field.ErrorText>
              {form.formState.errors.ParametrosClinicosSection?.saturacao?.message}
            </Field.ErrorText>
          </Field.Root>
        </SimpleGrid>

        {/* Nota */}
        <Text fontSize="sm" color="gray.600" mt={2}>
          <strong>Nota</strong>
        </Text>

        <Separator mt={-4} />

        <Text fontSize="sm" color="gray.600">
          Reduzir Pressão Arterial somente se maior 185/110 mmHg (sob regulação médica).
        </Text>
        <Text fontSize="sm" color="gray.600">
          Ofertar Oxigênio se Sat. menor que 94% (sob regulação médica).
        </Text>
        <Text fontSize="sm" color="gray.600">
          Corrigir hipoglicemia se HGT menor que 60mg/dl (sob regulação médica).
        </Text>
      </VStack>
    </Box>
  )
}