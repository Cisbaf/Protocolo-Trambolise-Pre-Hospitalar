package com.viteprotocolo.auth.service;

import com.viteprotocolo.auth.entity.AdminEntity;
import com.viteprotocolo.auth.entity.AdminRequest;
import com.viteprotocolo.auth.entity.Role;
import com.viteprotocolo.auth.repository.AdminRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
// AdminServiceTest.java

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @Mock
    private AdminRepository adminRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AdminService service;

    private AdminEntity adminEntity;

    @BeforeEach
    void setUp() {
        adminEntity = AdminEntity.builder()
                .username("admin")
                .password("encodedPass")
                .role(Role.ADMIN)
                .build();
    }

    // -------------------------------------------------------------------------
    // register
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Deve encodar a senha e salvar o AdminEntity")
    void register_encodesPasswordAndSaves() {
        AdminRequest req = new AdminRequest("admin", "senha123");
        when(passwordEncoder.encode("senha123")).thenReturn("encodedPass");

        service.register(req);

        ArgumentCaptor<AdminEntity> captor = ArgumentCaptor.forClass(AdminEntity.class);
        verify(adminRepository).save(captor.capture());
        assertEquals("admin", captor.getValue().getUsername());
        assertEquals("encodedPass", captor.getValue().getPassword());
    }

    @Test
    @DisplayName("Não deve salvar a senha em texto puro")
    void register_neverStoresPlainPassword() {
        AdminRequest req = new AdminRequest("admin", "senha123");
        when(passwordEncoder.encode("senha123")).thenReturn("$2a$encodedHash");

        service.register(req);

        ArgumentCaptor<AdminEntity> captor = ArgumentCaptor.forClass(AdminEntity.class);
        verify(adminRepository).save(captor.capture());
        assertNotEquals("senha123", captor.getValue().getPassword());
    }

    // -------------------------------------------------------------------------
    // existsByUsername
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Deve lançar IllegalArgumentException quando o username for nulo")
    void existsByUsername_nullUsername_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> service.existsByUsername(null));
        verifyNoInteractions(adminRepository);
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException quando o username estiver vazio")
    void existsByUsername_emptyUsername_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> service.existsByUsername(""));
        verifyNoInteractions(adminRepository);
    }

    @Test
    @DisplayName("Deve retornar true quando o username já existir no repositório")
    void existsByUsername_existingUser_returnsTrue() {
        when(adminRepository.existsByUsername("admin")).thenReturn(true);

        assertTrue(service.existsByUsername("admin"));
    }

    @Test
    @DisplayName("Deve retornar false quando o username não existir no repositório")
    void existsByUsername_nonExistingUser_returnsFalse() {
        when(adminRepository.existsByUsername("novo")).thenReturn(false);

        assertFalse(service.existsByUsername("novo"));
    }

    // -------------------------------------------------------------------------
    // findByUsername
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Deve retornar null quando o username for nulo")
    void findByUsername_nullUsername_returnsNull() {
        assertNull(service.findByUsername(null));
        verifyNoInteractions(adminRepository);
    }

    @Test
    @DisplayName("Deve retornar null quando o username estiver vazio")
    void findByUsername_emptyUsername_returnsNull() {
        assertNull(service.findByUsername(""));
        verifyNoInteractions(adminRepository);
    }

    @Test
    @DisplayName("Deve retornar o AdminEntity quando o username existir")
    void findByUsername_existingUser_returnsEntity() {
        when(adminRepository.findByUsername("admin")).thenReturn(adminEntity);

        AdminEntity result = service.findByUsername("admin");

        assertNotNull(result);
        assertEquals("admin", result.getUsername());
    }

    @Test
    @DisplayName("Deve retornar null quando o repositório não encontrar o username")
    void findByUsername_nonExistingUser_returnsNull() {
        when(adminRepository.findByUsername("fantasma")).thenReturn(null);

        assertNull(service.findByUsername("fantasma"));
    }

    // -------------------------------------------------------------------------
    // loadUserByUsername
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Deve retornar UserDetails quando o username existir")
    void loadUserByUsername_existingUser_returnsUserDetails() {
        when(adminRepository.findAll()).thenReturn(List.of(adminEntity));

        UserDetails result = service.loadUserByUsername("admin");

        assertNotNull(result);
        assertEquals("admin", result.getUsername());
        assertEquals("encodedPass", result.getPassword());
    }

    @Test
    @DisplayName("Deve lançar UsernameNotFoundException quando o username não existir")
    void loadUserByUsername_nonExistingUser_throwsUsernameNotFoundException() {
        when(adminRepository.findAll()).thenReturn(List.of(adminEntity));

        assertThrows(UsernameNotFoundException.class,
                () -> service.loadUserByUsername("desconhecido"));
    }

    @Test
    @DisplayName("Deve lançar UsernameNotFoundException quando o repositório estiver vazio")
    void loadUserByUsername_emptyRepository_throwsUsernameNotFoundException() {
        when(adminRepository.findAll()).thenReturn(List.of());

        assertThrows(UsernameNotFoundException.class,
                () -> service.loadUserByUsername("qualquer"));
    }
}