package com.viteprotocolo.protocolo.repository;

import com.viteprotocolo.protocolo.entity.ProtocoloPre;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface ProtocoloPreRepository extends JpaRepository<ProtocoloPre, Long>, JpaSpecificationExecutor<ProtocoloPre> {

    @Query("SELECT p FROM ProtocoloPre p WHERE p.municipio = :municipio " +
            "AND p.id NOT IN (SELECT pr.preId FROM Protocolo pr WHERE pr.preId IS NOT NULL)")
    Page<ProtocoloPre> findPendentesByMunicipio(String municipio, Pageable pageable);
}
