"use client"

import {
  Box,
  Container,
  VStack,
  Heading,
  Separator,
} from "@chakra-ui/react"

import { LinhaDoTempoSection } from "./LinhaDoTempoSection"
import { AvaliacaoNeurologicaSection } from "./AvaliacaoNeurologicaSection"
import { ParametrosClinicosSection } from "./ParametrosClinicosSection"
import { HistoriaClinicaSection } from "./HistoriaClinicaSection"
import { UnidadeReferenciaSection } from "./UnidadeReferenciaSection"
import { DesfechoCenaSection } from "./DesfechoCenaSection"
import { SectionCard } from "./SectionCard"
import { GroupContextProvider } from "../context/GroupContext"
import ParecerFinalSection from "./ParecerFinalSection"


export function ProtocoloTromboliseForm() {
  return (
    <GroupContextProvider>
      <Box bg="gray.50" minH="100vh" py={10}>
        <Container maxW="1000px">
          <VStack align="stretch" gap={8}>

            {/* Header */}
            <Box textAlign="center">
              <Heading
                size="lg"
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
            
            <ParecerFinalSection/>

          </VStack>
        </Container>
      </Box>
    </GroupContextProvider>
  )
}