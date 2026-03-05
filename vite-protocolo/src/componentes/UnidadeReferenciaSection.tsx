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
import { useGroupContext } from "../context/GroupContext"

export function UnidadeReferenciaSection() {
  const { form } = useGroupContext();
  const { formState: { errors } } = form;

  const unidades = createListCollection({
    items: [
      { label: "Hospital Municipal São João de Meriti", value: "Hospital Municipal São João de Meriti" },
      { label: "Hospital Geral de Nova Iguaçu", value: "Hospital Geral de Nova Iguaçu" },
      { label: "Hospital Municipalizado Adão Pereira Nunes", value: "Hospital Municipalizado Adão Pereira Nunes" },
      { label: "Hospital MRJ (SUBPAV)", value: "Hospital MRJ (SUBPAV)"},
      { label: "Upa", value: "Upa"},
      { label: "Outros Hospitais não elegíveis trombólise", value: "Outros Hospitais não elegíveis trombólise"}
    ],
  })

  return (
    <Box>
      <Grid
        templateColumns="260px 1fr"
        gap={4}
        alignItems="start"
      >
        <Text fontWeight="medium">
          Unidade de Referência Eleita
        </Text>

        <Field.Root 
          invalid={!!errors.UnidadeReferenciaSection?.unidadeReferenciaEleita}
        >
          <Controller
            name="UnidadeReferenciaSection.unidadeReferenciaEleita"
            control={form.control}
            render={({ field }) => (
              <Select.Root
                collection={unidades}
                size="sm"
                value={field.value ? [field.value] : []}
                onValueChange={(details) => {
                  field.onChange(details.value[0])
                }}
                width="320px"
                invalid={!!errors.UnidadeReferenciaSection?.unidadeReferenciaEleita}
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
            {errors.UnidadeReferenciaSection?.unidadeReferenciaEleita?.message}
          </Field.ErrorText>
        </Field.Root>

        <Text fontWeight="medium">
          Horário da Notificação à Unidade
        </Text>

        <Field.Root 
          invalid={!!errors.UnidadeReferenciaSection?.horarioNotificacaoUnidade}
        >
          <Input 
            type="datetime-local" 
            width="200px" 
            size="sm" 
            {...form.register("UnidadeReferenciaSection.horarioNotificacaoUnidade")}
          />
          <Field.ErrorText>
            {errors.UnidadeReferenciaSection?.horarioNotificacaoUnidade?.message}
          </Field.ErrorText>
        </Field.Root>
      </Grid>
    </Box>
  )
}