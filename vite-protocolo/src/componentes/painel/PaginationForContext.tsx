
import { useWatch } from "react-hook-form";
import { useAvcManagerContext } from "../../context/AvcManagerContext";
import { PaginationCP } from "./Pagination";
import React from "react";


export default function PaginationForContext() {
    const { form } = useAvcManagerContext();
    const { control, setValue } = form;

    const currentPageSize = useWatch({
        control,
        name: "size"
    })

    const currentPage = useWatch({
        control,
        name: "number"
    })

    const currentTotalElements = useWatch({
        control,
        name: "totalElements"
    })

    return <PaginationCP
        count={currentTotalElements}
        page={(currentPage ?? 0) + 1} // 👈 aqui soma 1
        pageSize={currentPageSize}
        onPageChange={(page)=>setValue("number", page - 1)}/>
}