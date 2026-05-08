package com.viteprotocolo.atendimento.entity.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO for {@link com.viteprotocolo.atendimento.entity.emb.Desfecho}
 */
@Builder
public record DesfechoDTO(
        @NotNull
        LocalDateTime horarioSaidaCena,
        @NotNull
        LocalDateTime horarioChegadaHospital
) implements Serializable {
}