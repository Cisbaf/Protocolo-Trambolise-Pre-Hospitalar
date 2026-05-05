package com.viteprotocolo.protocolo.service;

import com.viteprotocolo.auth.entity.Municipios;
import com.viteprotocolo.protocolo.entity.AtendenteEntity;
import com.viteprotocolo.protocolo.entity.cadSus.CadSusRequest;
import com.viteprotocolo.protocolo.entity.cadSus.CadSusResponse;
import com.viteprotocolo.protocolo.repository.AtendenteRepository;
import com.viteprotocolo.protocolo.service.client.CadSusClient;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

// AtendenteServiceTest.java

@ExtendWith(MockitoExtension.class)
class AtendenteServiceTest {

    @Mock
    private AtendenteRepository atendenteRepository;
    @Mock
    private CadSusClient cadSusClient;

    @InjectMocks
    private AtendenteService service;

    @BeforeEach
    void setUp() throws Exception {
        // injeta expirationTime via reflexão (campo @Value não é injetado pelo Mockito)
        Field field = AtendenteService.class.getDeclaredField("expirationTime");
        field.setAccessible(true);
        field.set(service, 3600L);
    }

    // -------------------------------------------------------------------------
    // createAttAccount
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Deve retornar null quando o CPF for nulo")
    void createAttAccount_nullCpf_returnsNull() {
        assertNull(service.createAttAccount(null));
        verifyNoInteractions(atendenteRepository, cadSusClient);
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException quando o CPF tiver menos de 11 dígitos")
    void createAttAccount_cpfTooShort_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> service.createAttAccount("123.456"));
        verifyNoInteractions(atendenteRepository, cadSusClient);
    }

    @Test
    @DisplayName("Deve retornar a conta existente sem criar nova quando o CPF já estiver cadastrado")
    void createAttAccount_cpfAlreadyExists_returnsExisting() {
        String cpf = "123.456.789-00";
        AtendenteEntity existing = new AtendenteEntity("João Silva", "12345678900");

        when(atendenteRepository.findByCpf("12345678900")).thenReturn(Optional.of(existing));

        AtendenteEntity result = service.createAttAccount(cpf);

        assertEquals(existing, result);
        verify(atendenteRepository).findByCpf("12345678900");
        verifyNoInteractions(cadSusClient);
        verify(atendenteRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve criar nova conta quando o CPF não estiver cadastrado")
    void createAttAccount_newCpf_createsAndReturnsAccount() {
        String cpf = "987.654.321-00";
        String cpfLimpo = "98765432100";

        CadSusResponse cadSusResp = new CadSusResponse("Maria Souza", cpfLimpo);
        AtendenteEntity saved = new AtendenteEntity("Maria Souza", cpfLimpo);

        when(atendenteRepository.findByCpf(cpfLimpo)).thenReturn(Optional.empty());
        when(cadSusClient.getCadSus(new CadSusRequest("cpf", cpfLimpo))).thenReturn(cadSusResp);
        when(atendenteRepository.save(any(AtendenteEntity.class))).thenReturn(saved);

        AtendenteEntity result = service.createAttAccount(cpf);

        assertNotNull(result);
        assertEquals("Maria Souza", result.getNome());
        assertEquals(cpfLimpo, result.getCpf());
        verify(cadSusClient).getCadSus(any());
        verify(atendenteRepository).save(any(AtendenteEntity.class));
    }

    @Test
    @DisplayName("Deve remover todos os não-dígitos do CPF antes de processar")
    void createAttAccount_cpfWithMask_stripsNonDigits() {
        String cpf = "111.222.333-44";
        String cpfLimpo = "11122233344";

        when(atendenteRepository.findByCpf(cpfLimpo)).thenReturn(Optional.empty());
        when(cadSusClient.getCadSus(any())).thenReturn(new CadSusResponse("Nome Teste", cpfLimpo));
        when(atendenteRepository.save(any())).thenReturn(new AtendenteEntity("Nome Teste", cpfLimpo));

        service.createAttAccount(cpf);

        verify(atendenteRepository).findByCpf(cpfLimpo);
        verify(cadSusClient).getCadSus(new CadSusRequest("cpf", cpfLimpo));
    }

    @Test
    @DisplayName("Deve propagar exceção quando o CadSusClient falhar")
    void createAttAccount_cadSusThrows_propagatesException() {
        String cpf = "11122233344";

        when(atendenteRepository.findByCpf(cpf)).thenReturn(Optional.empty());
        when(cadSusClient.getCadSus(any())).thenThrow(new RuntimeException("CadSus indisponível"));

        assertThrows(RuntimeException.class, () -> service.createAttAccount(cpf));
        verify(atendenteRepository, never()).save(any());
    }

    // -------------------------------------------------------------------------
    // setMunicio
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Deve adicionar cookie com nome correto e valor do enum quando o município for válido")
    void setMunicio_validMunicipio_addsCookie() {
        HttpServletResponse response = mock(HttpServletResponse.class);
        ArgumentCaptor<Cookie> cookieCaptor = ArgumentCaptor.forClass(Cookie.class);

        // Ajuste "SAO PAULO" ao toString() / nome real que você passa para setMunicio
        service.setMunicio(response, "SAO PAULO");

        verify(response).addCookie(cookieCaptor.capture());
        Cookie cookie = cookieCaptor.getValue();

        assertEquals("municipio_protocolo", cookie.getName());
        assertEquals(Municipios.SAO.name(), cookie.getValue()); // valor = enum.name()
        assertEquals("/", cookie.getPath());
        assertEquals(3600, cookie.getMaxAge());
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException quando o município não existir no enum")
    void setMunicio_invalidMunicipio_throwsException() {
        HttpServletResponse response = mock(HttpServletResponse.class);

        assertThrows(IllegalArgumentException.class,
                () -> service.setMunicio(response, "MUNICIPIO_INEXISTENTE"));
        verify(response, never()).addCookie(any());
    }

    @Test
    @DisplayName("Deve normalizar município com acentos antes de buscar no enum")
    void setMunicio_municipioWithAccents_normalizesAndAddsCorrectCookie() {
        HttpServletResponse response = mock(HttpServletResponse.class);
        ArgumentCaptor<Cookie> cookieCaptor = ArgumentCaptor.forClass(Cookie.class);

        // Ex: "São Paulo" → normalizado para "SAO" (3 primeiros chars upper)
        service.setMunicio(response, "São Paulo");

        verify(response).addCookie(cookieCaptor.capture());
        assertNotNull(cookieCaptor.getValue().getValue());
    }
}