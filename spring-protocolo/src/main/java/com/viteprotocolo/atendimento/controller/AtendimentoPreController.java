package com.viteprotocolo.atendimento.controller;

import com.viteprotocolo.atendimento.entity.dto.AtendimentoPre.PreResponse;
import com.viteprotocolo.atendimento.entity.preDto.AtendimentoPreRequest;
import com.viteprotocolo.atendimento.service.AtendimentoPreService;
import com.viteprotocolo.auth.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;

@RestController
@RequestMapping("/atendimentos/pre")
@RequiredArgsConstructor
public class AtendimentoPreController {

    private final AtendimentoPreService atendimentoPreService;
    private final UserService userService;


    @GetMapping("/home")
    public ResponseEntity<Page<PreResponse>> getAtendimentos(@RequestParam(required = false) String municipio,
                                                             @RequestParam(defaultValue = "0") int page,
                                                             @RequestParam(defaultValue = "10") int size,
                                                             @RequestParam(defaultValue = "id") String sort) {
        Page<PreResponse> protocolos;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort));
        try {
            if (municipio != null && !municipio.isEmpty()) {
                protocolos = atendimentoPreService.getAllAtendimentoPreByMunicipio(pageable, municipio);

            } else {
                protocolos = atendimentoPreService.getAllAtendimentoPrePendentes(pageable);
            }
            if (protocolos == null || protocolos.getTotalElements() == 0) {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
        return ResponseEntity.ok(protocolos);
    }

    @GetMapping("/all")
    public ResponseEntity<Page<PreResponse>> getAllAtendimentos(@RequestParam(defaultValue = "0") int page,
                                                                @RequestParam(defaultValue = "10") int size,
                                                                @RequestParam(defaultValue = "id") String sort) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort));

        return ResponseEntity.ok(atendimentoPreService.getAllAtendimentoPre(pageable));
    }

    @PostMapping("/pre-preenchimento")
    public ResponseEntity<PreResponse> createPrePreenchimento(
            @RequestBody @Valid AtendimentoPreRequest atendimentoPreRequest,
            Principal principal) {
        if (atendimentoPreRequest == null) {
            return ResponseEntity.badRequest().build();
        }
        try {
            var admin = principal != null ? userService.findByUsername(principal.getName()) : null;
            var created = atendimentoPreService.criarPrePreenchimento(atendimentoPreRequest, admin);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<PreResponse> updatePrePreenchimento(@RequestBody AtendimentoPreRequest atendimentoPreRequest, @PathVariable Long id, Principal principal) {
        if (atendimentoPreRequest == null || id == null) {
            return ResponseEntity.badRequest().build();
        }
        var admin = principal != null ? userService.findByUsername(principal.getName()) : null;

        return ResponseEntity.ok(atendimentoPreService.updatePrePreenchimento(atendimentoPreRequest, admin, id));
    }
}