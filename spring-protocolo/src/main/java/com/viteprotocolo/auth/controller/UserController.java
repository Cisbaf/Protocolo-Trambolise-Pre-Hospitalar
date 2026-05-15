package com.viteprotocolo.auth.controller;

import com.viteprotocolo.auth.entity.DTO.LoginRequest;
import com.viteprotocolo.auth.entity.DTO.UserRequest;
import com.viteprotocolo.auth.service.InOutService;
import com.viteprotocolo.auth.service.JwtRequestFilter;
import com.viteprotocolo.auth.service.RefreshTokenService;
import com.viteprotocolo.auth.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final RefreshTokenService refreshTokenService;
    private final InOutService inOutService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody @Valid UserRequest request) {
        if (userService.existsByUsername(request.username())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Usuário já cadastrado");
        } else {
            inOutService.register(request);
            return ResponseEntity.ok("Usuário cadastrado com sucesso!! " + request.username());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody @Valid LoginRequest request, HttpServletResponse response) {
        try {
            inOutService.login(request, response);
            return ResponseEntity.ok("Login feito com sucesso!!");

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Usuário ou senha inválidos");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro interno no servidor" + e.getMessage());
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof UserDetails userDetails) {
            return ResponseEntity.ok().body(
                    Map.of(
                            "username", userDetails.getUsername(),
                            "role", userDetails.getAuthorities()
                                    .stream()
                                    .map(GrantedAuthority::getAuthority)
                                    .findFirst()
                                    .orElse("")
                    )
            );
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletResponse response) {
        JwtRequestFilter.removeCookie(response, JwtRequestFilter.JWT_AUTH_COOKIE_NAME);
        JwtRequestFilter.removeCookie(response, JwtRequestFilter.JWT_REFRESH_COOLIE_NAME);
        return ResponseEntity.ok("Logout realizado com sucesso");
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(HttpServletRequest request) {
        return refreshTokenService.findByToken(request);
    }
}
