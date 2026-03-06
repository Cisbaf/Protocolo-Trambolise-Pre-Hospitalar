package com.viteprotocolo.service;

import com.viteprotocolo.entity.Protocolo;
import com.viteprotocolo.entity.dto.protocolo.ProtocoloRequest;
import com.viteprotocolo.entity.dto.protocolo.ProtocoloResponse;
import com.viteprotocolo.repository.ProtocoloRespository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    public List<ProtocoloResponse> getAllProtocolos() {
        return protocoloRespository.findAll().stream().map(protocoloMapper::toResponse).toList();
    }

    public ProtocoloResponse getProtocoloById(String id) {
        var protocolo = protocoloRespository.findById(id).orElseThrow();
        return protocoloMapper.toResponse(protocolo);
    }

    public List<Protocolo> getProtocoloByIdWithParams(String id, String nomeUnidade, String numeroOcorrencia) {
        if (id != null) {
            return protocoloRespository.findById(id).map(List::of).orElse(List.of());
        }
        List<Protocolo> resultados = new ArrayList<>();
        if (nomeUnidade != null) {
            resultados.addAll(protocoloRespository.findByUnidade_UnidadeReferenciaEleitaContaining(nomeUnidade));
        }
        if (numeroOcorrencia != null) {
            resultados.addAll(protocoloRespository.findByLinhaDoTempo_NumeroOcorrencia(numeroOcorrencia));
        }
        // Remove duplicatas se necessário
        return resultados.stream().distinct().collect(Collectors.toList());
    }

    public void deleteProtocoloById(String id) {
        protocoloRespository.deleteById(id);
    }

}
