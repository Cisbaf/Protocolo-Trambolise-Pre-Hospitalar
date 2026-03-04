"use client"

import {
  Box,
  VStack,
  HStack,
  Text,
  Input,
  Separator,
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
          <strong>Nota</strong>
        </Text>
        
        <Separator mt={-4}/>

        <Text fontSize="sm" color="gray.600" >
          Reduzir Pressão Arterial somente se maior 185/110 mmHg (sob regulação médica).
        </Text>
        <Text fontSize="sm" color="gray.600" >
          Ofertar Oxigênio se Sat. menor que 94% (sob regulação médica).
        </Text>
        <Text fontSize="sm" color="gray.600" >
          Corrigir hipoglicemia se HGT menor que 60mg/dl (sob regulação médica).
        </Text>

      </VStack>
    </Box>
  )
}