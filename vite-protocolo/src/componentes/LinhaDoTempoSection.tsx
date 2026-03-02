"use client"

import {
  Box,
  VStack,
  Field,
  Input,
} from "@chakra-ui/react"

export function LinhaDoTempoSection() {
  return (
    <Box>
      <VStack gap={4}>
        <Field.Root>
          <Field.Label>Nº da Ocorrência</Field.Label>
          <Input placeholder="Ex: 12345/2026" />
        </Field.Root>

        <Field.Root>
          <Field.Label>Atendimento Equipe SAMU</Field.Label>
          <Input type="time" />
        </Field.Root>

        <Field.Root>
          <Field.Label>Chegada na Cena</Field.Label>
          <Input type="time" />
        </Field.Root>

        <Field.Root>
          <Field.Label>Último Horário Visto Bem (LKW)</Field.Label>
          <Input type="datetime-local" />
        </Field.Root>

        <Field.Root>
          <Field.Label>Janela Estimada (LKW até agora)</Field.Label>
          <Input placeholder="Calculado automaticamente"  />
        </Field.Root>
      </VStack>
    </Box>
  )
}