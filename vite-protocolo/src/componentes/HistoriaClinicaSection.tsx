"use client"

import {
  Box,
  VStack,
  HStack,
  Field,
  Input,
  Checkbox,
  Text,
  Grid,
  Card,
} from "@chakra-ui/react"

export function HistoriaClinicaSection() {
  return (
    <Box>
      <VStack align="stretch" gap={8}>

        {/* ================= Idade ================= */}
        <Field.Root maxW="200px">
          <Field.Label>Idade do Paciente</Field.Label>
          <Input type="number" size="sm" />
        </Field.Root>

        {/* ================= Histórico ================= */}
        <Box>
          <Text fontWeight="semibold" mb={3}>
            Histórico de Doenças
          </Text>

          <Grid templateColumns="repeat(2, 1fr)" gap={4}>
            {[
              "Hipertensão",
              "Diabetes",
              "AVC Prévio",
              "Cirurgias (< 3 meses)",
            ].map((item) => (
              <Checkbox.Root key={item}>
                <Checkbox.HiddenInput />
                <Checkbox.Control />
                <Checkbox.Label>{item}</Checkbox.Label>
              </Checkbox.Root>
            ))}
          </Grid>
        </Box>

        {/* ================= Anticoagulantes ================= */}
        <Box>
          <Text fontWeight="semibold" mb={4}>
            Anticoagulantes em Uso
          </Text>

          <Grid templateColumns={{ base: "1fr", md: "1fr 1fr" }} gap={6}>

            {/* Injetáveis */}
            <Card.Root variant="outline" size="sm">
              <Card.Header bg="green.100">
                <Text fontWeight="semibold" color="green.800">
                  Anticoagulantes Injetáveis
                </Text>
              </Card.Header>

              <Card.Body>
                <VStack align="start" gap={2}>
                  {[
                    "Dalteparina (Fragmin®)",
                    "Enoxaparina (Lovenox®)",
                    "Tinzaparina (Innohep®)",
                    "Fondaparinux (Arixtra®)",
                    "Heparina",
                    "Nadroparina (Fraxiparina®)",
                  ].map((med) => (
                    <Checkbox.Root key={med}>
                      <Checkbox.HiddenInput />
                      <Checkbox.Control />
                      <Checkbox.Label>{med}</Checkbox.Label>
                    </Checkbox.Root>
                  ))}
                </VStack>
              </Card.Body>
            </Card.Root>

            {/* Orais */}
            <Card.Root variant="outline" size="sm">
              <Card.Header bg="blue.100">
                <Text fontWeight="semibold" color="blue.800">
                  Anticoagulantes Orais
                </Text>
              </Card.Header>

              <Card.Body>
                <VStack align="start" gap={2}>
                  {[
                    "Varfarina (Varfine®)",
                    "Acenocumarol (Sintrom®)",
                    "Fenindiona",
                    "Dabigatrana (Pradaxa®)",
                    "Rivaroxabana (Xarelto®)",
                    "Apixabana (Eliquis®)",
                    "Edoxabana (Lixiana®)",
                  ].map((med) => (
                    <Checkbox.Root key={med}>
                      <Checkbox.HiddenInput />
                      <Checkbox.Control />
                      <Checkbox.Label>{med}</Checkbox.Label>
                    </Checkbox.Root>
                  ))}
                </VStack>
              </Card.Body>
            </Card.Root>

          </Grid>
        </Box>

      </VStack>
    </Box>
  )
}