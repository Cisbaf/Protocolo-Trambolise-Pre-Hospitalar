"use client"

import {
  Box,
  VStack,
  Field,
  Input,
  Checkbox,
  Text,
  Grid,
  Card,
  Flex,
  CheckboxCard,
} from "@chakra-ui/react"
import React from "react"

export function HistoriaClinicaSection() {
  const [negaUso, setNegaUso] = React.useState(false);
  const [selectedMeds, setSelectedMeds] = React.useState<string[]>([]);


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
              "AVC Prévio (< 3 meses)",
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


        <Flex gap={10} >
          
          {/* ================= Anticoagulantes ================= */}
          <Box>
            <Flex gap={5} alignItems={"center"} mb={5}>
              <Text fontWeight="semibold" mb={4}>
                Anticoagulantes em Uso
              </Text>

              <CheckboxCard.Root
                  maxW="150px"
                  size="sm"
                  onChange={() => {
                    const novoValor = !negaUso;
                    setNegaUso(novoValor);

                    if (novoValor) {
                      setSelectedMeds([]); // limpa todos
                    }
                  }}
                  colorPalette="teal"
                >
                <CheckboxCard.HiddenInput />
                <CheckboxCard.Control>
                  <CheckboxCard.Label>Nega Uso</CheckboxCard.Label>
                  <CheckboxCard.Indicator />
                </CheckboxCard.Control>
              </CheckboxCard.Root>

            </Flex>

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
                      <Checkbox.Root
                          key={med}
                          disabled={negaUso}
                          checked={selectedMeds.includes(med)}
                          onCheckedChange={(e) => {
                            if (e.checked) {
                              setSelectedMeds(prev => [...prev, med]);
                            } else {
                              setSelectedMeds(prev => prev.filter(m => m !== med));
                            }
                          }}
                        >
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
                      <Checkbox.Root
                        key={med}
                        disabled={negaUso}
                        checked={selectedMeds.includes(med)}
                        onCheckedChange={(e) => {
                          if (e.checked) {
                            setSelectedMeds(prev => [...prev, med]);
                          } else {
                            setSelectedMeds(prev => prev.filter(m => m !== med));
                          }
                        }}
                      >
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

          {/* ================= Outros ================= */}
          <Box mt={16}>

            <Grid templateColumns={{ base: "1fr", md: "1fr" }} gap={6}>

              {/* Injetáveis */}
              <Card.Root variant="outline" size="sm">
                <Card.Header bg="yellow.100">
                  <Text fontWeight="semibold" color="yellow.800">
                    Outros Medicamentos
                  </Text>
                </Card.Header>

                <Card.Body>
                  <VStack align="start" gap={2}>
                    {[
                      "ASS",
                      "Clopidogrel"
                    ].map((med) => (
                      <Checkbox.Root
                        key={med}
                        disabled={negaUso}
                        checked={selectedMeds.includes(med)}
                        onCheckedChange={(e) => {
                          if (e.checked) {
                            setSelectedMeds(prev => [...prev, med]);
                          } else {
                            setSelectedMeds(prev => prev.filter(m => m !== med));
                          }
                        }}
                      >
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

        </Flex>

      </VStack>
    </Box>
  )
}