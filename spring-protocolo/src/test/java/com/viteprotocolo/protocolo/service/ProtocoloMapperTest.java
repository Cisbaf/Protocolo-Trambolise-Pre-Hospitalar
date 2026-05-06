package com.viteprotocolo.protocolo.service;

import com.viteprotocolo.protocolo.entity.Protocolo;
import com.viteprotocolo.protocolo.entity.dto.*;
import com.viteprotocolo.protocolo.entity.dto.protocolo.ProtocoloRequest;
import com.viteprotocolo.protocolo.entity.dto.protocolo.ProtocoloResponse;
import com.viteprotocolo.protocolo.entity.emb.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ProtocoloMapperTest {

    private ProtocoloMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ProtocoloMapper();
    }

    // -------------------------------------------------------------------------
    // toProtocolo
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Deve retornar null quando o ProtocoloRequest for nulo")
    void toProtocolo_nullRequest_returnsNull() {
        assertNull(mapper.toProtocolo(null));
    }

    @Test
    @DisplayName("Deve mapear ProtocoloRequest para a entidade Protocolo corretamente incluindo o CPF")
    void toProtocolo_validRequest_mapsCorrectly() {
        LocalDateTime now = LocalDateTime.now();

        LinhaDoTempoDTO ldt = LinhaDoTempoDTO.builder()
                .numeroOcorrencia("OC-123")
                .municipio("SAO_PAULO")
                .aberturaChamado(now)
                .chegadaCena(now)
                .ultimoHorarioVistoBem(now)
                .janelaEstimada("1h")
                .build();

        NeurologicaDTO neu = NeurologicaDTO.builder()
                .desvioFacial("SIM")
                .quedaBraco("NAO")
                .eyes("NORMAL")
                .balance("ALTERADO")
                .falaAnormal("SIM")
                .build();

        ParametrosDTO par = ParametrosDTO.builder()
                .glicemia(90L)
                .pressaoArterial("120/80")
                .saturacao(98)
                .build();

        HistoriaDTO hist = HistoriaDTO.builder()
                .idade(65)
                .uso_coagulante_em_48h(true)
                .doencas(Map.of("Hipertensao", true, "Diabetes", false))
                .medicamentos(List.of("Aspirina", "Warfarina"))
                .build();

        UnidadeDTO uni = UnidadeDTO.builder()
                .unidadeReferenciaEleita("Unidade A")
                .horarioNotificacaoUnidade(now)
                .build();

        DesfechoDTO des = DesfechoDTO.builder()
                .horarioSaidaCena(now)
                .horarioChegadaHospital(now)
                .build();

        ParecerFinalDTO pf = ParecerFinalDTO.builder()
                .elegibilidade("ELEGIVEL")
                .motivos(List.of("Janela terapeutica", "Sem contraindicacoes"))
                .build();

        ProtocoloRequest request = new ProtocoloRequest(des, hist, ldt, neu, par, uni, pf, "12345678900",0L);

        Protocolo result = mapper.toProtocolo(request);

        assertNotNull(result);
        assertEquals("12345678900", result.getCpf_atendente());

        assertEquals("OC-123", result.getLinhaDoTempo().getNumeroOcorrencia());
        assertEquals("SAO_PAULO", result.getLinhaDoTempo().getMunicipio());
        assertEquals(now, result.getLinhaDoTempo().getAberturaChamado());

        assertEquals("SIM", result.getNeurologica().getDesvioFacial());
        assertEquals("NAO", result.getNeurologica().getQuedaBraco());

        assertEquals(90L, result.getParametros().getGlicemia());

        assertEquals(65, result.getHistoria().getIdade());
        assertTrue(result.getHistoria().getUsoCoagulanteEm48h());

        assertEquals("Unidade A", result.getUnidade().getUnidadeReferenciaEleita());
        assertEquals("ELEGIVEL", result.getParecerFinal().getElegibilidade());
    }

    // -------------------------------------------------------------------------
    // toResponse
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Deve retornar null quando a entidade Protocolo for nula")
    void toResponse_nullProtocolo_returnsNull() {
        assertNull(mapper.toResponse(null));
    }

    @Test
    @DisplayName("Deve mapear entidade Protocolo para ProtocoloResponse corretamente incluindo novas flags (preId e finalizado)")
    void toResponse_validProtocolo_mapsCorrectly() {
        LocalDateTime now = LocalDateTime.now();

        Protocolo entity = Protocolo.builder()
                .id("test-uuid")
                .cpf_atendente("09876543211")
                .preId(42L)
                .dataCriacao(now)
                .linhaDoTempo(LinhaDoTempo.builder()
                        .numeroOcorrencia("OC-123")
                        .municipio("SAO_PAULO")
                        .aberturaChamado(now)
                        .build())
                .neurologica(Neurologica.builder()
                        .desvioFacial("SIM")
                        .build())
                .parametros(Parametros.builder()
                        .glicemia(100L)
                        .build())
                .historia(Historia.builder()
                        .idade(70)
                        .build())
                .unidade(Unidade.builder()
                        .unidadeReferenciaEleita("Unidade X")
                        .build())
                .desfecho(Desfecho.builder()
                        .horarioSaidaCena(now)
                        .build())
                .parecerFinal(ParecerFinal.builder()
                        .elegibilidade("ELEGIVEL")
                        .build())
                .build();

        ProtocoloResponse response = mapper.toResponse(entity);

        assertNotNull(response);
        assertEquals("test-uuid", response.id());
        assertEquals("09876543211", response.cpf_atendente());
        assertEquals(42L, response.preId());
        assertEquals(now, response.dataCriacao());

        assertEquals("OC-123", response.LinhaDoTempoSection().numeroOcorrencia());
        assertEquals("SIM", response.AvaliacaoNeurologicaSection().desvioFacial());
        assertEquals(100L, response.ParametrosClinicosSection().glicemia());
        assertEquals(70, response.HistoriaClinicaSection().idade());
        assertEquals("Unidade X", response.UnidadeReferenciaSection().unidadeReferenciaEleita());
        assertEquals("ELEGIVEL", response.ParecerFinalSection().elegibilidade());
    }
}