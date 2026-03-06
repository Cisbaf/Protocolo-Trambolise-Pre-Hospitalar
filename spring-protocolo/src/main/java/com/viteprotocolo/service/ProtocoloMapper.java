package com.viteprotocolo.service;

import com.viteprotocolo.entity.Protocolo;
import com.viteprotocolo.entity.dto.*;
import com.viteprotocolo.entity.dto.HistoriaDTO;
import com.viteprotocolo.entity.dto.protocolo.ProtocoloRequest;
import com.viteprotocolo.entity.dto.protocolo.ProtocoloResponse;
import com.viteprotocolo.entity.emb.*;
import org.springframework.stereotype.Service;

@Service
public class ProtocoloMapper {

    public Protocolo toProtocolo(ProtocoloRequest request) {
        if (request == null) return null;

        return Protocolo.builder()
                .linhaDoTempo(toIdentificacao(request.LinhaDoTempoSection()))
                .neurologica(toNeurologica(request.AvaliacaoNeurologicaSection()))
                .parametros(toParametros(request.ParametrosClinicosSection()))
                .historia(toHistoria(request.HistoriaClinicaSection()))
                .unidade(toUnidade(request.UnidadeReferenciaSection()))
                .desfecho(toDesfecho(request.DesfechoCenaSection()))
                .parecerFinal(toParecerFinal(request.ParecerFinalSection()))
                .build();
    }
    public ProtocoloResponse toResponse(Protocolo protocolo) {
        if (protocolo == null) return null;

        return ProtocoloResponse.builder()
                .id(protocolo.getId())
                .DesfechoCenaSection(toDesfechoDto(protocolo.getDesfecho()))
                .HistoriaClinicaSection(toHistoriaDto(protocolo.getHistoria()))
                .LinhaDoTempoSection(toIdentificacaoDto(protocolo.getLinhaDoTempo()))
                .AvaliacaoNeurologicaSection(toNeurologicaDto(protocolo.getNeurologica()))
                .ParametrosClinicosSection(toParametrosDto(protocolo.getParametros()))
                .UnidadeReferenciaSection(toUnidadeDto(protocolo.getUnidade()))
                .ParecerFinalSection(toParecerFinalDTO(protocolo.getParecerFinal()))
                .build();
    }

    //to class
    private LinhaDoTempo toIdentificacao(LinhaDoTempoDTO identificacao) {
        if (identificacao == null) return null;

        return LinhaDoTempo.builder()
                .numeroOcorrencia(identificacao.numeroOcorrencia())
                .aberturaChamado(identificacao.aberturaChamado())
                .chegadaCena(identificacao.chegadaCena())
                .municipio(identificacao.municipio())
                .ultimoHorarioVistoBem(identificacao.ultimoHorarioVistoBem())
                .janelaEstimada(identificacao.janelaEstimada())
                .build();
    }
    private Neurologica toNeurologica(NeurologicaDTO neurologica) {
        if (neurologica == null) return null;

        return Neurologica.builder()
                .desvioFacial(neurologica.desvioFacial())
                .quedaBraco(neurologica.quedaBraco())
                .falaAnormal(neurologica.falaAnormal())
                .balance(neurologica.balance())
                .eyes(neurologica.eyes())
                .build();
    }
    private Parametros toParametros(ParametrosDTO parametros) {
        if (parametros == null) return null;

        return Parametros.builder()
                .glicemia(parametros.glicemia())
                .pressaoArterial(parametros.pressaoArterial())
                .saturacao(parametros.saturacao())
                .build();
    }
    private com.viteprotocolo.entity.emb.Historia toHistoria(HistoriaDTO historia) {
        if (historia == null) return null;

        return com.viteprotocolo.entity.emb.Historia.builder()
                .idade(historia.idade())
                .doencas(historia.doencas())
                .medicamentos(historia.medicamentos())
                .build();
    }
    private Unidade toUnidade(UnidadeDTO unidade) {
        if (unidade == null) return null;

        return Unidade.builder()
                .unidadeReferenciaEleita(unidade.unidadeReferenciaEleita())
                .horarioNotificacaoUnidade(unidade.horarioNotificacaoUnidade())
                .build();
    }
    private Desfecho toDesfecho(DesfechoDTO desfecho) {
        if (desfecho == null) return null;

        return Desfecho.builder()
                .horarioSaidaCena(desfecho.horarioSaidaCena())
                .horarioChegadaHospital(desfecho.horarioChegadaHospital())
                .build();
    }
    private ParecerFinal toParecerFinal(ParecerFinalDTO parecerFinalDTO) {
        if (parecerFinalDTO == null) return null;

        return ParecerFinal.builder()
                .motivos(parecerFinalDTO.motivos())
                .elegibilidade(parecerFinalDTO.elegibilidade())
                .build();
    }

    //To DTO
    private NeurologicaDTO toNeurologicaDto(Neurologica neurologica) {
        if (neurologica == null) return null;

        return NeurologicaDTO.builder()
                .desvioFacial(neurologica.getDesvioFacial())
                .quedaBraco(neurologica.getQuedaBraco())
                .falaAnormal(neurologica.getFalaAnormal())
                .eyes(neurologica.getEyes())
                .balance(neurologica.getBalance())
                .build();
    }
    private LinhaDoTempoDTO toIdentificacaoDto(LinhaDoTempo identificacao) {
        if (identificacao == null) return null;

        return LinhaDoTempoDTO.builder()
                .numeroOcorrencia(identificacao.getNumeroOcorrencia())
                .aberturaChamado(identificacao.getAberturaChamado())
                .chegadaCena(identificacao.getChegadaCena())
                .municipio(identificacao.getMunicipio())
                .ultimoHorarioVistoBem(identificacao.getUltimoHorarioVistoBem())
                .janelaEstimada(identificacao.getJanelaEstimada())
                .build();
    }
    private ParametrosDTO toParametrosDto(Parametros parametros) {
        if (parametros == null) return null;

        return ParametrosDTO.builder()
                .glicemia(parametros.getGlicemia())
                .pressaoArterial(parametros.getPressaoArterial())
                .saturacao(parametros.getSaturacao())
                .build();
    }
    private HistoriaDTO toHistoriaDto(com.viteprotocolo.entity.emb.Historia historia) {
        if (historia == null) return null;

        return HistoriaDTO.builder()
                .idade(historia.getIdade())
                .medicamentos(historia.getMedicamentos())
                .doencas(historia.getDoencas())
                .build();
    }
    private UnidadeDTO toUnidadeDto(Unidade unidade) {
        if (unidade == null) return null;

        return UnidadeDTO.builder()
                .unidadeReferenciaEleita(unidade.getUnidadeReferenciaEleita())
                .horarioNotificacaoUnidade(unidade.getHorarioNotificacaoUnidade())
                .build();
    }
    private DesfechoDTO toDesfechoDto(Desfecho desfecho) {
        if (desfecho == null) return null;

        return DesfechoDTO.builder()
                .horarioSaidaCena(desfecho.getHorarioSaidaCena())
                .horarioChegadaHospital(desfecho.getHorarioChegadaHospital())
                .build();
    }
    private ParecerFinalDTO toParecerFinalDTO(ParecerFinal parecerFinal) {
        if (parecerFinal == null) return null;

        return ParecerFinalDTO.builder()
                .motivos(parecerFinal.getMotivos())
                .elegibilidade(parecerFinal.getElegibilidade())
                .build();
    }
}
