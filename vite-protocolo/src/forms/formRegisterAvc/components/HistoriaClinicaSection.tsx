"use client";

import {
  Box,
  VStack,
  Field,
  Input,
  Checkbox,
  Text,
  Grid,
  Card,
  Flex,
  CheckboxCard,
  RadioGroup,
  HStack,
} from "@chakra-ui/react";

import React from "react";
import {
  lista_doencas,
  medicamentos_injetaveis,
  medicamentos_orais,
  medicamentos_outros,
  options_coagulantes,
} from "../utils/labels";
import { Controller } from "react-hook-form";
import { useAvcFormContext } from "../../../context/AvcFormContext";

export function HistoriaClinicaSection() {
  const { form } = useAvcFormContext();

  const {
    register,
    setValue,
    watch,
    formState: { errors },
    control,
  } = form;

  const [negaUso, setNegaUso] = React.useState(false);

  const medicamentos = watch("HistoriaClinicaSection.medicamentos") || [];
  const fez_uso_48h = watch("HistoriaClinicaSection.uso_coagulante_em_48h");

  /**
   * =========================
   * Estados derivados
   * =========================
   */

  const bloquearTodosMedicamentos = negaUso;
  const bloquearAnticoagulantes = !negaUso && fez_uso_48h;

  /**
   * =========================
   * Effects de sincronização
   * =========================
   */

  // Se negar uso, limpar tudo
  React.useEffect(() => {
    if (negaUso) {
      setValue("HistoriaClinicaSection.uso_coagulante_em_48h", false);
      setValue("HistoriaClinicaSection.medicamentos", []);
    }
  }, [negaUso, setValue]);

  // Se marcou uso nas últimas 48h, remover anticoagulantes
  React.useEffect(() => {
    if (fez_uso_48h) {
      const filtrado = medicamentos.filter(
        (m: string) =>
          !medicamentos_injetaveis.includes(m) &&
          !medicamentos_orais.includes(m)
      );

      setValue("HistoriaClinicaSection.medicamentos", filtrado);
    }
  }, [fez_uso_48h]);

  /**
   * =========================
   * Funções
   * =========================
   */

  const toggleMedicamento = (med: string, checked: boolean) => {
    const novoArray = checked
      ? [...medicamentos, med]
      : medicamentos.filter((m: string) => m !== med);

    setValue("HistoriaClinicaSection.medicamentos", novoArray, {
      shouldDirty: true,
    });
  };

  const handleNegaUsoChange = () => {
    const novoValor = !negaUso;
    setNegaUso(novoValor);

    if (novoValor) {
      setValue("HistoriaClinicaSection.medicamentos", []);
    }
  };

  /**
   * =========================
   * Checkbox reutilizável
   * =========================
   */

  const MedicamentoCheckbox = ({
    nome,
    disabled,
  }: {
    nome: string;
    disabled?: boolean;
  }) => (
    <Checkbox.Root
      disabled={disabled}
      checked={medicamentos.includes(nome)}
      onCheckedChange={(e) => toggleMedicamento(nome, !!e.checked)}
    >
      <Checkbox.HiddenInput />
      <Checkbox.Control />
      <Checkbox.Label>{nome}</Checkbox.Label>
    </Checkbox.Root>
  );

  return (
    <Box w="100%">
      <VStack align="stretch" gap={8} w="100%">
        {/* ================= Idade ================= */}

        <Field.Root
          maxW={{ base: "100%", md: "200px" }}
          invalid={!!errors.HistoriaClinicaSection?.idade}
        >
          <Field.Label>Idade do Paciente</Field.Label>

          <Input
            type="number"
            size="sm"
            {...register("HistoriaClinicaSection.idade", {
              valueAsNumber: true,
            })}
          />

          {errors.HistoriaClinicaSection?.idade && (
            <Field.ErrorText>
              {errors.HistoriaClinicaSection.idade.message}
            </Field.ErrorText>
          )}
        </Field.Root>

        {/* ================= Histórico ================= */}

        <Box>
          <Text fontWeight="semibold" mb={3}>
            Histórico de Doenças
          </Text>

          <Grid templateColumns={{ base: "1fr", md: "repeat(2, 1fr)" }} gap={4}>
            {lista_doencas.map((item) => (
              <Controller
                key={item}
                name={`HistoriaClinicaSection.doencas.${item}` as const}
                control={control}
                render={({ field }) => (
                  <Checkbox.Root
                    checked={field.value}
                    onCheckedChange={(e) => field.onChange(!!e.checked)}
                  >
                    <Checkbox.HiddenInput />
                    <Checkbox.Control />
                    <Checkbox.Label>{item}</Checkbox.Label>
                  </Checkbox.Root>
                )}
              />
            ))}
          </Grid>
        </Box>

        {/* ================= Medicamentos ================= */}

        <Box pt={2}>
          <Flex justifyContent="space-between" wrap="wrap">
            {/* NEGA USO */}

            <Flex
              direction={{ base: "column", md: "row" }}
              gap={5}
              align={{ base: "flex-start", md: "center" }}
              mb={5}
            >
              <Text fontWeight="semibold">Anticoagulantes em Uso</Text>

              <CheckboxCard.Root
                maxW="150px"
                size="sm"
                checked={negaUso}
                onCheckedChange={handleNegaUsoChange}
                colorPalette="teal"
              >
                <CheckboxCard.HiddenInput />
                <CheckboxCard.Control>
                  <CheckboxCard.Label>Nega Uso</CheckboxCard.Label>
                  <CheckboxCard.Indicator />
                </CheckboxCard.Control>
              </CheckboxCard.Root>
            </Flex>

            {/* RADIO */}

            <Flex direction="column" gap={5} mb={5}>
              <Text fontWeight="semibold">
                FEZ USO DE ANTICOAGULANTE A MENOS DE 48h ?
              </Text>

              <Controller
                control={control}
                name="HistoriaClinicaSection.uso_coagulante_em_48h"
                render={({ field }) => (
                  <RadioGroup.Root
                    disabled={negaUso}
                    value={
                      field.value === undefined
                        ? undefined
                        : String(field.value)
                    }
                    onValueChange={(e) => field.onChange(e.value === "true")}
                  >
                    <HStack gap="6">
                      {options_coagulantes.map((option) => (
                        <RadioGroup.Item
                          key={option.label}
                          value={String(option.value)}
                        >
                          <RadioGroup.ItemHiddenInput />
                          <RadioGroup.ItemIndicator />
                          <RadioGroup.ItemText>
                            {option.label}
                          </RadioGroup.ItemText>
                        </RadioGroup.Item>
                      ))}
                    </HStack>
                  </RadioGroup.Root>
                )}
              />
            </Flex>
          </Flex>

          {/* ================= CARDS ================= */}

          <Grid
            templateColumns={{
              base: "1fr",
              md: "repeat(2, 1fr)",
              xl: "repeat(3, 1fr)",
            }}
            mt={2}
            gap={6}
          >
            {/* INJETÁVEIS */}

            <Card.Root variant="outline" size="sm">
              <Card.Header bg="green.100">
                <Text fontWeight="semibold" color="green.800">
                  Anticoagulantes Injetáveis
                </Text>
              </Card.Header>

              <Card.Body>
                <VStack align="start" gap={2}>
                  {medicamentos_injetaveis.map((med) => (
                    <MedicamentoCheckbox
                      key={med}
                      nome={med}
                      disabled={
                        bloquearTodosMedicamentos ||
                        bloquearAnticoagulantes
                      }
                    />
                  ))}
                </VStack>
              </Card.Body>
            </Card.Root>

            {/* ORAIS */}

            <Card.Root variant="outline" size="sm">
              <Card.Header bg="blue.100">
                <Text fontWeight="semibold" color="blue.800">
                  Anticoagulantes Orais
                </Text>
              </Card.Header>

              <Card.Body>
                <VStack align="start" gap={2}>
                  {medicamentos_orais.map((med) => (
                    <MedicamentoCheckbox
                      key={med}
                      nome={med}
                      disabled={
                        bloquearTodosMedicamentos ||
                        bloquearAnticoagulantes
                      }
                    />
                  ))}
                </VStack>
              </Card.Body>
            </Card.Root>

            {/* OUTROS */}

            <Card.Root variant="outline" size="sm">
              <Card.Header bg="yellow.100">
                <Text fontWeight="semibold" color="yellow.800">
                  Outros Medicamentos
                </Text>
              </Card.Header>

              <Card.Body>
                <VStack align="start" gap={2}>
                  {medicamentos_outros.map((med) => (
                    <MedicamentoCheckbox
                      key={med}
                      nome={med}
                      disabled={bloquearTodosMedicamentos}
                    />
                  ))}
                </VStack>
              </Card.Body>
            </Card.Root>
          </Grid>
        </Box>

        {errors.HistoriaClinicaSection?.medicamentos && (
          <Text color="red.500" fontSize="sm">
            {errors.HistoriaClinicaSection.medicamentos.message}
          </Text>
        )}
      </VStack>
    </Box>
  );
}