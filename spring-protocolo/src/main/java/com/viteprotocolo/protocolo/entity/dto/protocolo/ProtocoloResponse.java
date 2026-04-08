package com.viteprotocolo.entity.dto.protocolo;

import com.viteprotocolo.entity.dto.*;
import lombok.Builder;

import java.io.Serializable;

/**
 * DTO for {@link com.viteprotocolo.entity.Protocolo}
 */
@Builder
public record ProtocoloResponse(
        String id,
        DesfechoDTO DesfechoCenaSection,
        HistoriaDTO HistoriaClinicaSection,
        LinhaDoTempoDTO LinhaDoTempoSection,
        NeurologicaDTO AvaliacaoNeurologicaSection,
        ParametrosDTO ParametrosClinicosSection,
        UnidadeDTO UnidadeReferenciaSection,
        ParecerFinalDTO ParecerFinalSection

) implements Serializable {
}