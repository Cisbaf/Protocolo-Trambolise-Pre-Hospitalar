import type { DataFormValues } from "../forms/DataForm";

const VITE_API_URL = import.meta.env.VITE_API_URL;
const VITE_MODE = import.meta.env.VITE_MODE;

const protocolo = window.location.protocol; // ex: "https:"

// Obter host (domínio ou IP + porta)
const host = window.location.host; // ex: "avc.cisbaf.org.br" ou "192.168.1.10:8019"

const baseUrl = `${protocolo}//${host}`;

interface ApiResponse<T> {
  success: boolean;
  data?: T;
  message?: string;
}

export default async function SendFormBackend<T = any>(
  data: DataFormValues
): Promise<ApiResponse<T>> {
  try {
    const url = VITE_MODE == "dev"? VITE_API_URL : baseUrl
    const response = await fetch(`${url}/protocolo`, {
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