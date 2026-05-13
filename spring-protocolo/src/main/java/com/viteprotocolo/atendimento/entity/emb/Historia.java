package com.viteprotocolo.atendimento.entity.emb;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.util.List;
import java.util.Map;

@Embeddable
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Historia {
    private Integer idade;
    private Boolean usoCoagulanteEm48h;
    @ElementCollection
    private List<String> medicamentos;
    @ElementCollection
    private Map<String,Boolean> doencas;
}
