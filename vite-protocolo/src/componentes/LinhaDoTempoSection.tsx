"use client"

import {
  Box,
  VStack,
  Field,
  Input,
  Select,
  Portal,
  createListCollection,
} from "@chakra-ui/react"

const municipios = createListCollection({
  items: [
    { label: "BELFORD ROXO", value: "belford_roxo" },
    { label: "DUQUE DE CAXIAS", value: "duque_de_caxias" },
    { label: "ITAGUAÍ", value: "itaguai" },
    { label: "JAPERI", value: "japeri" },
    { label: "MAGÉ", value: "mage" },
    { label: "MESQUITA", value: "mesquita" },
    { label: "NILÓPOLIS", value: "nilopolis" },
    { label: "NOVA IGUAÇU", value: "nova_iguacu" },
    { label: "PARACAMBI", value: "paracambi" },
    { label: "QUEIMADOS", value: "queimados" },
    { label: "SÃO JOÃO DE MERITI", value: "sao_joao_de_meriti" },
    { label: "SEROPÉDICA", value: "seropedica" },
  ],
})

export function LinhaDoTempoSection() {
  return (
    <Box>
      <VStack gap={4}>
        <Field.Root>
          <Field.Label>Nº da Ocorrência</Field.Label>
          <Input placeholder="Ex: 12345/2026" />
        </Field.Root>

        <Field.Root>
          <Field.Label>Municipio</Field.Label>


         <Select.Root collection={municipios} size="sm" width="320px">
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
                        {municipios.items.map((municipio) => (
                        <Select.Item item={municipio} key={municipio.value}>
                            {municipio.label}
                            <Select.ItemIndicator />
                        </Select.Item>
                        ))}
                    </Select.Content>
                    </Select.Positioner>
                </Portal>
            </Select.Root>
          </Field.Root>


        <Field.Root>
          <Field.Label>Atendimento Equipe SAMU</Field.Label>
          <Input type="time" />
        </Field.Root>

        <Field.Root>
          <Field.Label>Chegada na Cena</Field.Label>
          <Input type="time" />
        </Field.Root>

        <Field.Root>
          <Field.Label>Último Horário Visto Bem (LKW)</Field.Label>
          <Input type="datetime-local" />
        </Field.Root>

        <Field.Root>
          <Field.Label>Janela Estimada (LKW até agora)</Field.Label>
          <Input placeholder="Calculado automaticamente"  />
        </Field.Root>
      </VStack>
    </Box>
  )
}