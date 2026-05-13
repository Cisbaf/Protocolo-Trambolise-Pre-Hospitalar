package com.viteprotocolo.atendimento.controller;

import com.viteprotocolo.atendimento.entity.dto.AtendimentoPre.PreResponse;
import com.viteprotocolo.atendimento.entity.preDto.AtendimentoPreRequest;
import com.viteprotocolo.atendimento.service.AtendimentoService;
import com.viteprotocolo.auth.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/atendimentos/pre")
@RequiredArgsConstructor
public class AtendimentoPreController {

    private final AtendimentoService atendimentoService;
    private final UserService userService;


    @GetMapping("/home")
    public ResponseEntity<Page<PreResponse>> getAtendimentos(HttpServletRequest request,
                                                             @RequestParam(defaultValue = "0") int page,
                                                             @RequestParam(defaultValue = "10") int size,
                                                             @RequestParam(defaultValue = "id") String sort) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort));

        var protocolos = atendimentoService.getAllAtendimentoPreByMunicipio(request, pageable);
        return ResponseEntity.ok(protocolos);
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
            var created = atendimentoService.criarPrePreenchimento(atendimentoPreRequest, admin);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}