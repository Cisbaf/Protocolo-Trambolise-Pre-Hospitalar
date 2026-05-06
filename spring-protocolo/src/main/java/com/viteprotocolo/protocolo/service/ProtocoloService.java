package com.viteprotocolo.protocolo.service;

import com.viteprotocolo.auth.entity.Municipios;
import com.viteprotocolo.protocolo.entity.Protocolo;
import com.viteprotocolo.protocolo.entity.ProtocoloPre;
import com.viteprotocolo.protocolo.entity.dto.protocolo.ProtocoloRequest;
import com.viteprotocolo.protocolo.entity.dto.protocolo.ProtocoloResponse;
import com.viteprotocolo.protocolo.entity.emb.LinhaDoTempo;
import com.viteprotocolo.protocolo.entity.preDto.ProtocoloPreRequest;
import com.viteprotocolo.protocolo.repository.PortocoloPreRepository;
import com.viteprotocolo.protocolo.repository.ProtocoloRespository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.Predicate;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProtocoloService {
    private final ProtocoloRespository protocoloRepository;
    private final ProtocoloMapper protocoloMapper;
    private final PortocoloPreRepository protocoloPreRepository;

    private static final Logger log = LoggerFactory.getLogger(ProtocoloService.class);

    public ProtocoloResponse createProtocolo(ProtocoloRequest protocolo) {
        try {
            if (protocolo == null) {
                log.warn("protocolo is null");
                throw new IllegalArgumentException();
            }
            var resp = protocoloMapper.toProtocolo(protocolo);
            resp.setDataCriacao(LocalDateTime.now());
            var salvo = protocoloRepository.save(resp);

            log.info("Protocolo criado: ID:{}, N. Ocorrencia: {}, Data Criação: {} \n ",
                    salvo.getId(), salvo.getLinhaDoTempo().getNumeroOcorrencia(), salvo.getDataCriacao());
            return protocoloMapper.toResponse(salvo);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new IllegalArgumentException(e);
        }
    }

    public Page<ProtocoloResponse> getAllProtocolos(Pageable pageable) {
        return protocoloRepository.findAll(pageable).map(protocoloMapper::toResponse);
    }

    public Page<ProtocoloResponse> getAllProtocolosByMunicipio(HttpServletRequest request, Pageable pageable) {
        String municipioCookieValue = getCookieValue(request);

        if (municipioCookieValue == null || municipioCookieValue.isBlank()) {
            return Page.empty(pageable);
        }
        String muniName = Municipios.valueOf(municipioCookieValue).getNomeExibicao();
        Page<Protocolo> protocolos = protocoloRepository.findByLinhaDoTempo_MunicipioAndFinalizadoFalse(muniName, pageable);

        return protocolos.map(protocoloMapper::toResponse);
    }




    public ProtocoloResponse getProtocoloById(String id) {
        var protocolo = protocoloRepository.findById(id).orElseThrow();
        return protocoloMapper.toResponse(protocolo);
    }

    public Page<ProtocoloResponse> getProtocoloByIdWithParams(
            String id, String nomeUnidade, String numeroOcorrencia, LocalDate aberturaChamado, String municipio, Pageable pageable) {

        Specification<Protocolo> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (id != null) {
                predicates.add(cb.equal(root.get("id"), id));
            }
            if (nomeUnidade != null) {
                predicates.add(cb.like(root.get("unidade").get("unidadeReferenciaEleita"), "%" + nomeUnidade + "%"));
            }
            if (numeroOcorrencia != null) {
                predicates.add(cb.equal(root.get("linhaDoTempo").get("numeroOcorrencia"), numeroOcorrencia));
            }
            if (aberturaChamado != null) {
                predicates.add(cb.between(
                        root.get("linhaDoTempo").get("aberturaChamado"),
                        aberturaChamado.atStartOfDay(),
                        aberturaChamado.atTime(23, 59, 59)
                ));
            }
            if (municipio != null) {
                predicates.add(cb.like(root.get("linhaDoTempo").get("municipio"), municipio));
            }

            // Se nenhum filtro foi passado, retorna tudo (1=1)
            if (predicates.isEmpty()) return cb.conjunction();

            return cb.or(predicates.toArray(new Predicate[0]));
        };

        return protocoloRepository.findAll(spec, pageable).map(protocoloMapper::toResponse);
    }

    public ProtocoloResponse findProtocoloByOcorrencia(String numeroOcorrencia) {
        if (numeroOcorrencia == null || numeroOcorrencia.isBlank()) {
            return ProtocoloResponse.builder().build();
        }
        var protocolo = protocoloRepository.findByLinhaDoTempo_NumeroOcorrencia(numeroOcorrencia).orElse(Protocolo.builder().build());
        return protocoloMapper.toResponse(protocolo);
    }

    @Transactional
    public ProtocoloPre criarPrePreenchimento(ProtocoloPreRequest protocoloPre) {
        if (protocoloPre == null) return null;

        var aberturaChamado = protocoloPre.aberturaChamado() != null ? protocoloPre.aberturaChamado() : null;
        var municipio = protocoloPre.municipio() != null ? protocoloPre.municipio() : "";
        var numeroOcorrencia = protocoloPre.numeroOcorrencia() != null ? protocoloPre.numeroOcorrencia() : "";

        var preProto = protocoloPreRepository.save(ProtocoloPre.builder().numeroOcorrencia(numeroOcorrencia).municipio(municipio)
                .aberturaChamado(aberturaChamado).build());

        var linhadoTempo = LinhaDoTempo.builder().numeroOcorrencia(numeroOcorrencia).municipio(municipio)
                .aberturaChamado(aberturaChamado).build();

        protocoloRepository.save(Protocolo.builder().linhaDoTempo(linhadoTempo).dataCriacao(LocalDateTime.now()).preId(preProto.getId()).finalizado(false).build());

        return preProto;
    }

    @Transactional
    public Protocolo editProtocolo(Protocolo request) {
        var protocoloExistente = protocoloRepository.findById(request.getId())
                .orElseThrow(() -> new EntityNotFoundException("Protocolo não encontrado"));

        protocoloExistente.setCpf_atendente(request.getCpf_atendente());
        protocoloExistente.setLinhaDoTempo(request.getLinhaDoTempo());
        protocoloExistente.setDesfecho(request.getDesfecho());
        protocoloExistente.setHistoria(request.getHistoria());
        protocoloExistente.setNeurologica(request.getNeurologica());
        protocoloExistente.setParametros(request.getParametros());
        protocoloExistente.setUnidade(request.getUnidade());
        protocoloExistente.setParecerFinal(request.getParecerFinal());

        protocoloExistente.setFinalizado(true);

        return protocoloRepository.save(protocoloExistente);
    }

    public void deleteProtocoloById(String id) {
        protocoloRepository.deleteById(id);
    }

    private String getCookieValue(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("municipio_protocolo".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}