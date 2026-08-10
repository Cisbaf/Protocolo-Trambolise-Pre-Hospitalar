package com.viteprotocolo.protocolo.entity.emb;

import jakarta.persistence.Column;
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
    private int idade;
    private boolean usoCoagulanteEm48h;
    @ElementCollection
    private List<String> medicamentos;
    @ElementCollection
    private Map<String,Boolean> doencas;
    /**
     * Boolean (nao boolean primitivo) de proposito: registros criados antes
     * deste campo existir devem ficar em branco (null) em vez de "nao".
     */
    private Boolean usaOutrasMedicacoes;
    @Column(columnDefinition = "TEXT")
    private String outrasMedicacoesDescricao;
}
