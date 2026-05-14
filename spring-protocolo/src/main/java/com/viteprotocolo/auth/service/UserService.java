package com.viteprotocolo.auth.service;

import com.viteprotocolo.auth.entity.UserEntity;
import com.viteprotocolo.auth.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final AdminRepository adminRepository;

    public boolean existsByUsername(String username) {
        if (username == null || username.isEmpty()) {
            throw new IllegalArgumentException("O nome do usuário não pode estar vazio");
        }
        return adminRepository.existsByUsername(username);
    }

    public UserEntity findByUsername(String username) {
        if (username == null || username.isEmpty()) {
            return null;
        }
        return adminRepository.findByUsername(username).orElse(null);
    }

    @Override
    public UserDetails loadUserByUsername(@NonNull String username) throws UsernameNotFoundException {
        UserEntity user = adminRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + username));
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(user.getRole().toString()));
        return new User(username, user.getPassword(), authorities);
    }
}
