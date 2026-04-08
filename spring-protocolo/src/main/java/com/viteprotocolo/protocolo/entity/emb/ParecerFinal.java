package com.viteprotocolo.protocolo.entity.emb;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.util.List;

@Embeddable
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ParecerFinal {
    private String elegibilidade;
    @ElementCollection
    private List<String> motivos;
}
