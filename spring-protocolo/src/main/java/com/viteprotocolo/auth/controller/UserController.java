package com.viteprotocolo.auth.controller;

import com.viteprotocolo.auth.entity.AdminEntity;
import com.viteprotocolo.auth.entity.AdminRequest;
import com.viteprotocolo.auth.entity.RefreshToken;
import com.viteprotocolo.auth.service.JwtRequestFilter;
import com.viteprotocolo.auth.service.JwtTokenUtil;
import com.viteprotocolo.auth.service.AdminService;
import com.viteprotocolo.auth.service.RefreshTokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class UserController {

    private final AdminService adminService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody @Valid AdminRequest request) {
        if (adminService.existsByUsername(request.username())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Usuário já cadastrado");
        } else {
            adminService.register(request);
            return ResponseEntity.ok("Usuário cadastrado com sucesso!! " + request.username());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody @Valid AdminRequest request, HttpServletResponse response) {
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.username(), request.password()));
            final UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            assert userDetails != null;
            final AdminEntity admin = adminService.findByUsername(request.username());

            var accessToken = jwtTokenUtil.generateToken(userDetails.getUsername());
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(admin);

            JwtRequestFilter.addCookie(response, JwtRequestFilter.JWT_AUTH_COOKIE_NAME, accessToken, Math.toIntExact(jwtTokenUtil.getExpirationTime()), true, false);
            JwtRequestFilter.addCookie(response, "refreshToken", refreshToken.getToken(),
                    15552000, true, false);

            return ResponseEntity.ok("Login feito com sucesso!!");

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Usuário ou senha inválidos");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro interno no servidor");
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
                    "username", userDetails.getUsername()
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
