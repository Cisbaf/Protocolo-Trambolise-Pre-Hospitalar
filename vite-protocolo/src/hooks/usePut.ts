import { useState } from "react";

type UsePutOptions<T> = {
  url: string;
  onSuccess?: (data: T) => void;
  onError?: (error: any) => void;
};

/**
 * PUT autenticado. Diferente do usePost, manda o cookie de sessão
 * (credentials: "include"), porque o backend exige login para alterar.
 */
export function usePut<T = any>({ url, onSuccess, onError }: UsePutOptions<T>) {
  const [data, setData] = useState<T | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<any>(null);

  const put = async (body: any) => {
    setLoading(true);
    setError(null);

    try {
      const response = await fetch(url, {
        method: "PUT",
        credentials: "include",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(body),
      });

      if (!response.ok) {
        throw {
          message:
            response.status === 404
              ? "Registro não encontrado. Ele pode ter sido apagado por outra pessoa."
              : response.status === 403
              ? "Sessão expirada. Entre novamente para salvar."
              : "Não foi possível salvar as alterações.",
          status: response.status,
        };
      }

      const result = await response.json();

      setData(result);
      onSuccess?.(result);
      return result;
    } catch (err) {
      setError(err);
      onError?.(err);
    } finally {
      setLoading(false);
    }
  };

  return { put, data, loading, error };
}
