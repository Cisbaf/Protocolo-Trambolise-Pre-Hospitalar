




import { Flex, Show, Table, Text } from "@chakra-ui/react"
import React from "react";
import { HiOutlineDocumentSearch } from "react-icons/hi";
import type { AvcDataValues } from "../../forms/formPaginationAvc/schemas/AvcData";
import { formatDateTimeBR } from "../../utils/dateUtils";


interface TableAvcProps {
    avcValues: AvcDataValues[];
    renderExtraCols?: React.ReactNode[];
    renderRow?: (item: AvcDataValues, index: number) => React.ReactNode;
    showDetail?: (protocol: string) => void;
}

export default function TableAvc({avcValues, renderExtraCols, renderRow, showDetail}: TableAvcProps) {
    return (
       <Show when={(avcValues.length > 0)} fallback={
        <Text color="gray.600">Nenhuma solicitação para listar no momento.</Text>
       }>
        <Table.Root size="sm" striped>
                <Table.Header>
                    <Table.Row>
                    <Table.ColumnHeader>N° Ocorrencia</Table.ColumnHeader>
                    <Table.ColumnHeader>Unidade</Table.ColumnHeader>
                    <Table.ColumnHeader>Municipio</Table.ColumnHeader>
                    <Table.ColumnHeader>Abertura do Chamado</Table.ColumnHeader>
                    <Table.ColumnHeader>Preenchido em</Table.ColumnHeader>
                    {renderExtraCols?.map((col, index)=> (
                        <Table.ColumnHeader key={`col-${index}`}>{col}</Table.ColumnHeader>
                    ))}
                    {showDetail && <Table.ColumnHeader textAlign="end">Detalhes</Table.ColumnHeader>}
                    </Table.Row>
                </Table.Header>
                <Table.Body>
                    {avcValues.map((item, index) => (
                        <Table.Row key={item.id}>
                            <Table.Cell>{item.LinhaDoTempoSection.numeroOcorrencia}</Table.Cell>
                            <Table.Cell>{item.UnidadeReferenciaSection.unidadeReferenciaEleita}</Table.Cell>
                            <Table.Cell>{item.LinhaDoTempoSection.municipio}</Table.Cell>
                            <Table.Cell>{formatDateTimeBR(item.LinhaDoTempoSection.aberturaChamado)}</Table.Cell>
                            <Table.Cell>{formatDateTimeBR(item.dataCriacao)}</Table.Cell>
                            {renderRow && renderRow(item, index)}
                            {showDetail && (
                            <Table.Cell textAlign="end">
                                <Flex justifyContent={"flex-end"}>
                                    <HiOutlineDocumentSearch 
                                        cursor={"pointer"}
                                        onClick={()=>showDetail(item.LinhaDoTempoSection.numeroOcorrencia)}
                                        size={26} />
                                </Flex>
                            </Table.Cell>
                            )}
                        </Table.Row>    
                    ))}
                </Table.Body>
            </Table.Root>
       </Show>
    )

}