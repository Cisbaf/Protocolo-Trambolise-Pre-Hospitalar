package com.viteprotocolo.auth.service;

import com.viteprotocolo.auth.entity.RefreshToken;
import com.viteprotocolo.auth.entity.UserEntity;
import com.viteprotocolo.auth.repository.RefreshTokenRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    @Value("${jwt.refresh.expiration}")
    private Long refreshTokenDurationMs;

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenUtil jwtTokenUtil;

    public RefreshToken createRefreshToken(UserEntity usuario) {
        RefreshToken refreshToken = refreshTokenRepository.findByUsuario(usuario)
                .orElseGet(RefreshToken::new);

        refreshToken.setUsuario(usuario);
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
        refreshToken.setToken(UUID.randomUUID().toString());

        return refreshTokenRepository.save(refreshToken);
    }

    public ResponseEntity<Map<String, String>> findByToken(HttpServletRequest request) {
        var token = getRefreshCookieValue(request);
        return refreshTokenRepository.findByToken(token)
                .map(this::verifyExpiration)
                .map(RefreshToken::getUsuario)
                .map(user -> {
                    String accessToken = jwtTokenUtil.generateToken(user.getUsername(), user.getRole().toString());
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

    private String getRefreshCookieValue(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("refreshToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}