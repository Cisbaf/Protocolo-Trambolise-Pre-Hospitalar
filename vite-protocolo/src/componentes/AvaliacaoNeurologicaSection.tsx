"use client"

import {
  Box,
  Table,
  Select,
  createListCollection,
    Portal
} from "@chakra-ui/react"

const avaliacoes = createListCollection({
    items: [
        { label: "Normal", value: "normal"},
        { label: "Alterado", value: "alterado"},
    ]
  })

export function AvaliacaoNeurologicaSection() {
  return (
    <Box>
      <Table.Root variant="outline">
        <Table.Header>
          <Table.Row>
            <Table.ColumnHeader>Teste</Table.ColumnHeader>
            <Table.ColumnHeader>Avaliação</Table.ColumnHeader>
            <Table.ColumnHeader>Achado (Normal/Alterado)</Table.ColumnHeader>
          </Table.Row>
        </Table.Header>

        <Table.Body>

          <Table.Row>
            <Table.Cell>Balance (Equilíbrio)</Table.Cell>
            <Table.Cell>
              Observe se há dificuldade para manter-se em pé ou andar"
            </Table.Cell>
            <Table.Cell>
                <Select.Root collection={avaliacoes} size="sm" width="320px">
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
                            {avaliacoes.items.map((avaliacao) => (
                            <Select.Item item={avaliacao} key={avaliacao.value}>
                                {avaliacao.label}
                                <Select.ItemIndicator />
                            </Select.Item>
                            ))}
                        </Select.Content>
                        </Select.Positioner>
                    </Portal>
                </Select.Root>
            </Table.Cell>
          </Table.Row>


          <Table.Row>
            <Table.Cell>Eyes (Visão)</Table.Cell>
            <Table.Cell>
              Pergunte se há visão dupla, perda visual ou alteração súbita"
            </Table.Cell>
            <Table.Cell>
                <Select.Root collection={avaliacoes} size="sm" width="320px">
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
                            {avaliacoes.items.map((avaliacao) => (
                            <Select.Item item={avaliacao} key={avaliacao.value}>
                                {avaliacao.label}
                                <Select.ItemIndicator />
                            </Select.Item>
                            ))}
                        </Select.Content>
                        </Select.Positioner>
                    </Portal>
                </Select.Root>
            </Table.Cell>
          </Table.Row>

          <Table.Row>
            <Table.Cell>Desvio Facial</Table.Cell>
            <Table.Cell>
              Peça para sorrir ou mostrar dentes
            </Table.Cell>
            <Table.Cell>
                <Select.Root collection={avaliacoes} size="sm" width="320px">
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
                            {avaliacoes.items.map((avaliacao) => (
                            <Select.Item item={avaliacao} key={avaliacao.value}>
                                {avaliacao.label}
                                <Select.ItemIndicator />
                            </Select.Item>
                            ))}
                        </Select.Content>
                        </Select.Positioner>
                    </Portal>
                </Select.Root>
            </Table.Cell>
          </Table.Row>

          <Table.Row>
            <Table.Cell>Queda do Braço</Table.Cell>
            <Table.Cell>
              Olhos fechados, braços estendidos (10s)
            </Table.Cell>
            <Table.Cell>
                <Select.Root collection={avaliacoes} size="sm" width="320px">
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
                            {avaliacoes.items.map((avaliacao) => (
                            <Select.Item item={avaliacao} key={avaliacao.value}>
                                {avaliacao.label}
                                <Select.ItemIndicator />
                            </Select.Item>
                            ))}
                        </Select.Content>
                        </Select.Positioner>
                    </Portal>
                </Select.Root>
            </Table.Cell>
          </Table.Row>

          <Table.Row>
            <Table.Cell>Fala Anormal</Table.Cell>
            <Table.Cell>
              "O rato roeu a roupa do rei..."
            </Table.Cell>
            <Table.Cell>
                <Select.Root collection={avaliacoes} size="sm" width="320px">
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
                            {avaliacoes.items.map((avaliacao) => (
                            <Select.Item item={avaliacao} key={avaliacao.value}>
                                {avaliacao.label}
                                <Select.ItemIndicator />
                            </Select.Item>
                            ))}
                        </Select.Content>
                        </Select.Positioner>
                    </Portal>
                </Select.Root>
            </Table.Cell>
          </Table.Row>
          
        </Table.Body>
      </Table.Root>
    </Box>
  )

  
}


