package com.viteprotocolo.auth.controller;

import com.viteprotocolo.auth.entity.UserRequest;
import com.viteprotocolo.auth.service.JwtRequestFilter;
import com.viteprotocolo.auth.service.JwtTokenUtil;
import com.viteprotocolo.auth.service.UserService;
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

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody @Valid UserRequest request) {
        if (userService.existsByUsername(request.username())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Usuário já cadastrado");
        } else {
            userService.register(request);
            return ResponseEntity.ok("Usuário cadastrado com sucesso!! " + request.username());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody @Valid UserRequest request, HttpServletResponse response) {
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.username(), request.password()));
            final UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            assert userDetails != null;
            var accessToken = jwtTokenUtil.generateToken(userDetails.getUsername());
            JwtRequestFilter.addCookie(response, JwtRequestFilter.JWT_COOKIE_NAME, accessToken, Math.toIntExact(jwtTokenUtil.getExpirationTime()), true, false);
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
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
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
        JwtRequestFilter.removeCookie(response, JwtRequestFilter.JWT_COOKIE_NAME);
        return ResponseEntity.ok("Logout realizado com sucesso");
    }
}
