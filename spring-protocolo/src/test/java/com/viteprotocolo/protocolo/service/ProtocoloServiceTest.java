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
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProtocoloServiceTest {

    @Mock
    private ProtocoloRespository protocoloRepository;
    @Mock
    private ProtocoloMapper protocoloMapper;
    @Mock
    private PortocoloPreRepository protocoloPreRepository;

    @InjectMocks
    private ProtocoloService service;

    @Captor
    private ArgumentCaptor<Protocolo> protocoloCaptor;

    private Protocolo protocolo;
    private ProtocoloResponse response;
    private ProtocoloRequest request;

    @BeforeEach
    void setUp() {
        LocalDateTime now = LocalDateTime.now();

        protocolo = Protocolo.builder()
                .id("uuid-1")
                .dataCriacao(now)
                .cpf_atendente("12345678900")
                .finalizado(false)
                .preId(10L)
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

        response = ProtocoloResponse.builder()
                .id("uuid-1")
                .cpf_atendente("12345678900")
                .dataCriacao(now)
                .finalizado(false)
                .preId(10L)
                .DesfechoCenaSection(new DesfechoDTO(now, now))
                .HistoriaClinicaSection(new HistoriaDTO(65, true, Map.of("AVC", true), List.of()))
                .LinhaDoTempoSection(new LinhaDoTempoDTO("OC-001", "SAO_PAULO", now, now, now, "1h"))
                .AvaliacaoNeurologicaSection(new NeurologicaDTO("SIM", "NAO", "NORMAL", "NORMAL", "NAO"))
                .ParametrosClinicosSection(new ParametrosDTO(100L, "120/80", 98))
                .UnidadeReferenciaSection(new UnidadeDTO("Unidade X", now))
                .ParecerFinalSection(new ParecerFinalDTO("ELEGIVEL", List.of("OK")))
                .build();

        request = new ProtocoloRequest(
                new DesfechoDTO(now, now),
                new HistoriaDTO(65, true, Map.of("AVC", true), List.of()),
                new LinhaDoTempoDTO("OC-001", "SAO_PAULO", now, now, now, "1h"),
                new NeurologicaDTO("SIM", "NAO", "NORMAL", "NORMAL", "NAO"),
                new ParametrosDTO(100L, "120/80", 98),
                new UnidadeDTO("Unidade X", now),
                new ParecerFinalDTO("ELEGIVEL", List.of("OK")),
                "12345678900"
        );
    }

    // -------------------------------------------------------------------------
    // createProtocolo
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Deve lançar IllegalArgumentException quando o request for nulo")
    void createProtocolo_nullRequest_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> service.createProtocolo(null));
        verifyNoInteractions(protocoloRepository, protocoloMapper);
    }

    @Test
    @DisplayName("Deve criar e retornar ProtocoloResponse corretamente")
    void createProtocolo_validRequest_returnsResponse() {
        when(protocoloMapper.toProtocolo(request)).thenReturn(protocolo);
        when(protocoloRepository.save(any(Protocolo.class))).thenReturn(protocolo);
        when(protocoloMapper.toResponse(protocolo)).thenReturn(response);

        ProtocoloResponse result = service.createProtocolo(request);

        assertNotNull(result);
        assertEquals("uuid-1", result.id());
        assertEquals("12345678900", result.cpf_atendente());

        verify(protocoloMapper).toProtocolo(request);
        verify(protocoloRepository).save(protocoloCaptor.capture());

        Protocolo savedProtocolo = protocoloCaptor.getValue();
        assertNotNull(savedProtocolo.getDataCriacao(), "A data de criação deve ser preenchida antes de salvar");
        verify(protocoloMapper).toResponse(protocolo);
    }

    @Test
    @DisplayName("Deve propagar exceção do repositório como IllegalArgumentException")
    void createProtocolo_repositoryThrows_wrapsException() {
        when(protocoloMapper.toProtocolo(request)).thenReturn(protocolo);
        when(protocoloRepository.save(any())).thenThrow(new RuntimeException("DB error"));

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

        when(protocoloRepository.findAll(pageable)).thenReturn(page);
        when(protocoloMapper.toResponse(protocolo)).thenReturn(response);

        Page<ProtocoloResponse> result = service.getAllProtocolos(pageable);

        assertFalse(result.isEmpty());
        assertEquals(1, result.getTotalElements());
        assertEquals("uuid-1", result.getContent().getFirst().id());
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
        verifyNoInteractions(protocoloRepository);
    }

    @Test
    @DisplayName("Deve filtrar por município quando o cookie for válido")
    void getAllProtocolosByMunicipio_validCookie_returnsMappedPage() {
        // Usa o primeiro municipio do Enum para garantir que existe e não quebra com IllegalArgumentException do valueOf
        Municipios municipio = Municipios.values()[0];
        String municipioEnumStr = municipio.name();
        String municipioDisplay = municipio.getNomeExibicao();

        Cookie validCookie = new Cookie("municipio_protocolo", municipioEnumStr);
        HttpServletRequest httpRequest = mock(HttpServletRequest.class);
        when(httpRequest.getCookies()).thenReturn(new Cookie[]{validCookie});

        Pageable pageable = PageRequest.of(0, 10);
        Page<Protocolo> page = new PageImpl<>(List.of(protocolo));

        when(protocoloRepository.findByLinhaDoTempo_MunicipioAndFinalizadoFalse(municipioDisplay, pageable)).thenReturn(page);
        when(protocoloMapper.toResponse(protocolo)).thenReturn(response);

        Page<ProtocoloResponse> result = service.getAllProtocolosByMunicipio(httpRequest, pageable);

        assertFalse(result.isEmpty());
        verify(protocoloRepository).findByLinhaDoTempo_MunicipioAndFinalizadoFalse(municipioDisplay, pageable);
    }

    // -------------------------------------------------------------------------
    // getProtocoloByIdWithParams
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Deve aplicar Specification dinâmico ao buscar protocolos")
    void getProtocoloByIdWithParams_withFilters_appliesSpec() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Protocolo> page = new PageImpl<>(List.of(protocolo));

        when(protocoloRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);
        when(protocoloMapper.toResponse(protocolo)).thenReturn(response);

        Page<ProtocoloResponse> result = service.getProtocoloByIdWithParams(
                "uuid-1", "Unidade X", "OC-001", LocalDate.now(), "SAO_PAULO", pageable);

        assertEquals(1, result.getTotalElements());
        verify(protocoloRepository).findAll(any(Specification.class), eq(pageable));
    }

    // -------------------------------------------------------------------------
    // criarPrePreenchimento
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Deve salvar PrePreenchimento e Protocolo associado ao preId, retornando o PrePreenchimento")
    void criarPrePreenchimento_validRequest_savesAndReturns() {
        LocalDateTime now = LocalDateTime.now();
        ProtocoloPreRequest req = new ProtocoloPreRequest("OC-002", "RIO_DE_JANEIRO", now);

        ProtocoloPre savedPre = ProtocoloPre.builder()
                .id(99L)
                .numeroOcorrencia("OC-002")
                .municipio("RIO_DE_JANEIRO")
                .aberturaChamado(now)
                .build();

        when(protocoloPreRepository.save(any(ProtocoloPre.class))).thenReturn(savedPre);

        ProtocoloPre result = service.criarPrePreenchimento(req);

        assertNotNull(result);
        assertEquals(99L, result.getId());

        verify(protocoloPreRepository).save(any(ProtocoloPre.class));
        verify(protocoloRepository).save(protocoloCaptor.capture());

        // Verifica se o protocolo criado carrega o preId e as informacoes bases
        Protocolo protocoloCriado = protocoloCaptor.getValue();
        assertNotNull(protocoloCriado);
        assertEquals(99L, protocoloCriado.getPreId());
        assertEquals("OC-002", protocoloCriado.getLinhaDoTempo().getNumeroOcorrencia());
        assertEquals("RIO_DE_JANEIRO", protocoloCriado.getLinhaDoTempo().getMunicipio());
        assertNotNull(protocoloCriado.getDataCriacao());
    }

    // -------------------------------------------------------------------------
    // editProtocolo
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Deve atualizar dados e setar finalizado como true quando editar protocolo existente")
    void editProtocolo_existingProtocolo_updatesAndSetsFinalizadoTrue() {
        Protocolo protocoloExistenteNoBanco = Protocolo.builder()
                .id("uuid-2")
                .finalizado(false)
                .cpf_atendente(null)
                .build();

        Protocolo editRequest = Protocolo.builder()
                .id("uuid-2")
                .cpf_atendente("00011122233")
                .unidade(Unidade.builder().unidadeReferenciaEleita("Hospital Novo").build())
                .parecerFinal(ParecerFinal.builder().elegibilidade("INELEGIVEL").build())
                .build();

        when(protocoloRepository.findById("uuid-2")).thenReturn(Optional.of(protocoloExistenteNoBanco));
        when(protocoloRepository.save(any(Protocolo.class))).thenAnswer(i -> i.getArgument(0));

        Protocolo result = service.editProtocolo(editRequest);

        assertNotNull(result);
        assertEquals("00011122233", result.getCpf_atendente());
        assertEquals("Hospital Novo", result.getUnidade().getUnidadeReferenciaEleita());
        assertEquals("INELEGIVEL", result.getParecerFinal().getElegibilidade());
        assertTrue(result.isFinalizado(), "A flag 'finalizado' deve estar true após a edição");

        verify(protocoloRepository).save(protocoloExistenteNoBanco);
    }
}