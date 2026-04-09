import type { AvcFormValues } from "../forms/formRegisterAvc/schemas/AvcFormSchema";
import { label_avc, label_cirurgia, medicamentos_outros } from "../forms/formRegisterAvc/utils/labels";
import calcularDiferencaEmHorasEMinutos, { parseDatetimeLocal } from "../utils/dateUtils";

interface ParecerFinalResponse {
  elegibilidade: "elegivel" | "inelegivel";
  motivos: string[];
}

export default function ParecerFinalHelper(
  avcFormData: AvcFormValues
): ParecerFinalResponse {

  const motivos: string[] = [];

  /* ===============================
     🔎 Extração dos dados necessários
  =============================== */

  const chegadaCena = parseDatetimeLocal(
    avcFormData.LinhaDoTempoSection.chegadaCena
  );

  const vistoBem = parseDatetimeLocal(
    avcFormData.LinhaDoTempoSection.ultimoHorarioVistoBem
  );

  const idade = avcFormData.HistoriaClinicaSection.idade;

  const avaliacoesNeurologicas = Object.values(
    avcFormData.AvaliacaoNeurologicaSection ?? {}
  );

  const historico_doencas = avcFormData.HistoriaClinicaSection.doencas;

  const medicamentos_selecionados = avcFormData.HistoriaClinicaSection.medicamentos;

  /* ===============================
     1️⃣ Regra do tempo
  =============================== */

  if (!vistoBem) {
    motivos.push("Horário 'Visto Bem' não informado.");
  }

  if (!chegadaCena) {
    motivos.push("Horário do atendimento da equipe SAMU não informado.");
  }

  if (vistoBem && chegadaCena) {
    const diferenca = calcularDiferencaEmHorasEMinutos(
      vistoBem,
      chegadaCena
    );

    if (diferenca.totalMinutos < 0) {
      motivos.push(
        "'Visto Bem' não pode ser posterior ao atendimento da equipe SAMU."
      );
    }

    if (diferenca.totalMinutos > 270) {
      motivos.push(
        "Tempo entre Visto Bem e Chegada na Cena superior a 4h30."
      );
    }
  }

  /* ===============================
     2️⃣ Regra da idade
  =============================== */

  if (!idade) {
    motivos.push("Defina a idade do paciente.");
  } else if (idade < 18) {
    motivos.push("Paciente menor de 18 anos.");
  }

  /* ===============================
     3️⃣ Regra neurológica
  =============================== */

  const temAlterado = avaliacoesNeurologicas.some(
    (avaliacao) => avaliacao === "alterado"
  );

  if (!temAlterado) {
    motivos.push("Necessário pelo menos uma avaliação neurológica 'Alterado'.");
  }

    /* ===============================
     Regra Doenças
  =============================== */
    if (historico_doencas["AVC Prévio (< 3 meses)"]){
      motivos.push(label_avc)
    };

    if (historico_doencas["Cirurgias de grande porte (< 3 semanas)"]){
      motivos.push(label_cirurgia)
    };

    /* ===============================
      Regra Medicamentos
    =============================== */
    if (
      medicamentos_selecionados.length > 0 &&
      medicamentos_selecionados.some(
        (med) => !medicamentos_outros.includes(med)
      )
    ) motivos.push("Uso de Anticoagulantes.");

    /* ===============================
      Regra FEZ USO DE ANTICOAGULANTE A MENOS DE 48h ?
    =============================== */

    if (avcFormData.HistoriaClinicaSection.uso_coagulante_em_48h) {
      motivos.push("Fez uso de anticoagulante a menos de 48 horas.")
    }
  
  return {
    elegibilidade: motivos.length === 0 ? "elegivel" : "inelegivel",
    motivos,
  };
}