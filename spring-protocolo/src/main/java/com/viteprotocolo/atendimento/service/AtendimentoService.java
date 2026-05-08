package com.viteprotocolo.atendimento.service;

import com.viteprotocolo.auth.entity.AdminEntity;
import com.viteprotocolo.auth.entity.Municipios;
import com.viteprotocolo.atendimento.entity.Atendimento;
import com.viteprotocolo.atendimento.entity.AtendimentoPre;
import com.viteprotocolo.atendimento.entity.dto.atendimento.AtendimentoRequest;
import com.viteprotocolo.atendimento.entity.dto.atendimento.AtendimentoResponse;
import com.viteprotocolo.atendimento.entity.preDto.AtendimentoPreRequest;
import com.viteprotocolo.atendimento.repository.AtendimentoPreRepository;
import com.viteprotocolo.atendimento.repository.AtendimentoRepository;
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
public class AtendimentoService {
    private final AtendimentoRepository protocoloRepository;
    private final AtendimentoMapper protocoloMapper;
    private final AtendimentoPreRepository protocoloPreRepository;
    private final AtendenteService atendenteService;

    private static final Logger log = LoggerFactory.getLogger(AtendimentoService.class);

    @Transactional
    public AtendimentoResponse createAtendimento(AtendimentoRequest protocolo) {
        try {
            if (protocolo == null) {
                log.warn("protocolo is null");
                throw new IllegalArgumentException();
            }
            var resp = protocoloMapper.toAtendimento(protocolo);
            resp.setDataCriacao(LocalDateTime.now());
            var salvo = protocoloRepository.save(resp);

            log.info("Atendimento criado: ID:{}, N. Ocorrencia: {}, Data Criação: {} \n ",
                    salvo.getId(), salvo.getLinhaDoTempo().getNumeroOcorrencia(), salvo.getDataCriacao());

            var atendente = atendenteService.createAttAccount(salvo.getCpf_atendente());
            salvo.setAtendente(atendente);

            return protocoloMapper.toResponse(salvo);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new IllegalArgumentException(e);
        }
    }

    public Page<AtendimentoResponse> getAllAtendimentos(Pageable pageable) {
        return protocoloRepository.findAll(pageable).map(protocoloMapper::toResponse);
    }

//    public Page<AtendimentoResponse> getAllAtendimentosByMunicipio(HttpServletRequest request, Pageable pageable) {
//        String municipioCookieValue = getCookieValue(request);
//
//        if (municipioCookieValue == null || municipioCookieValue.isBlank()) {
//            return Page.empty(pageable);
//        }
//        String muniName = Municipios.valueOf(municipioCookieValue).getNomeExibicao();
//        Page<Atendimento> protocolos = protocoloRepository.findByLinhaDoTempo_MunicipioAndFinalizadoFalse(muniName, pageable);
//
//        return protocolos.map(protocoloMapper::toResponse);
//    }

    public Page<AtendimentoPre> getAllAtendimentoPreByMunicipio(HttpServletRequest request, Pageable pageable) {
        String municipioCookieValue = getCookieValue(request);

        if (municipioCookieValue == null || municipioCookieValue.isBlank()) {
            return Page.empty(pageable);
        }
        String muniName = Municipios.valueOf(municipioCookieValue).getNomeExibicao();

        return protocoloPreRepository.findPendentesByMunicipio(muniName, pageable);
    }

    public AtendimentoResponse getAtendimentoById(String id) {
        var protocolo = protocoloRepository.findById(id).orElseThrow();
        return protocoloMapper.toResponse(protocolo);
    }

    public Page<AtendimentoResponse> getAtendimentoByIdWithParams(
            String id, String nomeUnidade, String numeroOcorrencia, LocalDate aberturaChamado, String municipio, Pageable pageable) {

        Specification<Atendimento> spec = (root, query, cb) -> {
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

    @Transactional
    public AtendimentoPre criarPrePreenchimento(AtendimentoPreRequest protocoloPre, AdminEntity admin) {
        if (protocoloPre == null) return null;

        var aberturaChamado = protocoloPre.aberturaChamado() != null ? protocoloPre.aberturaChamado() : null;
        var municipio = protocoloPre.municipio() != null ? protocoloPre.municipio() : "";
        var numeroOcorrencia = protocoloPre.numeroOcorrencia() != null ? protocoloPre.numeroOcorrencia() : "";

        return protocoloPreRepository.save(AtendimentoPre.builder()
                .numeroOcorrencia(numeroOcorrencia)
                .municipio(municipio)
                .aberturaChamado(aberturaChamado)
                .criadoPreAtt(LocalDateTime.now())
                .admin(admin)
                .build());
    }

    public void deleteAtendimentoById(String id) {
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