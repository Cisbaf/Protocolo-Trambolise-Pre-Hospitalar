"use client"

import {
  Box,
  Grid,
  Text,
  Input,
  Select,
  Portal,
  createListCollection,
} from "@chakra-ui/react"

export function UnidadeReferenciaSection() {

  const unidades = createListCollection({
    items: [
      { label: "Hospital Municipal", value: "h1" },
      { label: "Hospital Regional", value: "h2" },
      { label: "Unidade AVC Referência", value: "h3" },
    ],
  })

  return (
    <Box>
      <Grid
        templateColumns="260px 1fr"
        gap={4}
        alignItems="center"
      >
        <Text fontWeight="medium">
          Unidade de Referência Eleita
        </Text>

        <Select.Root
          collection={unidades}
          size="sm"
          width="320px"
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

        <Text fontWeight="medium">
          Horário da Notificação à Unidade
        </Text>

        <Input type="time" width="200px" size="sm" />
      </Grid>
    </Box>
  )
}