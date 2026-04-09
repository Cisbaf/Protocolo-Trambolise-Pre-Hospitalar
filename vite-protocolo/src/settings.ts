const VITE_API_URL = import.meta.env.VITE_API_URL;
const VITE_MODE = import.meta.env.VITE_MODE;

const protocolo = window.location.protocol; // ex: "https:"
const host = window.location.host; // ex: "avc.cisbaf.org.br" ou "192.168.1.10:8019"
const baseUrl = `${protocolo}//${host}`;

export const BaseURL = VITE_MODE == "dev"? VITE_API_URL : baseUrl