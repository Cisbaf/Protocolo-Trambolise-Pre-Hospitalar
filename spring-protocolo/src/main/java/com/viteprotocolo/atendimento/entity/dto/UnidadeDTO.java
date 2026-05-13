package com.viteprotocolo.atendimento.entity.dto;

import com.viteprotocolo.atendimento.entity.emb.Unidade;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.io.Serializable;
import java.time.LocalDateTime;

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