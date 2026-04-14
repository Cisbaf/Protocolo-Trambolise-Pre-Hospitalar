package com.viteprotocolo.protocolo.service;

import com.viteprotocolo.protocolo.entity.Protocolo;
import com.viteprotocolo.protocolo.entity.dto.protocolo.ProtocoloRequest;
import com.viteprotocolo.protocolo.entity.dto.protocolo.ProtocoloResponse;
import com.viteprotocolo.protocolo.repository.ProtocoloRespository;
import jakarta.persistence.criteria.Predicate;
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
    private final ProtocoloRespository protocoloRespository;
    private final ProtocoloMapper protocoloMapper;

    private static final Logger log = LoggerFactory.getLogger(ProtocoloService.class);

    public ProtocoloResponse createProtocolo(ProtocoloRequest protocolo) {
        try {
            if (protocolo == null) {
                log.warn("protocolo is null");
                throw new IllegalArgumentException();
            }
            var resp = protocoloMapper.toProtocolo(protocolo);
            resp.setDataCriacao(LocalDateTime.now());
            var salvo = protocoloRespository.save(resp);

            log.info("Protocolo criado: ID:{}, N. Ocorrencia: {}, Data Criação: {} \n ",
                    salvo.getId(), salvo.getLinhaDoTempo().getNumeroOcorrencia(), salvo.getDataCriacao());
            return protocoloMapper.toResponse(salvo);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new IllegalArgumentException(e);
        }
    }

    public Page<ProtocoloResponse> getAllProtocolos(Pageable pageable) {
        return protocoloRespository.findAll(pageable).map(protocoloMapper::toResponse);
    }

    public ProtocoloResponse getProtocoloById(String id) {
        var protocolo = protocoloRespository.findById(id).orElseThrow();
        return protocoloMapper.toResponse(protocolo);
    }

    public Page<Protocolo> getProtocoloByIdWithParams(
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
                predicates.add(cb.equal(root.get("linhaDoTempo").get("municipio"), municipio));
            }

            // Se nenhum filtro foi passado, retorna tudo (1=1)
            if (predicates.isEmpty()) return cb.conjunction();

            return cb.or(predicates.toArray(new Predicate[0]));
        };

        return protocoloRespository.findAll(spec, pageable);
    }

    public void deleteProtocoloById(String id) {
        protocoloRespository.deleteById(id);
    }

}
