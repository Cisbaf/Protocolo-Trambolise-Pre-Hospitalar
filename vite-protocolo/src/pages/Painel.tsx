import { Flex } from "@chakra-ui/react";
import PaginationForContext from "../componentes/painel/PaginationForContext";
import TableAvcForContext from "../componentes/painel/TableAvcForContext";
import { AvcManagerProvider } from "../context/AvcManagerContext";
import AvcFilter from "../componentes/painel/AvcFilter";
import { AvcSelectedProvider } from "../context/AvcSelected";
import TagsSelecteds from "../componentes/painel/TagsSelecteds";
import ControlTableView from "../componentes/painel/ControlTableView";

export default function PainelPage() {

    return (
        <AvcSelectedProvider>
            <AvcManagerProvider>
                <Flex
                    direction="column"
                    gap={5}>
                    <AvcFilter/>
                    <TagsSelecteds/>
                    <TableAvcForContext/>
                     <Flex justifyContent={"space-between"}>
                        <PaginationForContext/>
                        <ControlTableView/>
                    </Flex>
                </Flex>
                {/* <ControlTableView/> */}
            
            </AvcManagerProvider>
        </AvcSelectedProvider>
    )
}