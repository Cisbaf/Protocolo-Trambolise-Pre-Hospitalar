package com.viteprotocolo.auth.service;

import com.viteprotocolo.auth.entity.AdminEntity;
import com.viteprotocolo.auth.entity.RefreshToken;
import com.viteprotocolo.auth.repository.RefreshTokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Service
@AllArgsConstructor
@NoArgsConstructor
public class RefreshTokenService {

    @Value("${jwt.refresh.expiration}")
    private Long refreshTokenDurationMs;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;
    @Autowired
    private JwtRequestFilter jwtRequestFilter;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;


    public RefreshToken createRefreshToken(AdminEntity usuario) {
        RefreshToken refreshToken = new RefreshToken();

        refreshToken.setUsuario(usuario);
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
        refreshToken.setToken(UUID.randomUUID().toString()); // Token aleatório e único

        return refreshTokenRepository.save(refreshToken);
    }

    public ResponseEntity<Map<String, String>> findByToken(HttpServletRequest request) {
        var token = jwtRequestFilter.getCookieValue(request);
        return refreshTokenRepository.findByToken(token)
                .map(this::verifyExpiration)
                .map(RefreshToken::getUsuario)
                .map(user -> {
                    String accessToken = jwtTokenUtil.generateToken(user.getUsername());
                    return ResponseEntity.ok(Map.of("accessToken", accessToken));
                })
                .orElseThrow(() -> new RuntimeException("Refresh token não encontrado!"));
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new RuntimeException("Refresh token expirado. Faça login novamente.");
        }
        return token;
    }
}