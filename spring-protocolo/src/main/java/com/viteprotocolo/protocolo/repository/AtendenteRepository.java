package com.viteprotocolo.protocolo.repository;

import com.viteprotocolo.protocolo.entity.AtendenteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AtendenteRepository extends JpaRepository<AtendenteEntity, Long>{

    Optional<AtendenteEntity> findByCpf(String cpf);
}
