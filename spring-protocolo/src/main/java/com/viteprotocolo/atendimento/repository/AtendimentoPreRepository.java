package com.viteprotocolo.atendimento.repository;

import com.viteprotocolo.atendimento.entity.AtendimentoPre;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface AtendimentoPreRepository extends JpaRepository<AtendimentoPre, Long>, JpaSpecificationExecutor<AtendimentoPre> {

    @Query("SELECT p FROM AtendimentoPre p WHERE p.municipio = :municipio " +
            "AND p.id NOT IN (SELECT pr.preId FROM Atendimento pr WHERE pr.preId IS NOT NULL)")
    Page<AtendimentoPre> findPendentesByMunicipio(String municipio, Pageable pageable);
}
