package com.viteprotocolo.atendimento.entity.preDto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record AtendimentoPreRequest(
        String numeroOcorrencia,
        String municipio,
        LocalDateTime aberturaChamado
) {
}
