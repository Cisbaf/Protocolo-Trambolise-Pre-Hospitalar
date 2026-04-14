package com.viteprotocolo.protocolo.controller;

import com.viteprotocolo.protocolo.entity.Protocolo;
import com.viteprotocolo.protocolo.entity.dto.protocolo.ProtocoloRequest;
import com.viteprotocolo.protocolo.entity.dto.protocolo.ProtocoloResponse;
import com.viteprotocolo.protocolo.service.ProtocoloService;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/protocolo")
@RequiredArgsConstructor
public class ProtocoloController {
    private final ProtocoloService protocoloService;

    @PostMapping
    public ResponseEntity<ProtocoloResponse> createProtocolo(@RequestBody @Valid ProtocoloRequest protocoloRequest) {
        if (protocoloRequest == null) {
            return ResponseEntity.badRequest().build();
        }
        try {
            return ResponseEntity.ok(protocoloService.createProtocolo(protocoloRequest));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping
    public ResponseEntity<Page<ProtocoloResponse>> getProtocolo(@RequestParam(defaultValue = "0")  int page,
                                                                @RequestParam(defaultValue = "10") int size,
                                                                @RequestParam(defaultValue = "id") String sort) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort));
        try {
            return ResponseEntity.ok(protocoloService.getAllProtocolos(pageable));
        } catch (Exception e) {
            return ResponseEntity.noContent().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProtocoloResponse> getProtocoloById(@PathVariable String id) {
        try {
            return ResponseEntity.ok(protocoloService.getProtocoloById(id));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/params")
    public ResponseEntity<Page<Protocolo>> getProtocoloByIdWithParams(@RequestParam @Nullable String id,
                                                                      @RequestParam @Nullable String nomeUnidade,
                                                                      @RequestParam @Nullable String numeroOcorrencia,
                                                                      @RequestParam @Nullable LocalDate aberturaChamado,
                                                                      @RequestParam @Nullable String municipio,
                                                                      @RequestParam(defaultValue = "0")  int page,
                                                                      @RequestParam(defaultValue = "10") int size,
                                                                      @RequestParam(defaultValue = "id") String sort
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort));

        return ResponseEntity.ok(protocoloService.getProtocoloByIdWithParams(id, nomeUnidade, numeroOcorrencia, aberturaChamado, municipio, pageable));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProtocoloById(@PathVariable String id) {
        protocoloService.deleteProtocoloById(id);
        return ResponseEntity.noContent().build();
    }
}
