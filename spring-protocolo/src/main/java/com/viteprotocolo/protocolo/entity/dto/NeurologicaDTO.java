package com.viteprotocolo.protocolo.entity.dto;

import com.viteprotocolo.protocolo.entity.emb.Neurologica;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

import java.io.Serializable;

/**
 * DTO for {@link Neurologica}
 */
@Builder
public record NeurologicaDTO(
        @NotBlank
        String desvioFacial,
        @NotBlank
        String quedaBraco,
        @NotBlank
        String eyes,
        @NotBlank
        String balance,
        @NotBlank
        String falaAnormal
) implements Serializable {
}