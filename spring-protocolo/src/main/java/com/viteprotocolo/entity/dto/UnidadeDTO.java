package com.viteprotocolo.entity.dto;

import com.viteprotocolo.entity.emb.Unidade;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * DTO for {@link Unidade}
 */
@Builder
public record UnidadeDTO(
        @NotBlank
        String unidadeReferenciaEleita,
        @NotNull
        LocalDateTime horarioNotificacaoUnidade
) implements Serializable {
}