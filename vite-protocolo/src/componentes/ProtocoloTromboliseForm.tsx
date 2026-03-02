"use client"

import {
  Box,
  Container,
  VStack,
  Heading,
  Text,
  Separator,
} from "@chakra-ui/react"

import { LinhaDoTempoSection } from "./LinhaDoTempoSection"
import { AvaliacaoNeurologicaSection } from "./AvaliacaoNeurologicaSection"
import { ParametrosClinicosSection } from "./ParametrosClinicosSection"
import { HistoriaClinicaSection } from "./HistoriaClinicaSection"
import { CriteriosExclusaoSection } from "./CriteriosExclusaoSection"
import { UnidadeReferenciaSection } from "./UnidadeReferenciaSection"
import { DesfechoCenaSection } from "./DesfechoCenaSection"
import { SectionCard } from "./SectionCard"


export function ProtocoloTromboliseForm() {
  return (
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
              PROTOCOLO DE TROMBÓLISE PRÉ-HOSPITALAR
            </Heading>

            <Text fontSize="sm" color="gray.600" mt={2}>
              Decisão Clínica Sequencial em 7 Etapas | Contexto: 2026-02-27
            </Text>
          </Box>

          <Separator />

          {/* Sections */}

          <SectionCard title="LINHA DO TEMPO E IDENTIFICAÇÃO" step={1}>
            <LinhaDoTempoSection />
          </SectionCard>

          <SectionCard title="AVALIAÇÃO NEUROLÓGICA (ESCALA DE CINCINNATI)" step={2}>
            <AvaliacaoNeurologicaSection />
          </SectionCard>

          <SectionCard title="PARÂMETROS CLÍNICOS OBRIGATÓRIOS" step={3}>
            <ParametrosClinicosSection />
          </SectionCard>

          <SectionCard title="HISTÓRIA CLÍNICA E FATORES DE RISCO" step={4}>
            <HistoriaClinicaSection />
          </SectionCard>

          {/* <SectionCard title="CRITÉRIOS DE EXCLUSÃO (NIHSS E IMAGEM)" step={5}>
            <CriteriosExclusaoSection />
          </SectionCard> */}

          <SectionCard title="UNIDADE DE REFERÊNCIA E NOTIFICAÇÃO" step={5}>
            <UnidadeReferenciaSection />
          </SectionCard>

          <SectionCard title="DESFECHO DA CENA E ELEGIBILIDADE" step={6}>
            <DesfechoCenaSection />
          </SectionCard>

        </VStack>
      </Container>
    </Box>
  )
}