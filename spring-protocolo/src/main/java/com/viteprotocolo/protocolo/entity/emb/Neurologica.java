package com.viteprotocolo.protocolo.entity.emb;

import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Neurologica {
    private String desvioFacial;
    private String quedaBraco;
    private String falaAnormal;
    private String eyes;
    private String balance;
}
