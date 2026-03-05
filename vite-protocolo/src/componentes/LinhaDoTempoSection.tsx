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
} from "@chakra-ui/react"
import React from "react"
import { useGroupContext } from "../context/GroupContext"
import { Controller } from "react-hook-form"


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
  
  React.useEffect(()=>{
    if (lkwDisabled) {
      form.setValue("LinhaDoTempoSection.ultimoHorarioVistoBem", "");
      form.setValue("LinhaDoTempoSection.janelaEstimada", "");
    }

  }, [lkwDisabled]);

  return (
    <Box>
      <VStack gap={6}>
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


        <Field.Root
          invalid={!!form.formState.errors.LinhaDoTempoSection?.atendimentoSamu}
          >
          <Field.Label>Atendimento Equipe SAMU</Field.Label>
          <Input type="datetime-local" {...form.register("LinhaDoTempoSection.atendimentoSamu")}/>
          <Field.ErrorText>
            {
              form.formState.errors
                .LinhaDoTempoSection
                ?.atendimentoSamu?.message
            }
            </Field.ErrorText>
        </Field.Root>

        <Field.Root
          invalid={!!form.formState.errors.LinhaDoTempoSection?.chegadaCena}
        >
          <Field.Label>Chegada na Cena</Field.Label>
            <Input type="datetime-local" {...form.register("LinhaDoTempoSection.chegadaCena")}/>
            <Field.ErrorText>
              {
                form.formState.errors
                  .LinhaDoTempoSection
                  ?.chegadaCena?.message
              }
            </Field.ErrorText>
        </Field.Root>

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