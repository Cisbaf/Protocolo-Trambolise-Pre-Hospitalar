package com.viteprotocolo.atendimento.controller;

import com.viteprotocolo.atendimento.entity.dto.atendimento.AtendimentoRequest;
import com.viteprotocolo.atendimento.entity.dto.atendimento.AtendimentoResponse;
import com.viteprotocolo.atendimento.service.AtendimentoService;
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

@RestController
@RequestMapping("/atendimento")
@RequiredArgsConstructor
public class AtendimentoController {
    private final AtendimentoService atendimentoService;

    @PostMapping
    public ResponseEntity<AtendimentoResponse> createAtendimento(@RequestBody @Valid AtendimentoRequest atendimentoRequest) {
        if (atendimentoRequest == null) {
            return ResponseEntity.badRequest().build();
        }
        try {
            return ResponseEntity.ok(atendimentoService.createAtendimento(atendimentoRequest));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping
    public ResponseEntity<Page<AtendimentoResponse>> getAtendimento(@RequestParam(defaultValue = "0") int page,
                                                                    @RequestParam(defaultValue = "10") int size,
                                                                    @RequestParam(defaultValue = "id") String sort) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort));
        try {
            return ResponseEntity.ok(atendimentoService.getAllAtendimentos(pageable));
        } catch (Exception e) {
            return ResponseEntity.noContent().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<AtendimentoResponse> getAtendimentoById(@PathVariable String id) {
        try {
            return ResponseEntity.ok(atendimentoService.getAtendimentoById(id));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/params")
    public ResponseEntity<Page<AtendimentoResponse>> getAtendimentoByIdWithParams(@RequestParam(required = false) String id,
                                                                                  @RequestParam(required = false) String nomeUnidade,
                                                                                  @RequestParam(required = false) String numeroOcorrencia,
                                                                                  @RequestParam(required = false) LocalDate aberturaChamado,
                                                                                  @RequestParam(required = false) String municipio,
                                                                                  @RequestParam(defaultValue = "0") int page,
                                                                                  @RequestParam(defaultValue = "10") int size,
                                                                                  @RequestParam(defaultValue = "id") String sort
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort));

        return ResponseEntity.ok(atendimentoService.getAtendimentoByIdWithParams(id, nomeUnidade, numeroOcorrencia, aberturaChamado, municipio, pageable));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAtendimentoById(@PathVariable String id) {
        atendimentoService.deleteAtendimentoById(id);
        return ResponseEntity.noContent().build();
    }
}