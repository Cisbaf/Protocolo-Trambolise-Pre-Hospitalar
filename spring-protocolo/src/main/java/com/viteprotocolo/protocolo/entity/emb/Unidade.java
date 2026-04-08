package com.viteprotocolo.protocolo.entity.emb;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.time.LocalDateTime;

@Embeddable
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Unidade {
    private String unidadeReferenciaEleita;
    private LocalDateTime horarioNotificacaoUnidade;
}
