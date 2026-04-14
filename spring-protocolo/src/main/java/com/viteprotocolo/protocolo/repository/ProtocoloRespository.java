package com.viteprotocolo.protocolo.repository;

import com.viteprotocolo.protocolo.entity.Protocolo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface ProtocoloRespository extends JpaRepository<Protocolo, String>, JpaSpecificationExecutor<Protocolo> {

    Optional<Protocolo> findById(String id);
}
