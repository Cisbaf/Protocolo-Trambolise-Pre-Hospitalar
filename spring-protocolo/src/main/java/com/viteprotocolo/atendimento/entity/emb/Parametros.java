package com.viteprotocolo.atendimento.entity.emb;

import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Parametros {
    private Long glicemia;
    private String pressaoArterial;
    private Integer saturacao;
}
