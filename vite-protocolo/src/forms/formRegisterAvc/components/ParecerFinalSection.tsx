import { Box, Flex, For, Stack, Text } from "@chakra-ui/react";
import React from "react";
import { useWatch } from "react-hook-form";
import ParecerFinalHelper from "../../../helpers/ParecerFinal";
import { useAvcFormContext } from "../../../context/AvcFormContext";
import type { AvcFormValues } from "../schemas/AvcFormSchema";


export default function ParecerFinalSection() {
    const { form } = useAvcFormContext();

    // ✅ Observa o form inteiro
    const formValues = useWatch({
      control: form.control,
    }) as AvcFormValues;

    // ✅ Cálculo puro
    const parecerFinal = React.useMemo(() => {
      if (!formValues) return null;

      return ParecerFinalHelper(formValues);
    }, [formValues]);

    // ✅ Evita loop infinito
    const previousRef = React.useRef<string | null>(null);

    React.useEffect(() => {
      if (!parecerFinal) return;

      const serialized = JSON.stringify(parecerFinal);

      if (previousRef.current !== serialized) {
        previousRef.current = serialized;

        form.setValue("ParecerFinalSection", parecerFinal, {
          shouldDirty: false,
          shouldValidate: false,
        });
      }
    }, [parecerFinal, form]);


  const styleMap = {
    elegivel: {
      borderColor: "green.500",
      bgColor: "green.50",
      textColor: "green.700",
      label: "ELEGÍVEL",
    },
    inelegivel: {
      borderColor: "red.500",
      bgColor: "red.50",
      textColor: "red.700",
      label: "INELEGÍVEL",
    },
    default: {
      borderColor: "gray.300",
      bgColor: "gray.50",
      textColor: "gray.500",
      label: "AGUARDANDO PARECER",
    },
  };

  const currentStyle =
    styleMap[
      parecerFinal?.elegibilidade as keyof typeof styleMap
    ] ?? styleMap.default;

  return (
    <Box
      border="2px solid"
      borderColor={currentStyle.borderColor}
      bg={currentStyle.bgColor}
      borderRadius="lg"
      p={8}
      textAlign="center"
      boxShadow="sm"
    >
      <Text
        fontSize="4xl"
        fontWeight="bold"
        color={currentStyle.textColor}
        textTransform="uppercase"
        letterSpacing="wide"
      >
        {currentStyle.label}
      </Text>

      {parecerFinal?.elegibilidade && (
        <Flex direction="column" gap={2}>
          <Text fontSize="md" color="gray.600" mt={2}>
            Parecer final de elegibilidade
          </Text>

          {parecerFinal.elegibilidade === "inelegivel" && (
            <Stack>
              <For each={parecerFinal.motivos}>
                {(item, index) => (
                  <Text key={item + index} fontWeight="bold">
                    {item}
                  </Text>
                )}
              </For>
            </Stack>
          )}
        </Flex>
      )}
    </Box>
  );
}