package com.viteprotocolo.auth.repository;

import com.viteprotocolo.auth.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import com.viteprotocolo.auth.entity.AdminEntity;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    Optional<RefreshToken> findByUsuario(AdminEntity usuario);
}
