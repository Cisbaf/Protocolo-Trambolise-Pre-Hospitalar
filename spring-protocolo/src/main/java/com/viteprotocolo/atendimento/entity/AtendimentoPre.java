package com.viteprotocolo.atendimento.entity;

import com.viteprotocolo.auth.entity.AdminEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AtendimentoPre {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String numeroOcorrencia;
    private String municipio;
    private LocalDateTime aberturaChamado;
    private LocalDateTime criadoPreAtt;

    @ManyToOne
    @JoinColumn(name = "admin_id")
    private AdminEntity admin;
}
