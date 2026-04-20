import {
    Field,
    Input,
} from "@chakra-ui/react";
import { useAvcManagerContext } from "../../context/AvcManagerContext";
import React from "react";

export default function ControlTableView() {
    const { form } = useAvcManagerContext();
    const { setValue, watch } = form;

    const formValue = watch("size");

    const [localValue, setLocalValue] = React.useState<number | string>(formValue ?? "");

    // sincroniza com mudanças externas
    React.useEffect(() => {
        setLocalValue(formValue ?? "");
    }, [formValue]);

    // debounce
    React.useEffect(() => {
        if (localValue === "") return;

        const timer = setTimeout(() => {
            const parsed = Number(localValue);

            if (!isNaN(parsed) && parsed > 0) {
                setValue("number", 0);
                setValue("size", parsed);
            }
        }, 1000);

        return () => clearTimeout(timer);
    }, [localValue, setValue]);

    return (
        <Field.Root maxW="220px">
            <Input
                type="number"
                value={localValue}
                onChange={(e) => setLocalValue(e.target.value)}
                min={1}
            />
            <Field.HelperText fontSize="xs">
                Quantidade de registros exibidos por página
            </Field.HelperText>
        </Field.Root>
    );
}