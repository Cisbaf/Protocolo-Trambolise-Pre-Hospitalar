package com.viteprotocolo.atendimento.service;

import com.viteprotocolo.atendimento.entity.Atendimento;
import com.viteprotocolo.atendimento.entity.dto.*;
import com.viteprotocolo.atendimento.entity.dto.HistoriaDTO;
import com.viteprotocolo.atendimento.entity.dto.atendimento.AtendimentoRequest;
import com.viteprotocolo.atendimento.entity.dto.atendimento.AtendimentoResponse;
import com.viteprotocolo.atendimento.entity.emb.*;
import org.springframework.stereotype.Service;

@Service
public class AtendimentoMapper {

    public Atendimento toAtendimento(AtendimentoRequest request) {
        if (request == null) return null;

        return Atendimento.builder()
                .cpf_atendente(request.cpf_atendente())
                .linhaDoTempo(toIdentificacao(request.LinhaDoTempoSection()))
                .neurologica(toNeurologica(request.AvaliacaoNeurologicaSection()))
                .parametros(toParametros(request.ParametrosClinicosSection()))
                .historia(toHistoria(request.HistoriaClinicaSection()))
                .unidade(toUnidade(request.UnidadeReferenciaSection()))
                .desfecho(toDesfecho(request.DesfechoCenaSection()))
                .parecerFinal(toParecerFinal(request.ParecerFinalSection()))
                .preId(request.preId())
                .build();
    }

    public AtendimentoResponse toResponse(Atendimento atendimento) {
        if (atendimento == null) return null;

        return AtendimentoResponse.builder()
                .id(atendimento.getId())
                .cpf_atendente(atendimento.getCpf_atendente())
                .dataCriacao(atendimento.getDataCriacao())
                .preId(atendimento.getPreId())
                .DesfechoCenaSection(toDesfechoDto(atendimento.getDesfecho()))
                .HistoriaClinicaSection(toHistoriaDto(atendimento.getHistoria()))
                .LinhaDoTempoSection(toIdentificacaoDto(atendimento.getLinhaDoTempo()))
                .AvaliacaoNeurologicaSection(toNeurologicaDto(atendimento.getNeurologica()))
                .ParametrosClinicosSection(toParametrosDto(atendimento.getParametros()))
                .UnidadeReferenciaSection(toUnidadeDto(atendimento.getUnidade()))
                .ParecerFinalSection(toParecerFinalDTO(atendimento.getParecerFinal()))
                .atendente(atendimento.getAtendente())
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
    private com.viteprotocolo.atendimento.entity.emb.Historia toHistoria(HistoriaDTO historia) {
        if (historia == null) return null;

        return com.viteprotocolo.atendimento.entity.emb.Historia.builder()
                .idade(historia.idade())
                .usoCoagulanteEm48h(historia.uso_coagulante_em_48h())
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
    private HistoriaDTO toHistoriaDto(com.viteprotocolo.atendimento.entity.emb.Historia historia) {
        if (historia == null) return null;

        return HistoriaDTO.builder()
                .idade(historia.getIdade())
                .uso_coagulante_em_48h(historia.getUsoCoagulanteEm48h())
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
