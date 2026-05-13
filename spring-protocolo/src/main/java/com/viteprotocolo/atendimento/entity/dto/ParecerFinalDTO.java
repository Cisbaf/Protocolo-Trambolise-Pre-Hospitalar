package com.viteprotocolo.atendimento.entity.dto;

import lombok.Builder;

import java.io.Serializable;
import java.util.List;

/**
 * DTO for {@link com.viteprotocolo.atendimento.entity.emb.ParecerFinal}
 */
@Builder
public record ParecerFinalDTO(
        String elegibilidade,
        List<String> motivos
) implements Serializable {
}