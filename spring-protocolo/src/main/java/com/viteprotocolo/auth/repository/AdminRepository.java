package com.viteprotocolo.auth.repository;

import com.viteprotocolo.auth.entity.AdminEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRepository extends JpaRepository<AdminEntity, Long> {
    boolean existsByUsername(String username);

    AdminEntity findByUsername(String username);
}
