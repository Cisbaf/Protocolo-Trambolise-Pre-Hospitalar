"use client"

import {
  Box,
  VStack,
  HStack,
  Text,
  Input,
} from "@chakra-ui/react"

export function ParametrosClinicosSection() {
  return (
    <Box>

      <VStack align="stretch" gap={4}>
        {/* Glicemia */}
        <HStack>
          <Text minW="220px" fontWeight="medium">
            Glicemia Capilar (mg/dL)
          </Text>
          <Input placeholder="HGT" type="number" />
        </HStack>

        {/* Pressão Arterial */}
        <HStack>
          <Text minW="220px" fontWeight="medium">
            Pressão Arterial (PA)
          </Text>
          <Input placeholder="Ex: 190/110" />
        </HStack>

        {/* Saturação */}
        <HStack>
          <Text minW="220px" fontWeight="medium">
            Saturação de O₂ (%)
          </Text>
          <Input placeholder="Sat." type="number" />
        </HStack>

        {/* Nota */}
        <Text fontSize="sm" color="gray.600" mt={2}>
          <strong>Nota:</strong> Alvo PA &lt; 185/110 mmHg. Glicemia entre 60 e 400 mg/dL.
        </Text>
      </VStack>
    </Box>
  )
}