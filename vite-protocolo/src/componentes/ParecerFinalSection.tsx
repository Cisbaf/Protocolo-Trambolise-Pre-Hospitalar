import { Box, Text } from "@chakra-ui/react";
import { useGroupContext } from "../context/GroupContext";
import React from "react";

export default function ParecerFinalSection() {
    const { form } = useGroupContext();
    
    const elegibilidade = form.watch("ParecerFinalSection.elegibilidade");

    React.useEffect(()=>{
        setTimeout(()=>{
            form.setValue("ParecerFinalSection.elegibilidade", "inelegivel")
        }, 3000)
    }, [])

    const styleMap = {
        elegivel: {
            borderColor: "green.500",
            bgColor: "green.50",
            textColor: "green.700",
            label: "ELEGÍVEL"
        },
        inelegivel: {
            borderColor: "red.500",
            bgColor: "red.50",
            textColor: "red.700",
            label: "INELEGÍVEL"
        },
        default: {
            borderColor: "gray.300",
            bgColor: "gray.50",
            textColor: "gray.500",
            label: "AGUARDANDO PARECER"
        }
    };

    const currentStyle = styleMap[
        elegibilidade as keyof typeof styleMap
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
            {elegibilidade && (
                <Text fontSize="md" color="gray.600" mt={2}>
                    Parecer final de elegibilidade
                </Text>
            )}
        </Box>
    );
}