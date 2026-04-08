package com.viteprotocolo.protocolo.entity.dto;

import com.viteprotocolo.protocolo.entity.emb.Parametros;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.io.Serializable;

/**
 * DTO for {@link Parametros}
 */
@Builder
public record ParametrosDTO(
        @NotNull
        Long glicemia,
        @NotBlank
        String pressaoArterial,
        @NotNull
        int saturacao
) implements Serializable {
}