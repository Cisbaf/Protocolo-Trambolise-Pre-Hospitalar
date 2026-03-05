"use client"

import {
  Box,
  Table,
  Select,
  createListCollection,
    Portal
} from "@chakra-ui/react"
import { respostasAvaliacoes, testesNeurologicos } from "../utils/testeNeurologicos"
import { useGroupContext } from "../context/GroupContext"
import { Controller } from "react-hook-form";
import React from "react";


export function AvaliacaoNeurologicaSection() {
    const { form } = useGroupContext();

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
        {testesNeurologicos.map((teste) => (
            <Table.Row key={teste.key}>
            <Table.Cell>{teste.nome}</Table.Cell>
            <Table.Cell>{teste.descricao}</Table.Cell>
            <Table.Cell>
                <Controller
                control={form.control}
                name={`AvaliacaoNeurologicaSection.${teste.key}`} // mapeia para o form
                render={({ field }) => (
                    <Select.Root
                    {...field}
                    collection={createListCollection(respostasAvaliacoes)}
                    size="sm"
                    width="320px"
                    value={field.value ? [field.value] : []} // ✅ transformar string em array
                    onValueChange={(val) => field.onChange(val.value[0])} // atualiza o form
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
                            <Select.Item item={avaliacao} key={avaliacao.value}>
                                {avaliacao.label}
                                <Select.ItemIndicator />
                            </Select.Item>
                            ))}
                        </Select.Content>
                        </Select.Positioner>
                    </Portal>
                    </Select.Root>
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


