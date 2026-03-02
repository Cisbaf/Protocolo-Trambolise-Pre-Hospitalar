import { Card, Heading, VStack, HStack, Box } from "@chakra-ui/react"

type SectionCardProps = {
  title: string
  step?: number
  children: React.ReactNode
}

export function SectionCard({ title, step, children }: SectionCardProps) {
  return (
    <Card.Root
      variant="outline"
      size="lg"
      borderRadius="xl"
      bg="white"
    >
      <Card.Header pb={4}>
        <HStack align="center" gap={4}>
          
          {step && (
            <Box
              bg="red.600"
              color="white"
              fontWeight="bold"
              borderRadius="full"
              w="32px"
              h="32px"
              display="flex"
              alignItems="center"
              justifyContent="center"
              fontSize="sm"
            >
              {step}
            </Box>
          )}

          <Card.Title>
            <Heading size="md">{title}</Heading>
          </Card.Title>

        </HStack>
      </Card.Header>

      <Card.Body>
        <VStack align="stretch" gap={6}>
          {children}
        </VStack>
      </Card.Body>
    </Card.Root>
  )
}