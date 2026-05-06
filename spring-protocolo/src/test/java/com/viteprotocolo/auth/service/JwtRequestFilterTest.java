package com.viteprotocolo.auth.service;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtRequestFilterTest {

    @Mock
    private JwtTokenUtil jwtTokenUtil;
    @Mock
    private UserDetailsService userDetailsService;

    @InjectMocks
    private JwtRequestFilter filter;

    private HttpServletRequest request;
    private HttpServletResponse response;
    private FilterChain filterChain;

    @BeforeEach
    void setUp() {
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        filterChain = mock(FilterChain.class);
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    // -------------------------------------------------------------------------
    // doFilterInternal
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Deve continuar o filtro sem autenticar quando não houver token")
    void doFilterInternal_noToken_continuesChainUnauthenticated() throws Exception {
        when(request.getHeader("Authorization")).thenReturn(null);
        when(request.getCookies()).thenReturn(null);

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    @DisplayName("Deve extrair o JWT do header Authorization com prefixo Bearer")
    void doFilterInternal_bearerHeader_extractsAndAuthenticates() throws Exception {
        UserDetails userDetails = new User("admin", "pass", List.of());

        when(request.getHeader("Authorization")).thenReturn("Bearer valid-jwt");
        when(jwtTokenUtil.getUsernameFormToken("valid-jwt")).thenReturn("admin");
        when(userDetailsService.loadUserByUsername("admin")).thenReturn(userDetails);
        when(jwtTokenUtil.validateToken("valid-jwt")).thenReturn(true);

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals("admin",
                ((UserDetails) Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication().getPrincipal()))
                        .getUsername());
    }

    @Test
    @DisplayName("Deve extrair o JWT do cookie quando não houver header Authorization")
    void doFilterInternal_cookieToken_extractsAndAuthenticates() throws Exception {
        Cookie jwtCookie = new Cookie(JwtRequestFilter.JWT_AUTH_COOKIE_NAME, "cookie-jwt");
        UserDetails userDetails = new User("admin", "pass", List.of());

        when(request.getHeader("Authorization")).thenReturn(null);
        when(request.getCookies()).thenReturn(new Cookie[]{jwtCookie});
        when(jwtTokenUtil.getUsernameFormToken("cookie-jwt")).thenReturn("admin");
        when(userDetailsService.loadUserByUsername("admin")).thenReturn(userDetails);
        when(jwtTokenUtil.validateToken("cookie-jwt")).thenReturn(true);

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    @DisplayName("Deve preferir o header Bearer ao cookie quando ambos estiverem presentes")
    void doFilterInternal_bothHeaderAndCookie_prefersHeader() throws Exception {
        Cookie jwtCookie = new Cookie(JwtRequestFilter.JWT_AUTH_COOKIE_NAME, "cookie-jwt");
        UserDetails userDetails = new User("admin", "pass", List.of());

        when(request.getHeader("Authorization")).thenReturn("Bearer header-jwt");
        when(jwtTokenUtil.getUsernameFormToken("header-jwt")).thenReturn("admin");
        when(userDetailsService.loadUserByUsername("admin")).thenReturn(userDetails);
        when(jwtTokenUtil.validateToken("header-jwt")).thenReturn(true);

        filter.doFilterInternal(request, response, filterChain);

        verify(jwtTokenUtil, never()).getUsernameFormToken("cookie-jwt");
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("Deve continuar o filtro sem autenticar quando o token for inválido")
    void doFilterInternal_invalidToken_continuesChainUnauthenticated() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer invalid-jwt");
        when(jwtTokenUtil.getUsernameFormToken("invalid-jwt"))
                .thenThrow(new RuntimeException("Token inválido"));

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    @DisplayName("Deve limpar o contexto e continuar o filtro quando loadUserByUsername falhar")
    void doFilterInternal_loadUserFails_clearsContextAndContinues() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer valid-jwt");
        when(jwtTokenUtil.getUsernameFormToken("valid-jwt")).thenReturn("admin");
        when(userDetailsService.loadUserByUsername("admin"))
                .thenThrow(new UsernameNotFoundException("User not found"));

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    @DisplayName("Deve ignorar token não-Bearer no header Authorization")
    void doFilterInternal_nonBearerHeader_fallsThroughToCookieLookup() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Basic dXNlcjpwYXNz");
        when(request.getCookies()).thenReturn(null);

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    @DisplayName("Não deve re-autenticar quando já houver autenticação no SecurityContext")
    void doFilterInternal_alreadyAuthenticated_skipsAuthentication() throws Exception {
        UserDetails existingUser = new User("jaAutenticado", "pass", List.of());
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(existingUser, null, existingUser.getAuthorities()));

        when(request.getHeader("Authorization")).thenReturn("Bearer some-jwt");
        when(jwtTokenUtil.getUsernameFormToken("some-jwt")).thenReturn("jaAutenticado");

        filter.doFilterInternal(request, response, filterChain);

        verify(userDetailsService, never()).loadUserByUsername(any());
        verify(filterChain).doFilter(request, response);
    }

    // -------------------------------------------------------------------------
    // getCookieValue
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Deve retornar null quando não houver cookies")
    void getCookieValue_noCookies_returnsNull() {
        when(request.getCookies()).thenReturn(null);

        assertNull(filter.getCookieValue(request));
    }

    @Test
    @DisplayName("Deve retornar null quando o cookie correto não estiver presente")
    void getCookieValue_wrongCookieName_returnsNull() {
        Cookie wrong = new Cookie("outro_cookie", "valor");
        when(request.getCookies()).thenReturn(new Cookie[]{wrong});

        assertNull(filter.getCookieValue(request));
    }

    @Test
    @DisplayName("Deve retornar o valor do cookie 'auth_token' quando estiver presente")
    void getCookieValue_correctCookie_returnsValue() {
        Cookie correct = new Cookie(JwtRequestFilter.JWT_AUTH_COOKIE_NAME, "jwt-value");
        Cookie noise = new Cookie("outro", "x");
        when(request.getCookies()).thenReturn(new Cookie[]{noise, correct});

        assertEquals("jwt-value", filter.getCookieValue(request));
    }

    // -------------------------------------------------------------------------
    // addCookie (método estático)
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Deve adicionar cookie com todos os atributos corretos")
    void addCookie_setsAllAttributes() {
        HttpServletResponse resp = mock(HttpServletResponse.class);
        ArgumentCaptor<Cookie> captor = ArgumentCaptor.forClass(Cookie.class);

        JwtRequestFilter.addCookie(resp, "auth_token", "jwt-value", 3600, true, false);

        verify(resp).addCookie(captor.capture());
        Cookie cookie = captor.getValue();
        assertEquals("auth_token", cookie.getName());
        assertEquals("jwt-value", cookie.getValue());
        assertEquals("/", cookie.getPath());
        assertTrue(cookie.isHttpOnly());
        assertFalse(cookie.getSecure());
        assertEquals(3600, cookie.getMaxAge());
    }

    // -------------------------------------------------------------------------
    // removeCookie (método estático)
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Deve remover o cookie definindo maxAge como 0 e valor nulo")
    void removeCookie_setsMaxAgeZeroAndNullValue() {
        HttpServletResponse resp = mock(HttpServletResponse.class);
        ArgumentCaptor<Cookie> captor = ArgumentCaptor.forClass(Cookie.class);

        JwtRequestFilter.removeCookie(resp, "auth_token");

        verify(resp).addCookie(captor.capture());
        Cookie cookie = captor.getValue();
        assertEquals("auth_token", cookie.getName());
        assertNull(cookie.getValue());
        assertEquals(0, cookie.getMaxAge());
        assertTrue(cookie.isHttpOnly());
        assertEquals("/", cookie.getPath());
    }
}