import {
  Box,
  Button,
  CloseButton,
  Flex,
  Tag,
  Text,
  Wrap,
  WrapItem,
} from "@chakra-ui/react";
import { useAvcSelectedContext } from "../../context/AvcSelected";
import { useAvcManagerContext } from "../../context/AvcManagerContext";
import { exportAvcToExcel } from "../../helpers/exportAvcToExcel";

export default function TagsSelecteds() {
  const { useAvc } = useAvcSelectedContext();
  const { form } = useAvcManagerContext();

  const currentItems = form.getValues("content") || [];

  const handleSelectAll = () => {
    useAvc.addMany(currentItems);
  };

  const handleClearAll = () => {
    useAvc.clear();
  };

  const handleExportAll = () => {
    exportAvcToExcel(useAvc.avcList);
  }

  return (
    <Box
      mt={4}
      p={3}
      borderRadius="xl"
      bg="gray.50"
      border="1px solid"
      borderColor="gray.200"
    >
      <Flex justify="space-between" align="center" mb={2}>
        <Text fontSize="sm" fontWeight="medium" color="gray.600">
          Selecionados ({useAvc.avcList.length})
        </Text>

        <Flex gap={2}>
          <Button size="xs" variant="outline" onClick={handleSelectAll}>
            Selecionar página
          </Button>

            <Button
            size="xs"
            variant="outline"
            colorPalette="green"
            onClick={handleExportAll}
            disabled={!useAvc.avcList.length}
          >
            Exportar Selecionados
          </Button>

          <Button
            size="xs"
            variant="ghost"
            colorPalette="red"
            onClick={handleClearAll}
            disabled={!useAvc.avcList.length}
          >
            Limpar
          </Button>
        </Flex>
      </Flex>

      <Wrap gap={2}>
        {useAvc.avcList.map((item) => (
          <WrapItem key={item.id}>
            <Tag.Root
              borderRadius="full"
              px={3}
              py={1.5}
              bg="blue.100"
              color="blue.800"
              _hover={{
                bg: "blue.200",
                transform: "translateY(-1px)",
                boxShadow: "sm",
              }}
              transition="all 0.15s ease"
            >
              <Tag.Label fontSize="sm" fontWeight="semibold">
                {item.LinhaDoTempoSection.numeroOcorrencia}
              </Tag.Label>

              <Tag.EndElement ml={2}>
                <CloseButton
                  size="xs"
                  onClick={() => useAvc.remove(item.id)}
                />
              </Tag.EndElement>
            </Tag.Root>
          </WrapItem>
        ))}
      </Wrap>
    </Box>
  );
}