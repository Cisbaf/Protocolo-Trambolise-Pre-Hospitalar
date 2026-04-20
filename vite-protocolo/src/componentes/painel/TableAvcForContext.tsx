import { useWatch } from "react-hook-form";
import { useAvcManagerContext } from "../../context/AvcManagerContext";
import TableAvc from "./TableAvc";
import { useAvcSelectedContext } from "../../context/AvcSelected";
import { Checkbox, Flex } from "@chakra-ui/react";

export default function TableAvcForContext() {
    const { form } = useAvcManagerContext();
    const { useAvc } = useAvcSelectedContext();
    const { add, remove, getById } = useAvc;

    const avcList = useWatch({
        control: form.control,
        name: "content"
    });

    return (
        <TableAvc
            avcValues={avcList}
            renderExtraCols={["Selecionar"]}
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
    );
}