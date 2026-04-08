package com.viteprotocolo.protocolo.entity.dto;

import jakarta.validation.constraints.*;
import lombok.Builder;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * DTO for {@link com.viteprotocolo.protocolo.entity.emb.Historia}
 */
@Builder
public record HistoriaDTO(
        @NotNull
        @Positive
        @Min(1)
        int idade,
        boolean uso_coagulante_em_48h,
        @NotNull
        @NotEmpty
        Map<String, Boolean> doencas,
        List<String> medicamentos
) implements Serializable {
}