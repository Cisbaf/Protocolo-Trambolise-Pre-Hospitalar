package com.viteprotocolo.protocolo.repository;

import com.viteprotocolo.protocolo.entity.Protocolo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProtocoloRespository extends JpaRepository<Protocolo, String> {
    List<Protocolo> findByUnidade_UnidadeReferenciaEleitaContaining(String nomeUnidade);
    List<Protocolo> findByLinhaDoTempo_NumeroOcorrencia(String numeroOcorrencia);
    Optional<Protocolo> findById(String id);
}
