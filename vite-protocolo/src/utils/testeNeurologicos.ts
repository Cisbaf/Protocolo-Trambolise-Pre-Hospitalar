

export const testesNeurologicos = [
  {
    key: "balance",
    nome: "Balance (Equilíbrio)",
    descricao: "Observe se há dificuldade para manter-se em pé ou andar",
  },
  {
    key: "eyes",
    nome: "Eyes (Visão)",
    descricao: "Pergunte se há visão dupla, perda visual ou alteração súbita",
  },
  {
    key: "desvioFacial",
    nome: "Desvio Facial",
    descricao: "Peça para sorrir ou mostrar dentes",
  },
  {
    key: "quedaBraco",
    nome: "Queda do Braço",
    descricao: "Olhos fechados, braços estendidos (10s)",
  },
  {
    key: "falaAnormal",
    nome: "Fala Anormal",
    descricao: `"O rato roeu a roupa do rei..."`,
  },
] as const;

export const respostasAvaliacoes = {
    items: [
        { label: "Normal", value: "normal"},
        { label: "Alterado", value: "alterado"},
    ]
}