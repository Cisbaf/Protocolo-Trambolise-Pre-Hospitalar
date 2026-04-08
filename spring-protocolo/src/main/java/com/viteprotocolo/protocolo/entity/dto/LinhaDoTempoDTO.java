package com.viteprotocolo.protocolo.entity.dto;

import com.viteprotocolo.protocolo.entity.emb.LinhaDoTempo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO for {@link LinhaDoTempo}
 */
@Builder
public record LinhaDoTempoDTO(
        @NotBlank
        @NotNull
        String numeroOcorrencia,
        @NotBlank
        String municipio,
        @NotNull
        LocalDateTime aberturaChamado,
        @NotNull
        LocalDateTime chegadaCena,

        LocalDateTime ultimoHorarioVistoBem,

        String janelaEstimada
) implements Serializable {
}