import React from "react";

interface Filter {
  field: string;
  value: string;
}

interface UseFilterProps {
  initialFilters?: Filter[];
}

export function useFilter({ initialFilters = [] }: UseFilterProps) {
  const [filters, setFilters] = React.useState<Filter[]>(initialFilters);

  // adicionar ou atualizar filtro
  const setFilter = (field: string, value: string) => {
    setFilters((prev) => {
      const exists = prev.find((f) => f.field === field);

      if (exists) {
        return prev.map((f) =>
          f.field === field ? { ...f, value } : f
        );
      }

      return [...prev, { field, value }];
    });
  };

  // remover filtro específico
  const removeFilter = (field: string) => {
    setFilters((prev) => prev.filter((f) => f.field !== field));
  };

  // limpar tudo
  const clearFilters = () => {
    setFilters([]);
  };

  // gerar query params
  const queryString = React.useMemo(() => {
    const params = new URLSearchParams();

    filters.forEach((f) => {
      if (f.value) {
        params.append(f.field, f.value);
      }
    });

    return params.toString();
  }, [filters]);

  const queryParamsObject = React.useMemo(() => {
    const obj: Record<string, string> = {};

    filters.forEach((f) => {
      if (f.value) {
        obj[f.field] = f.value;
      }
    });

    return obj;
  }, [filters]);

  return {
    filters,
    setFilter,
    removeFilter,
    clearFilters,
    queryString,
    queryParamsObject
  };
}