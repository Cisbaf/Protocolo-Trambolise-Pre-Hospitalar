"use client"

import {
  Box,
  VStack,
  Field,
  Input,
  Select,
  Portal,
  createListCollection,
  CheckboxCard,
  Grid,
} from "@chakra-ui/react"
import React from "react"
import { useGroupContext } from "../context/GroupContext"
import { Controller } from "react-hook-form"
import calcularDiferencaEmHorasEMinutos from "../utils/calcularDiferencaDate"
import { parseDatetimeLocal } from "../utils/parseDatetimeLocal"

const municipios = createListCollection({
  items: [
    { label: "BELFORD ROXO", value: "belford_roxo" },
    { label: "DUQUE DE CAXIAS", value: "duque_de_caxias" },
    { label: "ITAGUAÍ", value: "itaguai" },
    { label: "JAPERI", value: "japeri" },
    { label: "MAGÉ", value: "mage" },
    { label: "MESQUITA", value: "mesquita" },
    { label: "NILÓPOLIS", value: "nilopolis" },
    { label: "NOVA IGUAÇU", value: "nova_iguacu" },
    { label: "PARACAMBI", value: "paracambi" },
    { label: "QUEIMADOS", value: "queimados" },
    { label: "SÃO JOÃO DE MERITI", value: "sao_joao_de_meriti" },
    { label: "SEROPÉDICA", value: "seropedica" },
  ],
})

export function LinhaDoTempoSection() {
  const { form } = useGroupContext();
  const [lkwDisabled, setLkwDisabled] = React.useState(false);
  const [chegadaCena, vistoBem] = form.watch(["LinhaDoTempoSection.chegadaCena", "LinhaDoTempoSection.ultimoHorarioVistoBem"]);

  React.useEffect(()=>{
    const vistoBemDate = parseDatetimeLocal(vistoBem);
    const chegadaCenaDate = parseDatetimeLocal(chegadaCena);
    if (chegadaCenaDate && vistoBemDate) {
      const calculo = calcularDiferencaEmHorasEMinutos(vistoBemDate, chegadaCenaDate).formatado;
      form.setValue("LinhaDoTempoSection.janelaEstimada", calculo);
    }
  }, [chegadaCena, vistoBem]);


  React.useEffect(()=>{
    if (lkwDisabled) {
      form.setValue("LinhaDoTempoSection.ultimoHorarioVistoBem", "");
      form.setValue("LinhaDoTempoSection.janelaEstimada", "");
    }

  }, [lkwDisabled]);

  return (
    <Box>
      <VStack gap={6}>
         <Grid
            templateColumns={{ base: "1fr", md: "repeat(2, 1fr)" }}
            gap={6}
            w="full"
          >
            {/* Nº da Ocorrência */}
            <Field.Root
              invalid={!!form.formState.errors.LinhaDoTempoSection?.numeroOcorrencia}
            >
              <Field.Label>Nº da Ocorrência</Field.Label>
              <Input
                placeholder="Ex: 12345/2026"
                {...form.register("LinhaDoTempoSection.numeroOcorrencia")}
              />
              <Field.ErrorText>
                {
                  form.formState.errors
                    .LinhaDoTempoSection
                    ?.numeroOcorrencia?.message
                }
              </Field.ErrorText>
            </Field.Root>

            {/* Município */}
            <Field.Root
              invalid={!!form.formState.errors.LinhaDoTempoSection?.municipio}
            >
              <Field.Label>Município</Field.Label>

              <Controller
                name="LinhaDoTempoSection.municipio"
                control={form.control}
                render={({ field }) => (
                  <Select.Root
                    collection={municipios}
                    value={field.value ? [field.value] : []}
                    onValueChange={(details) => {
                      field.onChange(details.value[0])
                    }}
                  >
                    <Select.HiddenSelect />
                    <Select.Control>
                      <Select.Trigger>
                        <Select.ValueText placeholder="Selecione" />
                      </Select.Trigger>
                      <Select.IndicatorGroup>
                        <Select.Indicator />
                      </Select.IndicatorGroup>
                    </Select.Control>

                    <Portal>
                      <Select.Positioner>
                        <Select.Content>
                          {municipios.items.map((municipio) => (
                            <Select.Item
                              item={municipio}
                              key={municipio.value}
                            >
                              {municipio.label}
                              <Select.ItemIndicator />
                            </Select.Item>
                          ))}
                        </Select.Content>
                      </Select.Positioner>
                    </Portal>
                  </Select.Root>
                )}
              />

              <Field.ErrorText>
                {
                  form.formState.errors
                    .LinhaDoTempoSection
                    ?.municipio?.message
                }
              </Field.ErrorText>
            </Field.Root>
          </Grid>


        <Grid
            templateColumns={{ base: "1fr", md: "repeat(2, 1fr)" }}
            gap={6}
            w="full"
          >
            {/* Abertura */}
            <Field.Root
              invalid={!!form.formState.errors.LinhaDoTempoSection?.aberturaChamado}
            >
              <Field.Label>Abertura do chamado</Field.Label>
              <Input
                type="datetime-local"
                {...form.register("LinhaDoTempoSection.aberturaChamado")}
              />
              <Field.ErrorText>
                {
                  form.formState.errors
                    .LinhaDoTempoSection
                    ?.aberturaChamado?.message
                }
              </Field.ErrorText>
            </Field.Root>

            {/* Chegada */}
            <Field.Root
              invalid={!!form.formState.errors.LinhaDoTempoSection?.chegadaCena}
            >
              <Field.Label>Chegada na Cena</Field.Label>
              <Input
                type="datetime-local"
                {...form.register("LinhaDoTempoSection.chegadaCena")}
              />
              <Field.ErrorText>
                {
                  form.formState.errors
                    .LinhaDoTempoSection
                    ?.chegadaCena?.message
                }
              </Field.ErrorText>
            </Field.Root>
          </Grid>

        <Field.Root
          invalid={!!form.formState.errors.LinhaDoTempoSection?.ultimoHorarioVistoBem}
          >
          <Field.Label>Último Horário Visto Bem (LKW)</Field.Label>
            <CheckboxCard.Root
              maxW="240px"
              size={"sm"}
              onChange={()=> setLkwDisabled(!lkwDisabled)}
              colorPalette="teal">
              <CheckboxCard.HiddenInput />
              <CheckboxCard.Control>
                <CheckboxCard.Label>Não soube informar</CheckboxCard.Label>
                <CheckboxCard.Indicator />
              </CheckboxCard.Control>
            </CheckboxCard.Root>

          <Input
            disabled={lkwDisabled}
            {...form.register("LinhaDoTempoSection.ultimoHorarioVistoBem")}
            type="datetime-local"
          />
           <Field.ErrorText>
            {
              form.formState.errors
                .LinhaDoTempoSection
                ?.ultimoHorarioVistoBem?.message
            }
          </Field.ErrorText>
        </Field.Root>

        <Field.Root
            invalid={!!form.formState.errors.LinhaDoTempoSection?.janelaEstimada}
          >
          <Field.Label>Janela Estimada (LKW até agora)</Field.Label>
          <Input
            disabled={true}
            placeholder="Calculado automaticamente"
            {...form.register("LinhaDoTempoSection.janelaEstimada")}
            />
        </Field.Root>
      </VStack>
    </Box>
  )
}