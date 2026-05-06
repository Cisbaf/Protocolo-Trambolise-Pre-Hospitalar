package com.viteprotocolo.protocolo.service;

import com.viteprotocolo.auth.entity.Municipios;
import com.viteprotocolo.protocolo.entity.Protocolo;
import com.viteprotocolo.protocolo.entity.dto.*;
import com.viteprotocolo.protocolo.entity.dto.protocolo.ProtocoloRequest;
import com.viteprotocolo.protocolo.entity.dto.protocolo.ProtocoloResponse;
import com.viteprotocolo.protocolo.entity.emb.*;
import com.viteprotocolo.protocolo.repository.ProtocoloRespository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
// ProtocoloServiceTest.java

@ExtendWith(MockitoExtension.class)
class ProtocoloServiceTest {

    @Mock
    private ProtocoloRespository protocoloRespository;
    @Mock
    private ProtocoloMapper protocoloMapper;

    @InjectMocks
    private ProtocoloService service;

    private Protocolo protocolo;
    private ProtocoloResponse response;
    private ProtocoloRequest request;

    @BeforeEach
    void setUp() {
        LocalDateTime now = LocalDateTime.now();

        protocolo = Protocolo.builder()
                .id("uuid-1")
                .dataCriacao(now)
                .linhaDoTempo(LinhaDoTempo.builder()
                        .numeroOcorrencia("OC-001")
                        .municipio("SAO_PAULO")
                        .aberturaChamado(now)
                        .chegadaCena(now)
                        .build())
                .neurologica(Neurologica.builder().desvioFacial("SIM").build())
                .parametros(Parametros.builder().glicemia(100L).pressaoArterial("120/80").build())
                .historia(Historia.builder().idade(65).doencas(Map.of("AVC", true)).build())
                .unidade(Unidade.builder().unidadeReferenciaEleita("Unidade X").build())
                .desfecho(Desfecho.builder().horarioSaidaCena(now).horarioChegadaHospital(now).build())
                .parecerFinal(ParecerFinal.builder().elegibilidade("ELEGIVEL").motivos(List.of("OK")).build())
                .build();

        response = new ProtocoloResponse(
                "uuid-1", now,
                new DesfechoDTO(now, now),
                new HistoriaDTO(65, true, Map.of("AVC", true), List.of()),
                new LinhaDoTempoDTO("OC-001", "SAO_PAULO", now, now, now, "1h"),
                new NeurologicaDTO("SIM", "NAO", "NORMAL", "NORMAL", "NAO"),
                new ParametrosDTO(100L, "120/80", 98),
                new UnidadeDTO("Unidade X", now),
                new ParecerFinalDTO("ELEGIVEL", List.of("OK"))
        );

        request = new ProtocoloRequest(
                new DesfechoDTO(now, now),
                new HistoriaDTO(65, true, Map.of("AVC", true), List.of()),
                new LinhaDoTempoDTO("OC-001", "SAO_PAULO", now, now, now, "1h"),
                new NeurologicaDTO("SIM", "NAO", "NORMAL", "NORMAL", "NAO"),
                new ParametrosDTO(100L, "120/80", 98),
                new UnidadeDTO("Unidade X", now),
                new ParecerFinalDTO("ELEGIVEL", List.of("OK"))
        );
    }

    // -------------------------------------------------------------------------
    // createProtocolo
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Deve lançar IllegalArgumentException quando o request for nulo")
    void createProtocolo_nullRequest_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> service.createProtocolo(null));
        verifyNoInteractions(protocoloRespository, protocoloMapper);
    }

    @Test
    @DisplayName("Deve criar e retornar ProtocoloResponse corretamente")
    void createProtocolo_validRequest_returnsResponse() {
        when(protocoloMapper.toProtocolo(request)).thenReturn(protocolo);
        when(protocoloRespository.save(protocolo)).thenReturn(protocolo);
        when(protocoloMapper.toResponse(protocolo)).thenReturn(response);

        ProtocoloResponse result = service.createProtocolo(request);

        assertNotNull(result);
        assertEquals("uuid-1", result.id());
        assertNotNull(protocolo.getDataCriacao());
        verify(protocoloMapper).toProtocolo(request);
        verify(protocoloRespository).save(protocolo);
        verify(protocoloMapper).toResponse(protocolo);
    }

    @Test
    @DisplayName("Deve propagar exceção do repositório como IllegalArgumentException")
    void createProtocolo_repositoryThrows_wrapsException() {
        when(protocoloMapper.toProtocolo(request)).thenReturn(protocolo);
        when(protocoloRespository.save(protocolo)).thenThrow(new RuntimeException("DB error"));

        assertThrows(IllegalArgumentException.class, () -> service.createProtocolo(request));
    }

    // -------------------------------------------------------------------------
    // getAllProtocolos
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Deve retornar página de ProtocoloResponse")
    void getAllProtocolos_returnsMappedPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Protocolo> page = new PageImpl<>(List.of(protocolo));

        when(protocoloRespository.findAll(pageable)).thenReturn(page);
        when(protocoloMapper.toResponse(protocolo)).thenReturn(response);

        Page<ProtocoloResponse> result = service.getAllProtocolos(pageable);

        assertFalse(result.isEmpty());
        assertEquals(1, result.getTotalElements());
        assertEquals("uuid-1", result.getContent().get(0).id());
    }

    @Test
    @DisplayName("Deve retornar página vazia quando não há protocolos")
    void getAllProtocolos_emptyRepository_returnsEmptyPage() {
        Pageable pageable = PageRequest.of(0, 10);
        when(protocoloRespository.findAll(pageable)).thenReturn(Page.empty());

        Page<ProtocoloResponse> result = service.getAllProtocolos(pageable);

        assertTrue(result.isEmpty());
    }

    // -------------------------------------------------------------------------
    // getAllProtocolosByMunicipio
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Deve retornar Page.empty quando o cookie for nulo")
    void getAllProtocolosByMunicipio_nullCookie_returnsEmptyPage() {
        HttpServletRequest httpRequest = mock(HttpServletRequest.class);
        when(httpRequest.getCookies()).thenReturn(null);
        Pageable pageable = PageRequest.of(0, 10);

        Page<ProtocoloResponse> result = service.getAllProtocolosByMunicipio(httpRequest, pageable);

        assertTrue(result.isEmpty());
        verifyNoInteractions(protocoloRespository);
    }

    @Test
    @DisplayName("Deve retornar Page.empty quando o cookie estiver em branco")
    void getAllProtocolosByMunicipio_blankCookieValue_returnsEmptyPage() {
        Cookie blankCookie = new Cookie("municipio_protocolo", "   ");
        HttpServletRequest httpRequest = mock(HttpServletRequest.class);
        when(httpRequest.getCookies()).thenReturn(new Cookie[]{blankCookie});
        Pageable pageable = PageRequest.of(0, 10);

        Page<ProtocoloResponse> result = service.getAllProtocolosByMunicipio(httpRequest, pageable);

        assertTrue(result.isEmpty());
        verifyNoInteractions(protocoloRespository);
    }

    @Test
    @DisplayName("Deve retornar Page.empty quando o cookie não for o 'municipio_protocolo'")
    void getAllProtocolosByMunicipio_wrongCookieName_returnsEmptyPage() {
        Cookie wrongCookie = new Cookie("outro_cookie", "SAO_PAULO");
        HttpServletRequest httpRequest = mock(HttpServletRequest.class);
        when(httpRequest.getCookies()).thenReturn(new Cookie[]{wrongCookie});
        Pageable pageable = PageRequest.of(0, 10);

        Page<ProtocoloResponse> result = service.getAllProtocolosByMunicipio(httpRequest, pageable);

        assertTrue(result.isEmpty());
        verifyNoInteractions(protocoloRespository);
    }

    @Test
    @DisplayName("Deve filtrar por município quando o cookie for válido")
    void getAllProtocolosByMunicipio_validCookie_returnsMappedPage() {
        String municipioEnum = Municipios.SAO.name(); // ajuste ao seu enum
        String municipioDisplay = Municipios.SAO.getNomeExibicao();

        Cookie validCookie = new Cookie("municipio_protocolo", municipioEnum);
        HttpServletRequest httpRequest = mock(HttpServletRequest.class);
        when(httpRequest.getCookies()).thenReturn(new Cookie[]{validCookie});

        Pageable pageable = PageRequest.of(0, 10);
        Page<Protocolo> page = new PageImpl<>(List.of(protocolo));

        when(protocoloRespository.findByLinhaDoTempo_Municipio(municipioDisplay, pageable)).thenReturn(page);
        when(protocoloMapper.toResponse(protocolo)).thenReturn(response);

        Page<ProtocoloResponse> result = service.getAllProtocolosByMunicipio(httpRequest, pageable);

        assertFalse(result.isEmpty());
        verify(protocoloRespository).findByLinhaDoTempo_Municipio(municipioDisplay, pageable);
    }

    // -------------------------------------------------------------------------
    // getProtocoloById
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Deve retornar ProtocoloResponse quando o ID existir")
    void getProtocoloById_existingId_returnsResponse() {
        when(protocoloRespository.findById("uuid-1")).thenReturn(Optional.of(protocolo));
        when(protocoloMapper.toResponse(protocolo)).thenReturn(response);

        ProtocoloResponse result = service.getProtocoloById("uuid-1");

        assertNotNull(result);
        assertEquals("uuid-1", result.id());
    }

    @Test
    @DisplayName("Deve lançar NoSuchElementException quando o ID não existir")
    void getProtocoloById_nonExistingId_throwsException() {
        when(protocoloRespository.findById("id-inexistente")).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> service.getProtocoloById("id-inexistente"));
    }

    // -------------------------------------------------------------------------
    // getProtocoloByIdWithParams
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Deve retornar todos os protocolos quando nenhum filtro for passado")
    void getProtocoloByIdWithParams_noFilters_returnsAll() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Protocolo> page = new PageImpl<>(List.of(protocolo));

        when(protocoloRespository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);
        when(protocoloMapper.toResponse(protocolo)).thenReturn(response);

        Page<ProtocoloResponse> result = service.getProtocoloByIdWithParams(
                null, null, null, null, null, pageable);

        assertEquals(1, result.getTotalElements());
    }

    @Test
    @DisplayName("Deve aplicar filtros quando os parâmetros forem informados")
    void getProtocoloByIdWithParams_withFilters_appliesSpec() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Protocolo> page = new PageImpl<>(List.of(protocolo));

        when(protocoloRespository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);
        when(protocoloMapper.toResponse(protocolo)).thenReturn(response);

        Page<ProtocoloResponse> result = service.getProtocoloByIdWithParams(
                "uuid-1", "Unidade X", "OC-001", LocalDate.now(), "SAO_PAULO", pageable);

        assertEquals(1, result.getTotalElements());
        verify(protocoloRespository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    @DisplayName("Deve retornar página vazia quando nenhum protocolo corresponder aos filtros")
    void getProtocoloByIdWithParams_noMatch_returnsEmptyPage() {
        Pageable pageable = PageRequest.of(0, 10);
        when(protocoloRespository.findAll(any(Specification.class), eq(pageable))).thenReturn(Page.empty());

        Page<ProtocoloResponse> result = service.getProtocoloByIdWithParams(
                "inexistente", null, null, null, null, pageable);

        assertTrue(result.isEmpty());
    }

    // -------------------------------------------------------------------------
    // deleteProtocoloById
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Deve chamar deleteById no repositório com o ID correto")
    void deleteProtocoloById_callsRepository() {
        doNothing().when(protocoloRespository).deleteById("uuid-1");

        assertDoesNotThrow(() -> service.deleteProtocoloById("uuid-1"));

        verify(protocoloRespository).deleteById("uuid-1");
    }
}