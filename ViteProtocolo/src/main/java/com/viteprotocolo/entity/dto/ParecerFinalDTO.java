package com.viteprotocolo.entity.dto;

import lombok.Builder;

import java.io.Serializable;
import java.util.List;

/**
 * DTO for {@link com.viteprotocolo.entity.emb.ParecerFinal}
 */
@Builder
public record ParecerFinalDTO(
        String elegibilidade,
        List<String> motivos
) implements Serializable {
}