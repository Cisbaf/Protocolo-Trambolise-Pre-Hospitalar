import type { DataFormValues } from "../forms/DataForm";

const VITE_API_URL = import.meta.env.VITE_API_URL;

interface ApiResponse<T> {
  success: boolean;
  data?: T;
  message?: string;
}

export default async function SendFormBackend<T = any>(
  data: DataFormValues
): Promise<ApiResponse<T>> {
  try {
    const response = await fetch(`${VITE_API_URL}/protocolo`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(data),
    });

    const responseJson = await response.json();

    if (!response.ok) {
      return {
        success: false,
        message: responseJson?.message || "Erro ao enviar formulário",
      };
    }

    return {
      success: true,
      data: responseJson,
    };
  } catch (error) {
    return {
      success: false,
      message: "Erro de conexão com o servidor",
    };
  }
}