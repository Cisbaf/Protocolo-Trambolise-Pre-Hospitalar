import React from "react";
import { useWatch } from "react-hook-form";
import { useGet } from "../hooks/useGet";
import { BaseURL } from "../settings";
import { useAvcDataPaginationForm } from "../hooks/useAvcDataPaginationForm";
import { useDynamicFilter } from "../hooks/useDynamicFilter";
import { LoadingOverlay } from "../componentes/ui/loading";

export interface SortField {
    label: string;
    value: string;
}

export const AVC_SORT_FIELDS: SortField[] = [
    { label: "Preenchido em", value: "dataCriacao" },
    { label: "Abertura do Chamado", value: "linhaDoTempo.aberturaChamado" },
    { label: "N° Ocorrência", value: "linhaDoTempo.numeroOcorrencia" },
];

export type SortDirection = "ASC" | "DESC";

interface AvcManagerType {
    form: ReturnType<typeof useAvcDataPaginationForm>;
    filter: ReturnType<typeof useDynamicFilter>;
    loading: boolean;
    /** Recarrega a página atual da listagem (depois de editar ou excluir). */
    refetch: () => void;
    sort: string;
    direction: SortDirection;
    /** Altera o campo e/ou a direção da ordenação e volta para a primeira página. */
    setSort: (sort: string, direction: SortDirection) => void;
}

interface AvcManagerProps {
    children: any;
}

const AvcManagerContext = React.createContext<AvcManagerType | null>(null);

export function AvcManagerProvider({children}: AvcManagerProps) {
    const formData = useAvcDataPaginationForm();
    const filter = useDynamicFilter({
    fields: [
        { label: "N° Ocorrência", search_key: "numeroOcorrencia", type_field: "text" },
        { label: "Unidade", search_key: "nomeUnidade", type_field: "text" },
        { label: "Data Abertura chamado", search_key: "aberturaChamado", type_field: "date"}
    ],
    });

    const { control, reset, setValue } = formData;

    const currentPage = useWatch({
        control,
        name: "number",
    });

    const currentSize = useWatch({
        control,
        name: "size",
    });

    const [sort, setSortField] = React.useState<string>(AVC_SORT_FIELDS[0].value);
    const [direction, setDirection] = React.useState<SortDirection>("DESC");

    const setSort = (newSort: string, newDirection: SortDirection) => {
        setValue("number", 0);
        setSortField(newSort);
        setDirection(newDirection);
    };

    const { data, loading, refetch  } = useGet({
        url: `${BaseURL}/protocolo/params`,
        params: {
            page: currentPage,
            size: currentSize,
            sort,
            direction,
            ...filter.queryParamsObject
        },
        autoFetch: true
    })

    React.useEffect(()=>{
        if (!data) return;
        reset(data);
    }, [data])

    return(
        <AvcManagerContext.Provider value={{form: formData, loading, filter, refetch, sort, direction, setSort}}>
            <LoadingOverlay isOpen={loading}/>
            {children}
        </AvcManagerContext.Provider>
    )
}

export function useAvcManagerContext() {
  const context = React.useContext(AvcManagerContext)

  if (!context) {
    throw new Error(
      "useAvcManager must be used inside AvcManagerProvider"
    )
  }

  return context;
}