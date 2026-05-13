package com.viteprotocolo.atendimento.entity.dto.atendimento;

import com.viteprotocolo.atendimento.entity.Atendimento;
import com.viteprotocolo.atendimento.entity.dto.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;

/**
 * DTO for {@link Atendimento}
 */
public record AtendimentoRequest(
        @NotNull
        @Valid
        DesfechoDTO DesfechoCenaSection,
        @NotNull
        @Valid
        HistoriaDTO HistoriaClinicaSection,
        @NotNull
        @Valid
        LinhaDoTempoDTO LinhaDoTempoSection,
        @NotNull
        @Valid
        NeurologicaDTO AvaliacaoNeurologicaSection,
        @NotNull
        @Valid
        ParametrosDTO ParametrosClinicosSection,
        @NotNull
        @Valid
        UnidadeDTO UnidadeReferenciaSection,
        @NotNull
        @Valid
        ParecerFinalDTO ParecerFinalSection,

        String cpf_atendente,
        Long preId

) implements Serializable {
}