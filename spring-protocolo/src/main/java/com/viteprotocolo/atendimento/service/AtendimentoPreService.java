package com.viteprotocolo.atendimento.service;

import com.viteprotocolo.atendimento.entity.AtendimentoPre;
import com.viteprotocolo.atendimento.entity.dto.AtendimentoPre.PreResponse;
import com.viteprotocolo.atendimento.entity.preDto.AtendimentoPreRequest;
import com.viteprotocolo.atendimento.repository.AtendimentoPreRepository;
import com.viteprotocolo.auth.entity.UserEntity;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AtendimentoPreService {
    private final AtendimentoPreRepository protocoloPreRepository;


    public Page<PreResponse> getAllAtendimentoPreByMunicipio(Pageable pageable, String municipio) {

        if (municipio == null || municipio.isBlank()) {
            return Page.empty(pageable);
        }

        var normalizado = Normalizer.normalize(municipio, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "").toLowerCase();

        return protocoloPreRepository.findPendentesByMunicipio(normalizado, pageable).map(AtendimentoMapper::toPreResponse);
    }

    public Page<PreResponse> getAllAtendimentoPrePendentes(Pageable pageable) {
        return protocoloPreRepository.findAllPendentes(pageable).map(AtendimentoMapper::toPreResponse);
    }

    public Page<PreResponse> getAllAtendimentoPre(Pageable pageable) {
        try {

            return protocoloPreRepository.findAll(pageable).map(AtendimentoMapper::toPreResponse);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
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

    @Transactional
    public PreResponse updatePrePreenchimento(AtendimentoPreRequest protocoloPre, UserEntity admin, Long id) {
        if (protocoloPre == null) return null;
        var entity = protocoloPreRepository.findById(id).orElse(null);

        if (entity == null) return null;

        try {
            var newEntity = entity.builder()
                    .numeroOcorrencia(protocoloPre.numeroOcorrencia() != null ? protocoloPre.numeroOcorrencia() : entity.getNumeroOcorrencia())
                    .municipio(protocoloPre.municipio() != null ? protocoloPre.municipio().toUpperCase() : entity.getMunicipio())
                    .aberturaChamado(protocoloPre.aberturaChamado() != null ? protocoloPre.aberturaChamado() : entity.getAberturaChamado())
                    .admin(admin != null ? admin : entity.getAdmin())
                    .build();
            protocoloPreRepository.save(newEntity);
            return AtendimentoMapper.toPreResponse(entity);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
