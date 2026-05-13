package com.viteprotocolo.auth.service;

import com.viteprotocolo.auth.entity.DTO.LoginRequest;
import com.viteprotocolo.auth.entity.DTO.UserRequest;
import com.viteprotocolo.auth.entity.Municipios;
import com.viteprotocolo.auth.entity.RefreshToken;
import com.viteprotocolo.auth.entity.Role;
import com.viteprotocolo.auth.entity.UserEntity;
import com.viteprotocolo.auth.repository.AdminRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.Normalizer;

@Service
@RequiredArgsConstructor
public class InOutService {
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;
    private final RefreshTokenService refreshTokenService;
    private final AdminRepository adminRepository;



    public void register(UserRequest request) {
        String encoded = passwordEncoder.encode(request.password());

        var entity = UserEntity.builder()
                .username(request.username())
                .password(encoded)
                .role(Role.valueOf(request.role().toUpperCase()))
                .build();

        if(request.municipio() != null && request.municipio().length() >= 3) {
            var normalizado = Normalizer.normalize(request.municipio(), Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
            var muni = Municipios.valueOf(normalizado.trim().toUpperCase().substring(0, 3));
            entity.setMunicipios(muni);
        }

        adminRepository.save(entity);
    }

    public void login(LoginRequest request, HttpServletResponse response) {
        final UserEntity admin = adminRepository.findByUsername(request.username())
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );

        final UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        assert userDetails != null;

        String role = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse("ROLE_USER");

        var accessToken = jwtTokenUtil.generateToken(userDetails.getUsername(), role);

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(admin);

        // Cookie do JWT
        JwtRequestFilter.addCookie(response, JwtRequestFilter.JWT_AUTH_COOKIE_NAME, accessToken,
                Math.toIntExact(jwtTokenUtil.getExpirationTime()), true, false);

        JwtRequestFilter.addCookie(response, "refreshToken", refreshToken.getToken(),
                15552000, true, false);

        JwtRequestFilter.addCookie(response, "role", role,
                Math.toIntExact(jwtTokenUtil.getExpirationTime()), false, false);
    }
}
