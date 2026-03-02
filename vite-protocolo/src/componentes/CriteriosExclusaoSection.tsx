"use client"

import {
  Box,
  VStack,
  Checkbox,
} from "@chakra-ui/react"

export function CriteriosExclusaoSection() {
  return (
    <Box>
      <Box
        border="1px solid"
        borderColor="gray.200"
        borderRadius="md"
        p={6}
      >
        <VStack align="start" gap={4}>
          <Checkbox.Root>
            <Checkbox.HiddenInput />
            <Checkbox.Control />
            <Checkbox.Label>
              Déficit neurológico leve e não incapacitante (NIHSS &lt; 4)?
            </Checkbox.Label>
          </Checkbox.Root>

          <Checkbox.Root>
            <Checkbox.HiddenInput />
            <Checkbox.Control />
            <Checkbox.Label>
              Evidência de Hemorragia em TC de Crânio (se disponível no local)?
            </Checkbox.Label>
          </Checkbox.Root>

          <Checkbox.Root>
            <Checkbox.HiddenInput />
            <Checkbox.Control />
            <Checkbox.Label>
              Melhora rápida e completa dos sintomas antes da avaliação?
            </Checkbox.Label>
          </Checkbox.Root>
        </VStack>
      </Box>
    </Box>
  )
}