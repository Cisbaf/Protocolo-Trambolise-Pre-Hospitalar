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
import { z } from "zod";

// Esquema exportado para uso no formulário pai
export const HistoriaClinicaSchema = z.object({
  idade: z
    .number({ message: "Informe a idade do paciente" })
    .positive("Idade deve ser um número positivo"),
  doencas: z.object({
    Hipertensão: z.boolean(),
    Diabetes: z.boolean(),
    "AVC Prévio (< 3 meses)": z.boolean(),
    "Cirurgias (< 3 meses)": z.boolean(),
  }),
  medicamentos: z.array(z.string()).optional(),
});

export function HistoriaClinicaSection() {
  const { form } = useGroupContext();
  const { register, setValue, watch, formState: { errors } } = form;

  // Estado local para o "Nega Uso" (não vai para o schema)
  const [negaUso, setNegaUso] = React.useState(false);

  // Observa o array atual de medicamentos
  const medicamentos = watch("HistoriaClinicaSection.medicamentos") || [];

  // Lista completa de medicamentos (apenas os nomes)
  const injetaveis = [
    "Dalteparina (Fragmin®)",
    "Enoxaparina (Lovenox®)",
    "Tinzaparina (Innohep®)",
    "Fondaparinux (Arixtra®)",
    "Heparina",
    "Nadroparina (Fraxiparina®)",
  ];

  const orais = [
    "Varfarina (Varfine®)",
    "Acenocumarol (Sintrom®)",
    "Fenindiona",
    "Dabigatrana (Pradaxa®)",
    "Rivaroxabana (Xarelto®)",
    "Apixabana (Eliquis®)",
    "Edoxabana (Lixiana®)",
  ];

  const outros = ["ASS", "Clopidogrel"];

  // Função para alternar a seleção de um medicamento
  const toggleMedicamento = (med: string, checked: boolean) => {
    const novoArray = checked
      ? [...medicamentos, med]
      : medicamentos.filter((m) => m !== med);
    setValue("HistoriaClinicaSection.medicamentos", novoArray, { shouldDirty: true });
  };

  // Manipulador do "Nega Uso" – limpa os medicamentos e desabilita os checkboxes
  const handleNegaUsoChange = () => {
    const novoValor = !negaUso;
    setNegaUso(novoValor);
    if (novoValor) {
      setValue("HistoriaClinicaSection.medicamentos", [], { shouldDirty: true });
    }
  };

  // Componente reutilizável para cada checkbox de medicamento
  const MedicamentoCheckbox = ({ nome }: { nome: string }) => (
    <Checkbox.Root
      key={nome}
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
    <Box>
      <VStack align="stretch" gap={8}>
        {/* ================= Idade ================= */}
        <Field.Root maxW="200px" invalid={!!errors.HistoriaClinicaSection?.idade}>
          <Field.Label>Idade do Paciente</Field.Label>
          <Input
            type="number"
            size="sm"
            {...register("HistoriaClinicaSection.idade", { valueAsNumber: true })}
          />
          {errors.HistoriaClinicaSection?.idade && (
            <Field.ErrorText>{errors.HistoriaClinicaSection?.idade.message}</Field.ErrorText>
          )}
        </Field.Root>

        {/* ================= Histórico ================= */}
        <Box>
          <Text fontWeight="semibold" mb={3}>
            Histórico de Doenças
          </Text>
          <Grid templateColumns="repeat(2, 1fr)" gap={4}>
            {([
              "Hipertensão",
              "Diabetes",
              "AVC Prévio (< 3 meses)",
              "Cirurgias (< 3 meses)",
            ] as const ).map((item) => (
              <Checkbox.Root key={item}>
                <Checkbox.HiddenInput {...register(`HistoriaClinicaSection.doencas.${item}`)} />
                <Checkbox.Control />
                <Checkbox.Label>{item}</Checkbox.Label>
              </Checkbox.Root>
            ))}
          </Grid>
        </Box>

        <Box>
            <Flex gap={5} alignItems="center" mb={5}>
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

            <Flex gap={10}>

              <Card.Root variant="outline" size="sm">
                <Card.Header bg="green.100">
                  <Text fontWeight="semibold" color="green.800">
                    Anticoagulantes Injetáveis
                  </Text>
                </Card.Header>
                <Card.Body>
                  <VStack align="start" gap={2}>
                    {injetaveis.map((med) => (
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
                    {orais.map((med) => (
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
                    {outros.map((med) => (
                      <MedicamentoCheckbox key={med} nome={med} />
                    ))}
                  </VStack>
                </Card.Body>
              </Card.Root>

            </Flex>
        </Box>

        {/* Exibição de erro para medicamentos, se necessário */}
        {errors.HistoriaClinicaSection?.medicamentos && (
          <Text color="red.500" fontSize="sm">
            {errors.HistoriaClinicaSection.medicamentos.message}
          </Text>
        )}
      </VStack>
    </Box>
  );
}