import React from "react";
import { useWatch } from "react-hook-form";
import { useGet } from "../hooks/useGet";
import { BaseURL } from "../settings";
import { useAvcDataPaginationForm } from "../hooks/useAvcDataPaginationForm";
import { useDynamicFilter } from "../hooks/useDynamicFilter";
import { LoadingOverlay } from "../componentes/ui/loading";

interface AvcManagerType {
    form: ReturnType<typeof useAvcDataPaginationForm>;
    filter: ReturnType<typeof useDynamicFilter>;
    loading: boolean;
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

    const { control, reset } = formData;

    const currentPage = useWatch({
        control,
        name: "number",
    });

    const currentSize = useWatch({
        control,
        name: "size",
    });

    const { data, loading  } = useGet({
        url: `${BaseURL}/protocolo/params`,
        params: {
            page: currentPage,
            size: currentSize,
            ...filter.queryParamsObject
        },
        autoFetch: true
    })

    React.useEffect(()=>{
        if (!data) return;
        reset(data);
    }, [data])

    return(
        <AvcManagerContext.Provider value={{form: formData, loading, filter}}>
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