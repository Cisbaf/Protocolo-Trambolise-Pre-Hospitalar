"use client";

import React from "react";
import {
  Box,
  Flex,
  Input,
  Button,
  Text,
  Portal,
  createListCollection,
  Select,
  Tag,
  CloseButton,
} from "@chakra-ui/react";
import type { FilterField, useDynamicFilter } from "../../hooks/useDynamicFilter";


interface DynamicFilterProps {
    filter: ReturnType<typeof useDynamicFilter>;
    resetConstruction?: () => void;
    debug?: boolean;
}

export function DynamicFilterBuilder({filter, debug, resetConstruction}: DynamicFilterProps) {
  const { filters, addFilter, removeFilter, queryString, fields } = filter;

  const [selectedField, setSelectedField] = React.useState<FilterField | null>(null);
  const [value, setValue] = React.useState("");

  const fieldCollection = React.useMemo(
    () =>
      createListCollection({
        items: fields.map((f) => ({
          label: f.label,
          value: f.search_key,
        })),
    }),
    [fields]
  );

  const handleAdd = () => {
    if (!selectedField || !value) return;
    if (resetConstruction) resetConstruction();
    addFilter(selectedField.search_key, value);
    setValue("");
  };

  return (
    <Box p="4" borderWidth="1px" borderRadius="2xl" shadow="sm">
      <Flex direction="column" gap="4">
        {/* BUILDER */}
        <Flex gap="3" align="end" wrap="wrap">
          {/* SELECT */}
          <Box minW="240px">
            <Select.Root
              collection={fieldCollection}
              value={selectedField ? [selectedField.search_key] : []}
              onValueChange={(e) => {
                const field = fields.find((f) => f.search_key === e.value[0]);
                setSelectedField(field || null);
                setValue("");
              }}
            >
              <Select.HiddenSelect />

              <Select.Label>Campo</Select.Label>

              <Select.Control>
                <Select.Trigger>
                  <Select.ValueText placeholder="Selecione um campo" />
                </Select.Trigger>
                <Select.IndicatorGroup>
                  <Select.Indicator />
                </Select.IndicatorGroup>
              </Select.Control>

              <Portal>
                <Select.Positioner>
                  <Select.Content>
                    {fieldCollection.items.map((item) => (
                      <Select.Item item={item} key={item.value}>
                        {item.label}
                        <Select.ItemIndicator />
                      </Select.Item>
                    ))}
                  </Select.Content>
                </Select.Positioner>
              </Portal>
            </Select.Root>
          </Box>

          {/* INPUT DINÂMICO */}
          <Box flex="1" minW="200px">
            <Input
              type={selectedField?.type_field || "text"}
              placeholder={
                selectedField
                  ? `Digite ${selectedField.label}`
                  : "Selecione um campo primeiro"
              }
              value={value}
              onChange={(e) => setValue(e.target.value)}
              disabled={!selectedField}
            />
          </Box>

          {/* BUTTON */}
          <Button
            onClick={handleAdd}
            disabled={!selectedField || !value}
            colorPalette="blue"
          >
            Adicionar
          </Button>
        </Flex>

        {/* FILTROS ATIVOS */}
        <Flex gap="2" wrap="wrap">
          {filters.length === 0 && (
            <Text fontSize="sm" color="gray.500">
              Nenhum filtro aplicado
            </Text>
          )}

          {filters.map((filter) => (
            <Tag.Root
              key={filter.field}
              borderRadius="full"
              variant="subtle"
              colorPalette="blue"
            >
              <Tag.Label>
                <strong>{filter.label}:</strong> {filter.value}
              </Tag.Label>
              <Tag.EndElement>
                <CloseButton
                  size="xs"
                  onClick={() => removeFilter(filter.field)}
                />
              </Tag.EndElement>
            </Tag.Root>
          ))}
        </Flex>
        {/* QUERY PREVIEW */}
        {debug && (
            <Box
            p="3"
            borderWidth="1px"
            borderRadius="lg"
            bg="gray.50"
            fontSize="sm"
            >
            <Text fontWeight="bold" mb="1">
                Query gerada
            </Text>
            <Text wordBreak="break-all" color="gray.700">
                {queryString || "—"}
            </Text>
            </Box>
        )}
      </Flex>
    </Box>
  );
}
