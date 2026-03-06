package com.viteprotocolo.entity;

import com.viteprotocolo.entity.emb.*;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Protocolo {

    @Id
    private String id;
    private LocalDateTime dataCriacao;
    @Embedded
    private Desfecho desfecho;
    @Embedded
    private Historia historia;
    @Embedded
    private LinhaDoTempo linhaDoTempo;
    @Embedded
    private Neurologica neurologica;
    @Embedded
    private Parametros parametros;
    @Embedded
    private Unidade unidade;
    @Embedded
    private ParecerFinal parecerFinal;

    @PrePersist
    public void gerarIdSeNaoExistir() {
        if (this.id == null) {
            this.id = java.time.format.DateTimeFormatter.ofPattern("yyMMddHHmmss")
                    .format(java.time.LocalDateTime.now())
                    + (int) (Math.random() * 90 + 10);
        }
    }
}
