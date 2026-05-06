package com.viteprotocolo.protocolo.entity.preDto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ProtocoloPreRequest(
        String numeroOcorrencia,
        String municipio,
        LocalDateTime aberturaChamado
) {
}
