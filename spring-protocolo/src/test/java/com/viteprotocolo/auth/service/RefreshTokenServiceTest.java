package com.viteprotocolo.auth.service;

import com.viteprotocolo.auth.entity.AdminEntity;
import com.viteprotocolo.auth.entity.RefreshToken;
import com.viteprotocolo.auth.repository.RefreshTokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.lang.reflect.Field;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;
    @Mock
    private JwtRequestFilter jwtRequestFilter;
    @Mock
    private JwtTokenUtil jwtTokenUtil;

    @InjectMocks
    private RefreshTokenService service;

    private AdminEntity admin;
    private RefreshToken refreshToken;

    @BeforeEach
    void setUp() throws Exception {
        Field field = RefreshTokenService.class.getDeclaredField("refreshTokenDurationMs");
        field.setAccessible(true);
        field.set(service, 86400000L); // 1 dia em ms

        admin = AdminEntity.builder()
                .username("admin")
                .password("encoded")
                .build();

        refreshToken = new RefreshToken();
        refreshToken.setToken("token-uuid");
        refreshToken.setUsuario(admin);
        refreshToken.setExpiryDate(Instant.now().plusSeconds(3600));
    }

    // -------------------------------------------------------------------------
    // createRefreshToken
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Deve criar e salvar RefreshToken com token UUID e data de expiração futura")
    void createRefreshToken_savesAndReturnsToken() {
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(refreshToken);

        RefreshToken result = service.createRefreshToken(admin);

        assertNotNull(result);
        assertEquals(admin, result.getUsuario());
        verify(refreshTokenRepository).save(any(RefreshToken.class));
    }

    @Test
    @DisplayName("Deve gerar data de expiração futura ao criar o token")
    void createRefreshToken_expiryDateIsInFuture() {
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(inv -> inv.getArgument(0));

        RefreshToken result = service.createRefreshToken(admin);

        assertTrue(result.getExpiryDate().isAfter(Instant.now()));
    }

    @Test
    @DisplayName("Deve gerar um token UUID único a cada chamada")
    void createRefreshToken_generatesUniqueToken() {
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(inv -> inv.getArgument(0));

        RefreshToken t1 = service.createRefreshToken(admin);
        RefreshToken t2 = service.createRefreshToken(admin);

        assertNotEquals(t1.getToken(), t2.getToken());
    }

    // -------------------------------------------------------------------------
    // verifyExpiration
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Deve retornar o token quando não estiver expirado")
    void verifyExpiration_validToken_returnsToken() {
        refreshToken.setExpiryDate(Instant.now().plusSeconds(3600));

        RefreshToken result = service.verifyExpiration(refreshToken);

        assertEquals(refreshToken, result);
        verify(refreshTokenRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Deve deletar o token e lançar RuntimeException quando estiver expirado")
    void verifyExpiration_expiredToken_deletesAndThrows() {
        refreshToken.setExpiryDate(Instant.now().minusSeconds(1));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.verifyExpiration(refreshToken));

        assertThat(ex.getMessage(), containsString("expirado"));
        verify(refreshTokenRepository).delete(refreshToken);
    }

    // -------------------------------------------------------------------------
    // findByToken
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Deve lançar RuntimeException quando o refresh token não for encontrado no repositório")
    void findByToken_tokenNotFound_throwsRuntimeException() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(jwtRequestFilter.getCookieValue(request)).thenReturn("token-inexistente");
        when(refreshTokenRepository.findByToken("token-inexistente")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> service.findByToken(request));
    }

    @Test
    @DisplayName("Deve retornar novo accessToken quando o refresh token for válido")
    void findByToken_validToken_returnsNewAccessToken() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(jwtRequestFilter.getCookieValue(request)).thenReturn("token-uuid");
        when(refreshTokenRepository.findByToken("token-uuid")).thenReturn(Optional.of(refreshToken));
        when(jwtTokenUtil.generateToken("admin")).thenReturn("novo-access-token");

        ResponseEntity<Map<String, String>> result =
                 service.findByToken(request);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assert result.getBody() != null;
        assertEquals("novo-access-token", result.getBody().get("accessToken"));
    }

    @Test
    @DisplayName("Deve lançar RuntimeException e deletar o token quando estiver expirado no findByToken")
    void findByToken_expiredToken_throwsAndDeletesToken() {
        refreshToken.setExpiryDate(Instant.now().minusSeconds(1));

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(jwtRequestFilter.getCookieValue(request)).thenReturn("token-uuid");
        when(refreshTokenRepository.findByToken("token-uuid")).thenReturn(Optional.of(refreshToken));

        assertThrows(RuntimeException.class, () -> service.findByToken(request));
        verify(refreshTokenRepository).delete(refreshToken);
    }
}