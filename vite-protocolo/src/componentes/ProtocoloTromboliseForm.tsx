"use client"

import {
  Box,
  VStack,
  Heading,
  Separator,
  Button,
} from "@chakra-ui/react"

import { LinhaDoTempoSection } from "./LinhaDoTempoSection"
import { AvaliacaoNeurologicaSection } from "./AvaliacaoNeurologicaSection"
import { ParametrosClinicosSection } from "./ParametrosClinicosSection"
import { HistoriaClinicaSection } from "./HistoriaClinicaSection"
import { UnidadeReferenciaSection } from "./UnidadeReferenciaSection"
import { DesfechoCenaSection } from "./DesfechoCenaSection"
import { SectionCard } from "./SectionCard"
import ParecerFinalSection from "./ParecerFinalSection"
import { useGroupContext } from "../context/GroupContext"
import type { DataFormValues } from "../forms/DataForm"
import SendFormBackend from "../helpers/SendFormBackend"
import { Toaster, toaster } from "./ui/toaster"
import React from "react"
import { LoadingOverlay } from "./ui/loading"

export function ProtocoloTromboliseForm() {
  const { form } = useGroupContext()
  const [loading, setLoading] = React.useState(false)

  const onSubmit = async (data: DataFormValues) => {
    setLoading(true)

    const result = await SendFormBackend(data)

    if (!result.success) {
      toaster.create({
        title: "Erro ao enviar formulário",
        description: result.message,
        type: "error",
        duration: 10000,
      })
      setLoading(false)
      return
    }

    toaster.create({
      title: "Sucesso 🥳🥳🥳!!!",
      description: `ID gerado ${result.data.id}`,
      type: "success",
      duration: 10000,
    })

    setLoading(false)
  }

  const onError = (errors: any) => {
    console.log("FORM ERROR ❌", errors)
  }

  return (
    <form
      onSubmit={form.handleSubmit(onSubmit, onError)}
      style={{ width: "100%" }}
    >
      <Toaster />
      <LoadingOverlay isOpen={loading} />

      <Box
        bg="gray.50"
        minH="100vh"
        w="100%"
        py={{ base: 4, md: 10 }}
      >
        {/* Container responsivo */}
        <Box
          w="100%"
          maxW={{ base: "100%", md: "720px", lg: "900px", xl: "1100px" }}
          mx="auto"
          px={{ base: 3, sm: 4, md: 6 }}
        >
          <VStack
            align="stretch"
            gap={{ base: 6, md: 8 }}
            w="100%"
          >
            {/* Header */}
            <Box textAlign="center">
              <Heading
                size={{ base: "md", md: "lg" }}
                color="red.600"
                letterSpacing="wide"
              >
                PROTOCOLO DE ATENDIMENTO AO AVC HIPERAGUDO
              </Heading>
            </Box>

            <Separator />

            {/* Sections */}

            <SectionCard title="LINHA DO TEMPO E IDENTIFICAÇÃO" step={1}>
              <LinhaDoTempoSection />
            </SectionCard>

            <SectionCard title="AVALIAÇÃO NEUROLÓGICA (ESCALA DE BE FAST)" step={2}>
              <AvaliacaoNeurologicaSection />
            </SectionCard>

            <SectionCard title="PARÂMETROS CLÍNICOS OBRIGATÓRIOS" step={3}>
              <ParametrosClinicosSection />
            </SectionCard>

            <SectionCard title="HISTÓRIA CLÍNICA E FATORES DE RISCO" step={4}>
              <HistoriaClinicaSection />
            </SectionCard>

            <SectionCard title="UNIDADE DE REFERÊNCIA E NOTIFICAÇÃO" step={5}>
              <UnidadeReferenciaSection />
            </SectionCard>

            <SectionCard title="DESFECHO DA CENA E ELEGIBILIDADE" step={6}>
              <DesfechoCenaSection />
            </SectionCard>

            <ParecerFinalSection />

            <Button
              type="submit"
              size="lg"
              colorScheme="red"
              w="100%"
            >
              ENVIAR FORMULÁRIO
            </Button>

          </VStack>
        </Box>
      </Box>
    </form>
  )
}