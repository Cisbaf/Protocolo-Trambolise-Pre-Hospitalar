import React from "react";
import type { AvcDataValues } from "../forms/formPaginationAvc/schemas/AvcData";

export default function useAvcDataList() {
  const [avcList, setAvcList] = React.useState<AvcDataValues[]>([]);

  // 🔹 Definir lista inteira (ex: vindo da API)
  const setAll = React.useCallback((docs: AvcDataValues[]) => {
    setAvcList(docs);
  }, []);

  // 🔹 Adicionar um documento
  const add = React.useCallback((doc: AvcDataValues) => {
    setAvcList((prev) => [doc, ...prev]);
  }, []);

  // 🔹 Buscar por ID 
  const getById = React.useCallback((id: string | number) => {
    return avcList.find((doc) => doc.id === id);
  }, [avcList]);

  // 🔹 Atualizar documento
  const update = React.useCallback((updatedDoc: AvcDataValues) => {
    setAvcList((prev) =>
      prev.map((doc) =>
        doc.id === updatedDoc.id ? updatedDoc : doc
      )
    );
  }, []);

  // 🔹 Remover documento
  const remove = React.useCallback((id: string | number) => {
    setAvcList((prev) =>
      prev.filter((doc) => doc.id !== id)
    );
  }, []);

    const addMany = React.useCallback((items: AvcDataValues[]) => {
      setAvcList((prev) => {
        const ids = new Set(prev.map((i) => i.id));
        const novos = items.filter((i) => !ids.has(i.id));
        return [...prev, ...novos];
      });
    }, []);

  // 🔹 Limpar lista
  const clear = React.useCallback(() => {
    setAvcList([]);
  }, []);

  return {
    avcList,
    setAll,
    add,
    getById,
    update,
    remove,
    addMany,
    clear
  };
}