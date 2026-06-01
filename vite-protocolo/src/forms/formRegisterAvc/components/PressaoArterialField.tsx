import {
  HStack,
  Input,
  Text,
  Field,
} from "@chakra-ui/react";
import { Controller } from "react-hook-form";
import { useAvcFormContext } from "../../../context/AvcFormContext";

export function PressaoArterialField() {
  const { form } = useAvcFormContext();

  return (
    <Field.Root
      invalid={
        !!form.formState.errors.ParametrosClinicosSection?.pressaoArterial
      }
    >
      <Field.Label>Pressão Arterial (PA)</Field.Label>

      <Controller
        control={form.control}
        name="ParametrosClinicosSection.pressaoArterial"
        defaultValue=""
        render={({ field }) => {
          const [sistolica = "", diastolica = ""] = (
            field.value || "/"
          ).split("/");

          return (
            <HStack gap={2}>
              <Input
                type="number"
                min={0}
                placeholder="120"
                value={sistolica}
                onChange={(e) => {
                  field.onChange(
                    `${e.target.value}/${diastolica}`
                  );
                }}
              />

              <Text fontSize="lg">/</Text>

              <Input
                type="number"
                min={0}
                placeholder="80"
                value={diastolica}
                onChange={(e) => {
                  field.onChange(
                    `${sistolica}/${e.target.value}`
                  );
                }}
              />
            </HStack>
          );
        }}
      />

      <Field.ErrorText>
        {
          form.formState.errors.ParametrosClinicosSection
            ?.pressaoArterial?.message
        }
      </Field.ErrorText>
    </Field.Root>
  );
}