"use client"

import {
  Box,
  Grid,
  Text,
  Input,
  Select,
  Portal,
  createListCollection,
  Field,
} from "@chakra-ui/react"
import { Controller } from "react-hook-form"
import { useAvcFormContext } from "../../../context/AvcFormContext"

export function UnidadeReferenciaSection() {
  const { form } = useAvcFormContext()
  const {
    formState: { errors },
  } = form

  const unidades = createListCollection({
    items: [
      {
        label: "Hospital Municipal São João de Meriti",
        value: "Hospital Municipal São João de Meriti",
      },
      {
        label: "Hospital Geral de Nova Iguaçu",
        value: "Hospital Geral de Nova Iguaçu",
      },
      {
        label: "Hospital Municipalizado Adão Pereira Nunes",
        value: "Hospital Municipalizado Adão Pereira Nunes",
      },
      {
        label: "Hospital MRJ (SUBPAV)",
        value: "Hospital MRJ (SUBPAV)",
      },
      { label: "Upa", value: "Upa" },
      {
        label: "Outros Hospitais não elegíveis trombólise",
        value: "Outros Hospitais não elegíveis trombólise",
      },
    ],
  })

  return (
    <Box w="100%">
      <Grid
        templateColumns={{
          base: "1fr",
          md: "240px 1fr",
        }}
        gap={4}
        alignItems="start"
        w="100%"
      >
        {/* ================= Unidade ================= */}
        <Text fontWeight="medium">
          Unidade de Referência Eleita
        </Text>

        <Field.Root
          w="100%"
          invalid={
            !!errors.UnidadeReferenciaSection
              ?.unidadeReferenciaEleita
          }
        >
          <Controller
            name="UnidadeReferenciaSection.unidadeReferenciaEleita"
            control={form.control}
            render={({ field }) => (
              <Select.Root
                collection={unidades}
                size="sm"
                w="100%"
                minW={0}
                value={field.value ? [field.value] : []}
                onValueChange={(details) =>
                  field.onChange(details.value[0])
                }
                invalid={
                  !!errors.UnidadeReferenciaSection
                    ?.unidadeReferenciaEleita
                }
              >
                <Select.HiddenSelect />

                <Select.Control>
                  <Select.Trigger>
                    <Select.ValueText placeholder="Selecione a Unidade..." />
                  </Select.Trigger>
                  <Select.IndicatorGroup>
                    <Select.Indicator />
                  </Select.IndicatorGroup>
                </Select.Control>

                <Portal>
                  <Select.Positioner>
                    <Select.Content>
                      {unidades.items.map((unidade) => (
                        <Select.Item
                          item={unidade}
                          key={unidade.value}
                        >
                          {unidade.label}
                          <Select.ItemIndicator />
                        </Select.Item>
                      ))}
                    </Select.Content>
                  </Select.Positioner>
                </Portal>
              </Select.Root>
            )}
          />
          <Field.ErrorText>
            {
              errors.UnidadeReferenciaSection
                ?.unidadeReferenciaEleita?.message
            }
          </Field.ErrorText>
        </Field.Root>

        {/* ================= Horário ================= */}
        <Text fontWeight="medium">
          Horário da Notificação à Unidade
        </Text>

        <Field.Root
          w="100%"
          invalid={
            !!errors.UnidadeReferenciaSection
              ?.horarioNotificacaoUnidade
          }
        >
          <Input
            type="datetime-local"
            size="sm"
            w="100%"
            {...form.register(
              "UnidadeReferenciaSection.horarioNotificacaoUnidade"
            )}
          />
          <Field.ErrorText>
            {
              errors.UnidadeReferenciaSection
                ?.horarioNotificacaoUnidade?.message
            }
          </Field.ErrorText>
        </Field.Root>
      </Grid>
    </Box>
  )
}