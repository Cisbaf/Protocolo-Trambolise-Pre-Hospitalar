package com.viteprotocolo.atendimento.entity.dto.AtendimentoPre;

import com.viteprotocolo.auth.entity.DTO.UserResponse;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record PreResponse (
        Long id,
         String numeroOcorrencia,
         String municipio,
         LocalDateTime aberturaChamado,
         LocalDateTime criadoPreAtt,
         UserResponse admin
){
}
