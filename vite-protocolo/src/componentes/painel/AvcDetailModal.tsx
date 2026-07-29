"use client"

import React from "react";
import {
  Badge,
  Box,
  Button,
  CloseButton,
  Dialog,
  Flex,
  Heading,
  Portal,
  Separator,
  Spacer,
  Switch,
  Text,
} from "@chakra-ui/react";

import { AvcFormProvider, useAvcFormContext } from "../../context/AvcFormContext";
import { SectionCard } from "../SectionCard";
import { LinhaDoTempoSection } from "../../forms/formRegisterAvc/components/LinhaDoTempoSection";
import { AvaliacaoNeurologicaSection } from "../../forms/formRegisterAvc/components/AvaliacaoNeurologicaSection";
import { ParametrosClinicosSection } from "../../forms/formRegisterAvc/components/ParametrosClinicosSection";
import { HistoriaClinicaSection } from "../../forms/formRegisterAvc/components/HistoriaClinicaSection";
import { UnidadeReferenciaSection } from "../../forms/formRegisterAvc/components/UnidadeReferenciaSection";
import { DesfechoCenaSection } from "../../forms/formRegisterAvc/components/DesfechoCenaSection";
import ParecerFinalSection from "../../forms/formRegisterAvc/components/ParecerFinalSection";

import { protocoloToFormValues } from "../../helpers/protocoloToForm";
import { formatDateTimeBR } from "../../utils/dateUtils";
import { useDelete } from "../../hooks/useDelete";
import { BaseURL } from "../../settings";
import { DeleteSuccessToaster, ErrorToaster } from "../../forms/formRegisterAvc/utils/toasters";
import type { AvcDataValues } from "../../forms/formPaginationAvc/schemas/AvcData";

interface AvcDetailModalProps {
  item: AvcDataValues | null;
  onClose: () => void;
  /** Chamado após salvar ou excluir, para a listagem se atualizar. */
  onChanged: () => void;
}

/* ============================================================
   Seções do formulário. Em modo leitura o fieldset desabilitado
   bloqueia todos os campos de uma vez.
   ============================================================ */
function SecoesDoProtocolo({ somenteLeitura }: { somenteLeitura: boolean }) {
  return (
    <fieldset
      disabled={somenteLeitura}
      style={{ border: 0, padding: 0, margin: 0, minWidth: 0 }}
    >
      <Flex direction="column" gap={5} opacity={somenteLeitura ? 0.95 : 1}>
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

        <SectionCard title="DESFECHO DA CENA" step={6}>
          <DesfechoCenaSection />
        </SectionCard>

        <ParecerFinalSection />
      </Flex>
    </fieldset>
  );
}

/* ============================================================
   Chave de modo edição. Precisa do contexto do formulário para
   desfazer as alterações ao voltar para leitura.
   ============================================================ */
function ChaveModoEdicao({
  editando,
  setEditando,
  valoresOriginais,
}: {
  editando: boolean;
  setEditando: (v: boolean) => void;
  valoresOriginais: ReturnType<typeof protocoloToFormValues>;
}) {
  const { form } = useAvcFormContext();

  const alternar = (ligado: boolean) => {
    // sair do modo edição descarta o que não foi salvo
    if (!ligado) form.reset(valoresOriginais);
    setEditando(ligado);
  };

  return (
    <Switch.Root
      checked={editando}
      onCheckedChange={(e) => alternar(e.checked)}
      colorPalette="orange"
    >
      <Switch.HiddenInput />
      <Switch.Control />
      <Switch.Label fontWeight="medium">Modo edição</Switch.Label>
    </Switch.Root>
  );
}

/* ============================================================
   Modal
   ============================================================ */
export default function AvcDetailModal({ item, onClose, onChanged }: AvcDetailModalProps) {
  const [editando, setEditando] = React.useState(false);
  const [confirmandoExclusao, setConfirmandoExclusao] = React.useState(false);

  // toda vez que abre outro registro, volta para leitura
  React.useEffect(() => {
    setEditando(false);
    setConfirmandoExclusao(false);
  }, [item?.id]);

  const valoresOriginais = React.useMemo(
    () => (item ? protocoloToFormValues(item) : null),
    [item]
  );

  const { remove, loading: excluindo } = useDelete({
    url: `${BaseURL}/protocolo/${item?.id}`,
    onSuccess: () => {
      DeleteSuccessToaster(item?.LinhaDoTempoSection?.numeroOcorrencia ?? "");
      setConfirmandoExclusao(false);
      onChanged();
      onClose();
    },
    onError: (erro) => ErrorToaster(erro.message),
  });

  if (!item || !valoresOriginais) return null;

  return (
    <Dialog.Root
      open={!!item}
      onOpenChange={(e) => {
        if (!e.open) onClose();
      }}
      size="cover"
      placement="center"
      closeOnInteractOutside={!editando}
    >
      <Portal>
        <Dialog.Backdrop />
        <Dialog.Positioner>
          <Dialog.Content maxH="94vh" overflow="hidden" borderRadius="xl">
            {/* key força o formulário a recarregar quando troca de registro */}
            <AvcFormProvider
              key={item.id}
              initialValues={valoresOriginais}
              protocoloId={item.id}
              onSaved={() => {
                setEditando(false);
                onChanged();
              }}
            >
              <Box maxH="94vh" overflowY="auto">
                {/* ---------------- Cabeçalho ---------------- */}
                <Box
                  position="sticky"
                  top={0}
                  zIndex={2}
                  bg="white"
                  px={6}
                  pt={5}
                  pb={3}
                  borderBottomWidth="1px"
                >
                  <Flex align="center" gap={3} wrap="wrap">
                    <Heading size="md">
                      Ocorrência {item.LinhaDoTempoSection?.numeroOcorrencia}
                    </Heading>

                    <Badge colorPalette={editando ? "orange" : "gray"}>
                      {editando ? "EDITANDO" : "SOMENTE LEITURA"}
                    </Badge>

                    <Spacer />

                    <ChaveModoEdicao
                      editando={editando}
                      setEditando={setEditando}
                      valoresOriginais={valoresOriginais}
                    />

                    <Dialog.CloseTrigger asChild>
                      <CloseButton size="sm" />
                    </Dialog.CloseTrigger>
                  </Flex>

                  <Text fontSize="sm" color="gray.600" mt={1}>
                    ID {item.id} · {item.LinhaDoTempoSection?.municipio} · preenchido em{" "}
                    {item.dataCriacao ? formatDateTimeBR(item.dataCriacao) : "—"}
                  </Text>
                </Box>

                {/* ---------------- Corpo ---------------- */}
                <Box px={6} py={5}>
                  <SecoesDoProtocolo somenteLeitura={!editando} />
                </Box>

                {/* ---------------- Rodapé ---------------- */}
                <Box
                  position="sticky"
                  bottom={0}
                  zIndex={2}
                  bg="white"
                  px={6}
                  py={4}
                  borderTopWidth="1px"
                >
                  <Flex gap={3} align="center" wrap="wrap">
                    {/* Excluir fica isolado na ponta esquerda, longe do Salvar */}
                    <Button
                      colorPalette="red"
                      variant="outline"
                      onClick={() => setConfirmandoExclusao(true)}
                      loading={excluindo}
                    >
                      Excluir registro
                    </Button>

                    <Spacer />

                    <Button variant="ghost" onClick={onClose}>
                      Fechar
                    </Button>

                    {editando && (
                      <Button type="submit" colorPalette="blue">
                        Salvar alterações
                      </Button>
                    )}
                  </Flex>
                </Box>
              </Box>
            </AvcFormProvider>
          </Dialog.Content>
        </Dialog.Positioner>
      </Portal>

      {/* ============================================================
          Confirmação de exclusão.
          Fica centralizada na tela e com o botão de confirmar do lado
          DIREITO — o "Excluir registro" que abriu este diálogo está no
          canto inferior esquerdo, então um duplo clique acidental não
          cai em cima do confirmar.
          ============================================================ */}
      <Dialog.Root
        open={confirmandoExclusao}
        onOpenChange={(e) => setConfirmandoExclusao(e.open)}
        placement="center"
        size="sm"
        role="alertdialog"
      >
        <Portal>
          <Dialog.Backdrop />
          <Dialog.Positioner>
            <Dialog.Content>
              <Dialog.Header>
                <Dialog.Title color="red.600">Excluir definitivamente?</Dialog.Title>
              </Dialog.Header>

              <Dialog.Body>
                <Text>
                  A ocorrência{" "}
                  <Text as="span" fontWeight="bold">
                    {item.LinhaDoTempoSection?.numeroOcorrencia}
                  </Text>{" "}
                  ({item.LinhaDoTempoSection?.municipio}) será apagada do banco.
                </Text>
                <Separator my={3} />
                <Text fontSize="sm" color="gray.600">
                  Esta ação não tem volta: o registro não fica na lixeira nem pode ser
                  recuperado depois.
                </Text>
              </Dialog.Body>

              <Dialog.Footer>
                <Button variant="ghost" onClick={() => setConfirmandoExclusao(false)}>
                  Cancelar
                </Button>
                <Button colorPalette="red" onClick={() => remove()} loading={excluindo}>
                  Sim, excluir
                </Button>
              </Dialog.Footer>
            </Dialog.Content>
          </Dialog.Positioner>
        </Portal>
      </Dialog.Root>
    </Dialog.Root>
  );
}
