package com.viteprotocolo.protocolo.entity.dto;

import lombok.Builder;

import java.io.Serializable;
import java.util.List;

/**
 * DTO for {@link com.viteprotocolo.protocolo.entity.emb.ParecerFinal}
 */
@Builder
public record ParecerFinalDTO(
        String elegibilidade,
        List<String> motivos
) implements Serializable {
}