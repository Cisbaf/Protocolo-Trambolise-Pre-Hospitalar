package com.viteprotocolo.entity.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * DTO for {@link com.viteprotocolo.entity.emb.Desfecho}
 */
@Builder
public record DesfechoDTO(
        @NotNull
        LocalDateTime horarioSaidaCena,
        @NotNull
        LocalDateTime horarioChegadaHospital
) implements Serializable {
}