package com.viteprotocolo.atendimento.entity.dto.atendimento;

import com.viteprotocolo.atendimento.entity.AtendenteEntity;
import com.viteprotocolo.atendimento.entity.Atendimento;
import com.viteprotocolo.atendimento.entity.dto.*;
import lombok.Builder;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO for {@link Atendimento}
 */
@Builder
public record AtendimentoResponse(
        String id,
        String cpf_atendente,
        LocalDateTime dataCriacao,
        DesfechoDTO DesfechoCenaSection,
        HistoriaDTO HistoriaClinicaSection,
        LinhaDoTempoDTO LinhaDoTempoSection,
        NeurologicaDTO AvaliacaoNeurologicaSection,
        ParametrosDTO ParametrosClinicosSection,
        UnidadeDTO UnidadeReferenciaSection,
        ParecerFinalDTO ParecerFinalSection,
        Long preId,
        AtendenteEntity atendente


) implements Serializable {
}