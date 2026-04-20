

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


const options_coagulantes = [
    { label: "SIM", value: true },
    { label: "NÃO", value: false }
]

const municipios = [
    { label: "BELFORD ROXO", value: "belford roxo" },
    { label: "DUQUE DE CAXIAS", value: "duque de caxias" },
    { label: "ITAGUAÍ", value: "itaguai" },
    { label: "JAPERI", value: "japeri" },
    { label: "MAGÉ", value: "mage" },
    { label: "MESQUITA", value: "mesquita" },
    { label: "NILÓPOLIS", value: "nilopolis" },
    { label: "NOVA IGUAÇU", value: "nova iguacu" },
    { label: "PARACAMBI", value: "paracambi" },
    { label: "QUEIMADOS", value: "queimados" },
    { label: "SÃO JOÃO DE MERITI", value: "sao joao de meriti" },
    { label: "SEROPÉDICA", value: "seropedica" },
  ]

export {
    label_cirurgia,
    label_avc,
    medicamentos_injetaveis,
    medicamentos_orais,
    medicamentos_outros,
    lista_doencas,
    options_coagulantes,
    municipios
}