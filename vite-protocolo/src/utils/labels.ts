

const label_cirurgia = "Cirurgias de grande porte (< 3 semanas)";

const label_avc = "AVC Prévio (< 3 meses)";

const medicamentos_injetaveis = [
    "Dalteparina (Fragmin®)",
    "Enoxaparina (Lovenox®)",
    "Tinzaparina (Innohep®)",
    "Fondaparinux (Arixtra®)",
    "Heparina",
    "Nadroparina (Fraxiparina®)",
];

const medicamentos_orais = [
    "Varfarina (Varfine®)",
    "Acenocumarol (Sintrom®)",
    "Fenindiona",
    "Dabigatrana (Pradaxa®)",
    "Rivaroxabana (Xarelto®)",
    "Apixabana (Eliquis®)",
    "Edoxabana (Lixiana®)",
];

const medicamentos_outros = ["AAS", "Clopidogrel"];

const lista_doencas = [
  "Hipertensão",
  "Diabetes",
  "AVC Prévio (< 3 meses)",
  "Cirurgias de grande porte (< 3 semanas)",
] as const;

export {
    label_cirurgia,
    label_avc,
    medicamentos_injetaveis,
    medicamentos_orais,
    medicamentos_outros,
    lista_doencas
}