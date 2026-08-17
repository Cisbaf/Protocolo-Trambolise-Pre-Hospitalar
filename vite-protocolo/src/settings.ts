const VITE_API_URL = import.meta.env.VITE_API_URL;
const VITE_MODE = import.meta.env.VITE_MODE;

const protocolo = window.location.protocol; // ex: "https:"
const host = window.location.host; // ex: "avc.cisbaf.org.br" ou "192.168.1.10:8019"
const baseUrl = `${protocolo}//${host}`;

/**
 * Em dev o backend roda numa porta separada, entao a origem nao pode ser a
 * da propria pagina. Mas fixar "localhost" quebra o acesso pela rede interna:
 * a pagina aberta em http://192.168.x.x:5174 mandaria as requisicoes para o
 * localhost de quem esta testando, e nao para esta maquina.
 *
 * Aproveitamos so a PORTA do VITE_API_URL e usamos o host da pagina. Alem de
 * apontar para o backend certo, isso mantem a API no mesmo site da pagina
 * (SameSite ignora a porta), que e' o que o cookie de sessao exige: ele e'
 * gravado com SameSite=Strict, entao em contexto cross-site o navegador
 * simplesmente nao o reenvia e /auth/me responde 401.
 */
function apiUrlDev(): string {
  try {
    const url = new URL(VITE_API_URL);
    url.protocol = protocolo;
    url.hostname = window.location.hostname;
    return url.origin;
  } catch {
    // VITE_API_URL ausente ou malformado: mantem o comportamento anterior
    return VITE_API_URL;
  }
}

export const BaseURL = VITE_MODE == "dev" ? apiUrlDev() : baseUrl
