package com.viteprotocolo.protocolo.entity.emb;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Embeddable
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LinhaDoTempo {

    private String numeroOcorrencia;
    private String municipio;
    private LocalDateTime aberturaChamado;
    private LocalDateTime chegadaCena;
    private LocalDateTime ultimoHorarioVistoBem;
    private String janelaEstimada;
}
