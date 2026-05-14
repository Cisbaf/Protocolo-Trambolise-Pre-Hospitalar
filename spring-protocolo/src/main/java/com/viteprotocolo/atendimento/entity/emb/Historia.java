package com.viteprotocolo.atendimento.entity.emb;

import jakarta.persistence.*;
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
    @CollectionTable(joinColumns = @JoinColumn(name = "protocolo_id"))
    private List<String> medicamentos;

    @ElementCollection
    @CollectionTable(joinColumns = @JoinColumn(name = "protocolo_id"))
    private Map<String,Boolean> doencas;
}
