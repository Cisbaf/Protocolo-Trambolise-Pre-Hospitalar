"use client"

import {
  Box,
  Grid,
  Text,
  Input,
  RadioCard,
  HStack,
  VStack,
  createListCollection,
} from "@chakra-ui/react"

export function DesfechoCenaSection() {

  const elegibilidade = createListCollection({
    items: [
      { label: "ELEGÍVEL", value: "elegivel" },
      { label: "INELEGÍVEL", value: "inelegivel" },
    ],
  })

  return (
    <Box>
      {/* Horários */}
      <Grid
        templateColumns="260px 1fr"
        gap={4}
        alignItems="center"
        mb={8}
      >
        <Text fontWeight="medium">
          Horário de Saída da Cena
        </Text>
        <Input type="time" size="sm" width="200px" />

        <Text fontWeight="medium">
          Horário de Chegada ao Hospital
        </Text>
        <Input type="time" size="sm" width="200px" />
      </Grid>

      {/* Parecer Final */}
      <Box
        border="1px solid"
        borderColor="gray.300"
        borderRadius="md"
        p={8}
      >
        <VStack gap={6}>
          <Text fontWeight="bold">
            PARECER FINAL DE ELEGIBILIDADE:
          </Text>

          <RadioCard.Root
            orientation="horizontal"
            justify="center"
            gap="6"
          >
            <HStack>
              {elegibilidade.items.map((item) => (
                <RadioCard.Item
                  key={item.value}
                  value={item.value}
                  px={10}
                  py={6}
                  borderRadius="md"
                >
                  <RadioCard.ItemHiddenInput />
                  <RadioCard.ItemControl>
                    <RadioCard.ItemText
                      fontSize="lg"
                      fontWeight="bold"
                      color={
                        item.value === "elegivel"
                          ? "green.600"
                          : "red.600"
                      }
                    >
                      {item.label}
                    </RadioCard.ItemText>
                  </RadioCard.ItemControl>
                </RadioCard.Item>
              ))}
            </HStack>
          </RadioCard.Root>
        </VStack>
      </Box>

    </Box>
  )
}