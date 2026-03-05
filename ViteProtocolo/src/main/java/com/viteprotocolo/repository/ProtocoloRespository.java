package com.viteprotocolo.repository;

import com.viteprotocolo.entity.Protocolo;
import com.viteprotocolo.entity.emb.LinhaDoTempo;
import com.viteprotocolo.entity.emb.Unidade;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProtocoloRespository extends JpaRepository<Protocolo, String> {
    List<Protocolo> findByUnidade_UnidadeReferenciaEleitaContaining(String nomeUnidade);
    List<Protocolo> findByLinhaDoTempo_NumeroOcorrencia(String numeroOcorrencia);
    Optional<Protocolo> findById(String id);
}
