"use client"

import {
  Box,
  Table,
  Select,
  Field,
  createListCollection,
  Portal,
  Stack,
  Text,
  useBreakpointValue,
} from "@chakra-ui/react"
import { respostasAvaliacoes, testesNeurologicos } from "../utils/testeNeurologicos"
import { Controller } from "react-hook-form";
import { useAvcFormContext } from "../../../context/AvcFormContext";

export function AvaliacaoNeurologicaSection() {
  const { form } = useAvcFormContext()

  const isMobile = useBreakpointValue({ base: true, md: false })

  /* ===============================
     MOBILE → CARD LAYOUT
  =============================== */
  if (isMobile) {
    return (
      <Stack gap={4}>
        {testesNeurologicos.map((teste) => (
          <Box
            key={teste.key}
            p={4}
            borderWidth="1px"
            borderRadius="lg"
            bg="white"
            shadow="sm"
          >
            <Stack gap={2}>
              <Text fontWeight="bold">{teste.nome}</Text>
              <Text fontSize="sm" color="gray.600">
                {teste.descricao}
              </Text>

              <Controller
                control={form.control}
                name={`AvaliacaoNeurologicaSection.${teste.key}`}
                render={({ field, fieldState }) => (
                  <Field.Root invalid={!!fieldState.error}>
                    <Select.Root
                      collection={createListCollection(respostasAvaliacoes)}
                      size="sm"
                      w="100%"
                      value={field.value ? [field.value] : []}
                      onValueChange={(val) =>
                        field.onChange(val.value[0])
                      }
                    >
                      <Select.HiddenSelect />

                      <Select.Control>
                        <Select.Trigger>
                          <Select.ValueText placeholder="Selecione" />
                        </Select.Trigger>
                        <Select.IndicatorGroup>
                          <Select.Indicator />
                        </Select.IndicatorGroup>
                      </Select.Control>

                      <Portal>
                        <Select.Positioner>
                          <Select.Content>
                            {respostasAvaliacoes.items.map((avaliacao) => (
                              <Select.Item
                                item={avaliacao}
                                key={avaliacao.value}
                              >
                                {avaliacao.label}
                                <Select.ItemIndicator />
                              </Select.Item>
                            ))}
                          </Select.Content>
                        </Select.Positioner>
                      </Portal>
                    </Select.Root>

                    {fieldState.error && (
                      <Field.ErrorText>
                        {fieldState.error.message}
                      </Field.ErrorText>
                    )}
                  </Field.Root>
                )}
              />
            </Stack>
          </Box>
        ))}
      </Stack>
    )
  }

  /* ===============================
     DESKTOP → TABLE LAYOUT
  =============================== */
  return (
    <Box w="100%" overflowX="auto">
      <Table.Root variant="outline">
        <Table.Header>
          <Table.Row>
            <Table.ColumnHeader>Teste</Table.ColumnHeader>
            <Table.ColumnHeader>Avaliação</Table.ColumnHeader>
            <Table.ColumnHeader>Achado</Table.ColumnHeader>
          </Table.Row>
        </Table.Header>

        <Table.Body>
          {testesNeurologicos.map((teste) => (
            <Table.Row key={teste.key}>
              <Table.Cell>{teste.nome}</Table.Cell>
              <Table.Cell>{teste.descricao}</Table.Cell>

              <Table.Cell>
                <Controller
                  control={form.control}
                  name={`AvaliacaoNeurologicaSection.${teste.key}`}
                  render={({ field, fieldState }) => (
                    <Field.Root invalid={!!fieldState.error}>
                      <Select.Root
                        collection={createListCollection(respostasAvaliacoes)}
                        size="sm"
                        w="100%"
                        minW={0}
                        value={field.value ? [field.value] : []}
                        onValueChange={(val) =>
                          field.onChange(val.value[0])
                        }
                      >
                        <Select.HiddenSelect />

                        <Select.Control>
                          <Select.Trigger>
                            <Select.ValueText placeholder="Selecione" />
                          </Select.Trigger>
                          <Select.IndicatorGroup>
                            <Select.Indicator />
                          </Select.IndicatorGroup>
                        </Select.Control>

                        <Portal>
                          <Select.Positioner>
                            <Select.Content>
                              {respostasAvaliacoes.items.map((avaliacao) => (
                                <Select.Item
                                  item={avaliacao}
                                  key={avaliacao.value}
                                >
                                  {avaliacao.label}
                                  <Select.ItemIndicator />
                                </Select.Item>
                              ))}
                            </Select.Content>
                          </Select.Positioner>
                        </Portal>
                      </Select.Root>

                      {fieldState.error && (
                        <Field.ErrorText>
                          {fieldState.error.message}
                        </Field.ErrorText>
                      )}
                    </Field.Root>
                  )}
                />
              </Table.Cell>
            </Table.Row>
          ))}
        </Table.Body>
      </Table.Root>
    </Box>
  )
}