package com.viteprotocolo.entity.dto.protocolo;

import com.viteprotocolo.entity.dto.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;

/**
 * DTO for {@link com.viteprotocolo.entity.Protocolo}
 */
public record ProtocoloRequest(
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
        ParecerFinalDTO ParecerFinalSection
) implements Serializable {
}