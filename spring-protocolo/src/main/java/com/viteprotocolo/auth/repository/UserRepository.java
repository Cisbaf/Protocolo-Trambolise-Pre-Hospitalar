package com.viteprotocolo.auth.repository;

import com.viteprotocolo.auth.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    UserEntity findByUsernameAndPassword(String username, String pass);
    boolean existsByUsername(String username);
}
