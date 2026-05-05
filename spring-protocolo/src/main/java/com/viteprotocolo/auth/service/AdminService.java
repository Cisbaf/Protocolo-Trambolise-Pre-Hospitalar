package com.viteprotocolo.auth.service;

import com.viteprotocolo.auth.entity.AdminEntity;
import com.viteprotocolo.auth.entity.AdminRequest;
import com.viteprotocolo.auth.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminService implements UserDetailsService {
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;


    public void register(AdminRequest request) {
        String encoded = passwordEncoder.encode(request.password());
        adminRepository.save(AdminEntity.builder().username(request.username()).password(encoded).build());
    }

    public boolean existsByUsername(String username) {
        if (username == null || username.isEmpty()) {
            throw new IllegalArgumentException("O nome do usuário não pode estar vazio");
        }
        return adminRepository.existsByUsername(username);
    }

    public AdminEntity findByUsername(String username) {
        if (username == null || username.isEmpty()) {
            return null;
        }
        return adminRepository.findByUsername(username);
    }

    @Override
    public UserDetails loadUserByUsername(@NonNull String username) throws UsernameNotFoundException {
        AdminEntity user = adminRepository.findAll()
                .stream()
                .filter(u -> u.getUsername().equals(username))
                .findFirst()
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return new User(username, user.getPassword(), new java.util.ArrayList<>());
    }
}
