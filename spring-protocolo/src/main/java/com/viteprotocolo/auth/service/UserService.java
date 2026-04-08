package com.viteprotocolo.auth.service;

import com.viteprotocolo.auth.entity.UserEntity;
import com.viteprotocolo.auth.entity.UserRequest;
import com.viteprotocolo.auth.repository.UserRepository;
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
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    public void register(UserRequest request) {
        String encoded = passwordEncoder.encode(request.password());
        userRepository.save(UserEntity.builder().username(request.username()).password(encoded).build());
    }

    public boolean existsByUsername(String username) {
        if (username == null || username.isEmpty()) {
            throw new IllegalArgumentException("O nome do usuário não pode estar vazio");
        }
        return userRepository.existsByUsername(username);
    }

    @Override
    public UserDetails loadUserByUsername(@NonNull String username) throws UsernameNotFoundException {
        UserEntity user = userRepository.findAll()
                .stream()
                .filter(u -> u.getUsername().equals(username))
                .findFirst()
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return new User(username, user.getPassword(), new java.util.ArrayList<>());
    }
}
