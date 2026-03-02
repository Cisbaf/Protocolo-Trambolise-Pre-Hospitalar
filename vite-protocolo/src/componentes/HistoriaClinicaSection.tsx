"use client"

import {
  Box,
  VStack,
  HStack,
  Field,
  Input,
  Textarea,
  Checkbox,
  Text,
} from "@chakra-ui/react"

export function HistoriaClinicaSection() {
  return (
    <Box>

      <VStack align="stretch" gap={6}>
        {/* Idade */}
        <Field.Root>
          <Field.Label>Idade do Paciente</Field.Label>
          <Input type="number" />
        </Field.Root>

        {/* Histórico de Doenças */}
        <Box>
          <Text fontWeight="semibold" mb={3}>
            Histórico de Doenças
          </Text>

          <HStack gap={10} align="start" flexWrap="wrap">
            <VStack align="start">
              <Checkbox.Root>
                <Checkbox.HiddenInput />
                <Checkbox.Control />
                <Checkbox.Label>Hipertensão</Checkbox.Label>
              </Checkbox.Root>

              <Checkbox.Root>
                <Checkbox.HiddenInput />
                <Checkbox.Control />
                <Checkbox.Label>Cirurgias (&lt; 3 meses)</Checkbox.Label>
              </Checkbox.Root>
            </VStack>

            <VStack align="start">
              <Checkbox.Root>
                <Checkbox.HiddenInput />
                <Checkbox.Control />
                <Checkbox.Label>Diabetes</Checkbox.Label>
              </Checkbox.Root>

              <Checkbox.Root>
                <Checkbox.HiddenInput />
                <Checkbox.Control />
                <Checkbox.Label>AVC Prévio</Checkbox.Label>
              </Checkbox.Root>
            </VStack>
          </HStack>
        </Box>

        {/* Medicamentos */}
        <Field.Root>
          <Field.Label>
            Medicamentos em Uso Contínuo / Anticoagulantes
          </Field.Label>
          <Textarea
            placeholder="Listar medicações e última dose de anticoagulante..."
            rows={4}
          />
        </Field.Root>
      </VStack>
    </Box>
  )
}