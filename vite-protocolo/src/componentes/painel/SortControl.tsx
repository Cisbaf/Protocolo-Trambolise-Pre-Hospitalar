import React from "react";
import {
    Box,
    Flex,
    Portal,
    Select,
    createListCollection,
} from "@chakra-ui/react";
import { AVC_SORT_FIELDS, useAvcManagerContext, type SortDirection } from "../../context/AvcManagerContext";

const DIRECTION_OPTIONS: { label: string; value: SortDirection }[] = [
    { label: "Mais recente primeiro", value: "DESC" },
    { label: "Mais antigo primeiro", value: "ASC" },
];

export default function SortControl() {
    const { sort, direction, setSort } = useAvcManagerContext();

    const fieldCollection = React.useMemo(
        () =>
            createListCollection({
                items: AVC_SORT_FIELDS.map((f) => ({ label: f.label, value: f.value })),
            }),
        []
    );

    const directionCollection = React.useMemo(
        () =>
            createListCollection({
                items: DIRECTION_OPTIONS.map((d) => ({ label: d.label, value: d.value })),
            }),
        []
    );

    return (
        <Flex gap="3" align="end" wrap="wrap">
            <Box minW="200px">
                <Select.Root
                    collection={fieldCollection}
                    value={[sort]}
                    onValueChange={(e) => setSort(e.value[0], direction)}
                >
                    <Select.HiddenSelect />
                    <Select.Label>Ordenar por</Select.Label>
                    <Select.Control>
                        <Select.Trigger>
                            <Select.ValueText placeholder="Ordenar por" />
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

            <Box minW="200px">
                <Select.Root
                    collection={directionCollection}
                    value={[direction]}
                    onValueChange={(e) => setSort(sort, e.value[0] as SortDirection)}
                >
                    <Select.HiddenSelect />
                    <Select.Label>Direção</Select.Label>
                    <Select.Control>
                        <Select.Trigger>
                            <Select.ValueText placeholder="Direção" />
                        </Select.Trigger>
                        <Select.IndicatorGroup>
                            <Select.Indicator />
                        </Select.IndicatorGroup>
                    </Select.Control>
                    <Portal>
                        <Select.Positioner>
                            <Select.Content>
                                {directionCollection.items.map((item) => (
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
        </Flex>
    );
}
