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
} from "@chakra-ui/react";
import React from "react";
import { useGroupContext } from "../context/GroupContext";
import {
  lista_doencas,
  medicamentos_injetaveis,
  medicamentos_orais,
  medicamentos_outros,
} from "../utils/labels";
import { Controller } from "react-hook-form";

export function HistoriaClinicaSection() {
  const { form } = useGroupContext();
  const {
    register,
    setValue,
    watch,
    formState: { errors },
  } = form;

  const [negaUso, setNegaUso] = React.useState(false);

  const medicamentos = watch("HistoriaClinicaSection.medicamentos") || [];

  const toggleMedicamento = (med: string, checked: boolean) => {
    const novoArray = checked
      ? [...medicamentos, med]
      : medicamentos.filter((m) => m !== med);

    setValue("HistoriaClinicaSection.medicamentos", novoArray, {
      shouldDirty: true,
    });
  };

  const handleNegaUsoChange = () => {
    const novoValor = !negaUso;
    setNegaUso(novoValor);

    if (novoValor) {
      setValue("HistoriaClinicaSection.medicamentos", [], {
        shouldDirty: true,
      });
    }
  };

  const MedicamentoCheckbox = ({ nome }: { nome: string }) => (
    <Checkbox.Root
      disabled={negaUso}
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
              {errors.HistoriaClinicaSection?.idade.message}
            </Field.ErrorText>
          )}
        </Field.Root>

        {/* ================= Histórico ================= */}
        <Box>
          <Text fontWeight="semibold" mb={3}>
            Histórico de Doenças
          </Text>

          <Grid
            templateColumns={{ base: "1fr", md: "repeat(2, 1fr)" }}
            gap={4}
          >
            {lista_doencas.map((item) => (
              <Controller
                key={item}
                name={`HistoriaClinicaSection.doencas.${item}` as const}
                control={form.control}
                render={({ field }) => (
                  <Checkbox.Root
                    checked={field.value}
                    onCheckedChange={(e) =>
                      field.onChange(!!e.checked)
                    }
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
        <Box>
          <Flex
            direction={{ base: "column", md: "row" }}
            gap={5}
            align={{ base: "flex-start", md: "center" }}
            mb={5}
          >
            <Text fontWeight="semibold">
              Anticoagulantes em Uso
            </Text>

            <CheckboxCard.Root
              maxW="150px"
              size="sm"
              checked={negaUso}
              onCheckedChange={handleNegaUsoChange}
              colorPalette="teal"
            >
              <CheckboxCard.HiddenInput />
              <CheckboxCard.Control>
                <CheckboxCard.Label>
                  Nega Uso
                </CheckboxCard.Label>
                <CheckboxCard.Indicator />
              </CheckboxCard.Control>
            </CheckboxCard.Root>
          </Flex>

          {/* Cards Responsivos */}
          <Grid
            templateColumns={{
              base: "1fr",
              md: "repeat(2, 1fr)",
              xl: "repeat(3, 1fr)",
            }}
            gap={6}
          >
            <Card.Root variant="outline" size="sm">
              <Card.Header bg="green.100">
                <Text fontWeight="semibold" color="green.800">
                  Anticoagulantes Injetáveis
                </Text>
              </Card.Header>
              <Card.Body>
                <VStack align="start" gap={2}>
                  {medicamentos_injetaveis.map((med) => (
                    <MedicamentoCheckbox key={med} nome={med} />
                  ))}
                </VStack>
              </Card.Body>
            </Card.Root>

            <Card.Root variant="outline" size="sm">
              <Card.Header bg="blue.100">
                <Text fontWeight="semibold" color="blue.800">
                  Anticoagulantes Orais
                </Text>
              </Card.Header>
              <Card.Body>
                <VStack align="start" gap={2}>
                  {medicamentos_orais.map((med) => (
                    <MedicamentoCheckbox key={med} nome={med} />
                  ))}
                </VStack>
              </Card.Body>
            </Card.Root>

            <Card.Root variant="outline" size="sm">
              <Card.Header bg="yellow.100">
                <Text fontWeight="semibold" color="yellow.800">
                  Outros Medicamentos
                </Text>
              </Card.Header>
              <Card.Body>
                <VStack align="start" gap={2}>
                  {medicamentos_outros.map((med) => (
                    <MedicamentoCheckbox key={med} nome={med} />
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