import { Portal, Box, Spinner, Center } from "@chakra-ui/react"

export function LoadingOverlay({ isOpen }: { isOpen: boolean }) {
  if (!isOpen) return null

  return (
    <Portal>
      <Box
        position="fixed"
        inset="0"
        bg="blackAlpha.600"
        zIndex="overlay"
      >
        <Center h="100%">
          <Spinner size="xl" color="white" />
        </Center>
      </Box>
    </Portal>
  )
}