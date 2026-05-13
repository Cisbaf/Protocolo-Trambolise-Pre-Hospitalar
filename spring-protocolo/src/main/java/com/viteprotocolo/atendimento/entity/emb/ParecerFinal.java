package com.viteprotocolo.atendimento.entity.emb;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
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
    @CollectionTable(joinColumns = @JoinColumn(name = "protocolo_id"))
    private List<String> motivos;
}
