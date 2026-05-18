package com.viteprotocolo.atendimento.service;

import com.viteprotocolo.atendimento.entity.Atendimento;
import com.viteprotocolo.atendimento.entity.AtendimentoPre;
import com.viteprotocolo.atendimento.entity.dto.AtendimentoPre.PreResponse;
import com.viteprotocolo.atendimento.entity.dto.atendimento.AtendimentoRequest;
import com.viteprotocolo.atendimento.entity.dto.atendimento.AtendimentoResponse;
import com.viteprotocolo.atendimento.entity.preDto.AtendimentoPreRequest;
import com.viteprotocolo.atendimento.repository.AtendimentoPreRepository;
import com.viteprotocolo.atendimento.repository.AtendimentoRepository;
import com.viteprotocolo.auth.entity.UserEntity;
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

import java.text.Normalizer;
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
            var resp = protocoloMapper.toAtendimento(protocolo);
            resp.setDataCriacao(LocalDateTime.now());

            var atendente = atendenteService.createAttAccount(resp.getCpf_atendente());
            resp.setAtendente(atendente);

            var salvo = protocoloRepository.save(resp);

            return protocoloMapper.toResponse(salvo);
        } catch (Exception e) {
            log.error("Erro ao criar atendimento: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public Page<AtendimentoResponse> getAllAtendimentos(Pageable pageable) {
        return protocoloRepository.findAll(pageable).map(protocoloMapper::toResponse);
    }

    public Page<PreResponse> getAllAtendimentoPreByMunicipio(Pageable pageable, String municipio) {

        if (municipio == null || municipio.isBlank()) {
            return Page.empty(pageable);
        }

        var normalizado = Normalizer.normalize(municipio, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "").toLowerCase();

        return protocoloPreRepository.findPendentesByMunicipio(normalizado, pageable).map(AtendimentoMapper::toPreResponse);
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
    public PreResponse criarPrePreenchimento(AtendimentoPreRequest protocoloPre, UserEntity admin) {
        if (protocoloPre == null) return null;

        var aberturaChamado = protocoloPre.aberturaChamado() != null ? protocoloPre.aberturaChamado() : null;
        var municipio = protocoloPre.municipio() != null ? protocoloPre.municipio().toUpperCase() : "";
        var numeroOcorrencia = protocoloPre.numeroOcorrencia() != null ? protocoloPre.numeroOcorrencia() : "";

        var entity = protocoloPreRepository.save(AtendimentoPre.builder()
                .numeroOcorrencia(numeroOcorrencia)
                .municipio(municipio)
                .aberturaChamado(aberturaChamado)
                .criadoPreAtt(LocalDateTime.now())
                .admin(admin)
                .build());
        return AtendimentoMapper.toPreResponse(entity);
    }

    public void deleteAtendimentoById(String id) {
        if(id == null) return;
        var entidade = protocoloRepository.findById(id).orElseThrow();
        protocoloRepository.deleteById(entidade.getId());
    }

    static String getCookieValue(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("municipio_atendente".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}