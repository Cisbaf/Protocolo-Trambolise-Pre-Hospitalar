import React from "react";
import { useFilter } from "./useFilter";

export interface FilterField {
  label: string;
  search_key: string;
  type_field: "text" | "date" | "number";
}

interface UseDynamicFilterProps {
  fields: FilterField[];
}

export function useDynamicFilter({ fields }: UseDynamicFilterProps) {
  const { filters, setFilter, removeFilter, clearFilters, queryString, queryParamsObject } = useFilter({});

  // adiciona filtro baseado no search_key
  const addFilter = (search_key: string, value: string) => {
    const fieldExists = fields.find((f) => f.search_key === search_key);

    if (!fieldExists) {
      console.warn(`Campo ${search_key} não existe nos filtros`);
      return;
    }

    setFilter(search_key, value);
  };

  // remover usando search_key
  const removeDynamicFilter = (search_key: string) => {
    removeFilter(search_key);
  };

  // útil pra UI (exibir label junto com valor)
  const mappedFilters = React.useMemo(() => {
    return filters.map((f) => {
      const field = fields.find((field) => field.search_key === f.field);

      return {
        ...f,
        label: field?.label ?? f.field,
        type: field?.type_field,
      };
    });
  }, [filters, fields]);

  return {
    filters: mappedFilters,
    rawFilters: filters,
    addFilter,
    removeFilter: removeDynamicFilter,
    clearFilters,
    queryString,
    availableFields: fields,
    fields,
    queryParamsObject
  };
}