import { useState } from "react";

type UseDeleteOptions = {
  url: string;
  onSuccess?: () => void;
  onError?: (error: any) => void;
};

/**
 * DELETE autenticado. A exclusão é definitiva no backend, então a
 * confirmação fica por conta de quem chama.
 */
export function useDelete({ url, onSuccess, onError }: UseDeleteOptions) {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<any>(null);

  const remove = async () => {
    setLoading(true);
    setError(null);

    try {
      const response = await fetch(url, {
        method: "DELETE",
        credentials: "include",
      });

      if (!response.ok) {
        throw {
          message:
            response.status === 404
              ? "Registro não encontrado. Ele já pode ter sido apagado."
              : response.status === 403
              ? "Sessão expirada. Entre novamente para excluir."
              : "Não foi possível excluir o registro.",
          status: response.status,
        };
      }

      onSuccess?.();
      return true;
    } catch (err) {
      setError(err);
      onError?.(err);
      return false;
    } finally {
      setLoading(false);
    }
  };

  return { remove, loading, error };
}
