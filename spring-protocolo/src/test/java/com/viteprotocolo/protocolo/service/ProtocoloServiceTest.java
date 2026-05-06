package com.viteprotocolo.protocolo.service;

import com.viteprotocolo.auth.entity.Municipios;
import com.viteprotocolo.protocolo.entity.Protocolo;
import com.viteprotocolo.protocolo.entity.ProtocoloPre;
import com.viteprotocolo.protocolo.entity.dto.*;
import com.viteprotocolo.protocolo.entity.dto.protocolo.ProtocoloRequest;
import com.viteprotocolo.protocolo.entity.dto.protocolo.ProtocoloResponse;
import com.viteprotocolo.protocolo.entity.emb.*;
import com.viteprotocolo.protocolo.entity.preDto.ProtocoloPreRequest;
import com.viteprotocolo.protocolo.repository.PortocoloPreRepository;
import com.viteprotocolo.protocolo.repository.ProtocoloRespository;
import jakarta.persistence.EntityNotFoundException;
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
    @Mock
    private PortocoloPreRepository protocoloPreRepository;

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
                "uuid-1", "12345678900", now,
                new DesfechoDTO(now, now),
                new HistoriaDTO(65, true, Map.of("AVC", true), List.of()),
                new LinhaDoTempoDTO("OC-001", "SAO_PAULO", now, now, now, "1h"),
                new NeurologicaDTO("SIM", "NAO", "NORMAL", "NORMAL", "NAO"),
                new ParametrosDTO(100L, "120/80", 98),
                new UnidadeDTO("Unidade X", now),
                new ParecerFinalDTO("ELEGIVEL", List.of("OK")),
                null
        );

        request = new ProtocoloRequest(
                new DesfechoDTO(now, now),
                new HistoriaDTO(65, true, Map.of("AVC", true), List.of()),
                new LinhaDoTempoDTO("OC-001", "SAO_PAULO", now, now, now, "1h"),
                new NeurologicaDTO("SIM", "NAO", "NORMAL", "NORMAL", "NAO"),
                new ParametrosDTO(100L, "120/80", 98),
                new UnidadeDTO("Unidade X", now),
                new ParecerFinalDTO("ELEGIVEL", List.of("OK")),
                "12345678900",
                null
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
    
    // -------------------------------------------------------------------------
    // findProtocoloByOcorrencia
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Deve retornar ProtocoloResponse vazio se a ocorrencia for nula ou em branco")
    void findProtocoloByOcorrencia_nullOrBlank_returnsEmpty() {
        ProtocoloResponse resultNull = service.findProtocoloByOcorrencia(null);
        ProtocoloResponse resultBlank = service.findProtocoloByOcorrencia("   ");

        // Verifica se retornou os objetos vazios gerados pelo builder
        assertNotNull(resultNull);
        assertNotNull(resultBlank);
        verifyNoInteractions(protocoloRespository, protocoloMapper);
    }

    @Test
    @DisplayName("Deve retornar ProtocoloResponse mapeado quando a ocorrencia for encontrada")
    void findProtocoloByOcorrencia_found_returnsResponse() {
        when(protocoloRespository.findByLinhaDoTempo_NumeroOcorrencia("OC-001")).thenReturn(Optional.of(protocolo));
        when(protocoloMapper.toResponse(protocolo)).thenReturn(response);

        ProtocoloResponse result = service.findProtocoloByOcorrencia("OC-001");

        assertNotNull(result);
        assertEquals("uuid-1", result.id());
        verify(protocoloRespository).findByLinhaDoTempo_NumeroOcorrencia("OC-001");
        verify(protocoloMapper).toResponse(protocolo);
    }

    @Test
    @DisplayName("Deve retornar ProtocoloResponse vazio se a ocorrencia não for encontrada")
    void findProtocoloByOcorrencia_notFound_returnsEmptyMappedResponse() {
        when(protocoloRespository.findByLinhaDoTempo_NumeroOcorrencia("OC-999")).thenReturn(Optional.empty());
        when(protocoloMapper.toResponse(any(Protocolo.class))).thenReturn(ProtocoloResponse.builder().build());

        ProtocoloResponse result = service.findProtocoloByOcorrencia("OC-999");

        assertNotNull(result);
        verify(protocoloRespository).findByLinhaDoTempo_NumeroOcorrencia("OC-999");
        verify(protocoloMapper).toResponse(any(Protocolo.class));
    }

    // -------------------------------------------------------------------------
    // criarPrePreenchimento
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Deve retornar nulo se o request de PrePreenchimento for nulo")
    void criarPrePreenchimento_nullRequest_returnsNull() {
        ProtocoloPre result = service.criarPrePreenchimento(null);

        assertNull(result);
        verifyNoInteractions(protocoloPreRepository, protocoloRespository);
    }

    @Test
    @DisplayName("Deve salvar PrePreenchimento e Protocolo e retornar o PrePreenchimento")
    void criarPrePreenchimento_validRequest_savesAndReturns() {
        LocalDateTime now = LocalDateTime.now();
        // Crie aqui a sua classe record simulando o request (Ajuste o construtor conforme o seu Record)
        ProtocoloPreRequest req = new ProtocoloPreRequest("OC-002", "RIO_DE_JANEIRO", now);

        ProtocoloPre savedPre = ProtocoloPre.builder()
                .id(1L)
                .numeroOcorrencia("OC-002")
                .municipio("RIO_DE_JANEIRO")
                .aberturaChamado(now)
                .build();

        when(protocoloPreRepository.save(any(ProtocoloPre.class))).thenReturn(savedPre);
        when(protocoloRespository.save(any(Protocolo.class))).thenReturn(protocolo);

        ProtocoloPre result = service.criarPrePreenchimento(req);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("OC-002", result.getNumeroOcorrencia());

        verify(protocoloPreRepository).save(any(ProtocoloPre.class));
        verify(protocoloRespository).save(any(Protocolo.class));
    }

    // -------------------------------------------------------------------------
    // editProtocolo
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Deve lançar EntityNotFoundException se tentar editar um protocolo que não existe")
    void editProtocolo_notFound_throwsException() {
        Protocolo editRequest = Protocolo.builder().id("inexistente").build();

        when(protocoloRespository.findById("inexistente")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.editProtocolo(editRequest));
        verify(protocoloRespository, never()).save(any());
    }

    @Test
    @DisplayName("Deve atualizar dados e setar finalizado como true quando editar protocolo existente")
    void editProtocolo_existingProtocolo_updatesAndSetsFinalizadoTrue() {
        // Objeto que está no banco (antes do atendente editar)
        Protocolo protocoloExistenteNoBanco = Protocolo.builder()
                .id("uuid-2")
                .finalizado(false)
                .cpf_atendente(null)
                .build();

        // Objeto que veio do front-end com os dados editados
        Protocolo editRequest = Protocolo.builder()
                .id("uuid-2")
                .cpf_atendente("12345678900")
                .unidade(Unidade.builder().unidadeReferenciaEleita("Hospital Central").build())
                .build();

        when(protocoloRespository.findById("uuid-2")).thenReturn(Optional.of(protocoloExistenteNoBanco));
        when(protocoloRespository.save(any(Protocolo.class))).thenAnswer(i -> i.getArgument(0));

        Protocolo result = service.editProtocolo(editRequest);

        // Verifica se atualizou os dados
        assertNotNull(result);
        assertEquals("12345678900", result.getCpf_atendente());
        assertEquals("Hospital Central", result.getUnidade().getUnidadeReferenciaEleita());

        // Verifica se a flag foi alterada corretamente
        assertTrue(result.isFinalizado());

        // Garante que o save foi chamado
        verify(protocoloRespository).findById("uuid-2");
        verify(protocoloRespository).save(protocoloExistenteNoBanco);
    }
}