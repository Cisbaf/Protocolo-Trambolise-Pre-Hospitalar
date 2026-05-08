package com.viteprotocolo.atendimento.repository;

import com.viteprotocolo.atendimento.entity.Atendimento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface AtendimentoRepository extends JpaRepository<Atendimento, String>, JpaSpecificationExecutor<Atendimento> {

    Optional<Atendimento> findById(String id);

}
