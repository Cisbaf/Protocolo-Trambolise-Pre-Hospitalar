import { AvaliacaoNeurologicaSection } from "../forms/formRegisterAvc/components/AvaliacaoNeurologicaSection";
import { DesfechoCenaSection } from "../forms/formRegisterAvc/components/DesfechoCenaSection";
import { HistoriaClinicaSection } from "../forms/formRegisterAvc/components/HistoriaClinicaSection";
import { LinhaDoTempoSection } from "../forms/formRegisterAvc/components/LinhaDoTempoSection";
import { ParametrosClinicosSection } from "../forms/formRegisterAvc/components/ParametrosClinicosSection";
import ParecerFinalSection from "../forms/formRegisterAvc/components/ParecerFinalSection";
import { SectionCard } from "../componentes/SectionCard";
import { UnidadeReferenciaSection } from "../forms/formRegisterAvc/components/UnidadeReferenciaSection";
import { AvcFormProvider } from "../context/AvcFormContext";

import {
    Button,
  Heading,
  Separator,
  Flex,
} from "@chakra-ui/react"

export default function AvcFormPage() {

    return (
        <AvcFormProvider fakeInitialValues>
           <Flex direction="column" gap={5}>
                {/* Header */}
                <Heading
                    size={{ base: "md", md: "lg" }}
                    color="red.600"
                    letterSpacing="wide"
                    textAlign={"center"}
                    >
                    PROTOCOLO DE ATENDIMENTO AO AVC HIPERAGUDO!
                </Heading>

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

                <Separator/>

                <Button
                    type="submit"
                    size="lg"
                    colorScheme="red"
                    w="100%"
                >
                    ENVIAR FORMULÁRIO
                </Button>
            </Flex>

        </AvcFormProvider>
    )

}