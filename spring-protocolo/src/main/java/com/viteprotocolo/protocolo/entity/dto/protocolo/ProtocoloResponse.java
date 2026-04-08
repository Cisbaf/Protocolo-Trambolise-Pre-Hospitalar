package com.viteprotocolo.protocolo.entity.dto.protocolo;

import com.viteprotocolo.protocolo.entity.dto.*;
import lombok.Builder;

import java.io.Serializable;

/**
 * DTO for {@link com.viteprotocolo.protocolo.entity.Protocolo}
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