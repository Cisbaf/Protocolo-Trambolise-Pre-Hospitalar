import React from "react";
import { useWatch } from "react-hook-form";
import { useAvcManagerContext } from "../../context/AvcManagerContext";
import TableAvc from "./TableAvc";
import AvcDetailModal from "./AvcDetailModal";
import { useAvcSelectedContext } from "../../context/AvcSelected";
import { Checkbox, Flex } from "@chakra-ui/react";
import type { AvcDataValues } from "../../forms/formPaginationAvc/schemas/AvcData";

export default function TableAvcForContext() {
    const { form, refetch } = useAvcManagerContext();
    const { useAvc } = useAvcSelectedContext();
    const { add, remove, getById } = useAvc;

    const [detalhe, setDetalhe] = React.useState<AvcDataValues | null>(null);

    const avcList = useWatch({
        control: form.control,
        name: "content"
    });

    return (
        <>
            <TableAvc
                avcValues={avcList}
                renderExtraCols={["Selecionar"]}
                showDetail={(item) => setDetalhe(item)}
                renderRow={(item) => {
                    const isChecked = !!getById(item.id);

                    return (
                        <Flex padding={3} bg={"gray.100"} justifyContent={"center"}>
                            <Checkbox.Root
                            checked={isChecked}
                            onCheckedChange={(e) => {
                                if (e.checked) add(item);
                                else remove(item.id);
                            }}
                             >
                            <Checkbox.HiddenInput />
                            <Checkbox.Control />
                        </Checkbox.Root>
                        </Flex>
                    );
                }}
            />

            <AvcDetailModal
                item={detalhe}
                onClose={() => setDetalhe(null)}
                onChanged={() => {
                    // um registro alterado ou apagado não pode continuar
                    // na seleção com os dados antigos
                    if (detalhe) remove(detalhe.id);
                    refetch();
                }}
            />
        </>
    );
}
