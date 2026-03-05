package com.viteprotocolo.entity.dto;

import jakarta.validation.constraints.*;
import lombok.Builder;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * DTO for {@link com.viteprotocolo.entity.emb.Historia}
 */
@Builder
public record HistoriaDTO(
        @NotNull
        @Positive
        @Min(1)
        int idade,
        @NotNull
        @NotEmpty
        Map<String, Boolean> doencas,
        @NotNull
        @NotEmpty
        List<String> medicamentos
) implements Serializable {
}