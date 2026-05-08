package com.viteprotocolo.atendimento.controller;

import com.viteprotocolo.auth.service.AdminService;
import com.viteprotocolo.atendimento.entity.AtendimentoPre;
import com.viteprotocolo.atendimento.entity.dto.atendimento.AtendimentoRequest;
import com.viteprotocolo.atendimento.entity.dto.atendimento.AtendimentoResponse;
import com.viteprotocolo.atendimento.entity.preDto.AtendimentoPreRequest;
import com.viteprotocolo.atendimento.service.AtendenteService;
import com.viteprotocolo.atendimento.service.AtendimentoService;
import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
import java.time.LocalDate;

@RestController
@RequestMapping("/atendimento")
@RequiredArgsConstructor
public class AtendimentoController {
    private final AtendimentoService atendimentoService;
    private final AtendenteService atendenteService;
    private final AdminService adminService;

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
    public ResponseEntity<Page<AtendimentoResponse>> getAtendimentoByIdWithParams(@RequestParam @Nullable String id,
                                                                                @RequestParam @Nullable String nomeUnidade,
                                                                                @RequestParam @Nullable String numeroOcorrencia,
                                                                                @RequestParam @Nullable LocalDate aberturaChamado,
                                                                                @RequestParam @Nullable String municipio,
                                                                                @RequestParam(defaultValue = "0") int page,
                                                                                @RequestParam(defaultValue = "10") int size,
                                                                                @RequestParam(defaultValue = "id") String sort
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort));

        return ResponseEntity.ok(atendimentoService.getAtendimentoByIdWithParams(id, nomeUnidade, numeroOcorrencia, aberturaChamado, municipio, pageable));
    }


    @GetMapping("/home")
    public ResponseEntity<Page<AtendimentoPre>> getAtendimentos(HttpServletRequest request,
                                                                @RequestParam(defaultValue = "0") int page,
                                                                @RequestParam(defaultValue = "10") int size,
                                                                @RequestParam(defaultValue = "id") String sort) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort));

        var protocolos = atendimentoService.getAllAtendimentoPreByMunicipio(request, pageable);
        return ResponseEntity.ok(protocolos);
    }

    @PostMapping("/municipio/{muni}")
    public ResponseEntity<Void> setMunicio(@PathVariable(name = "muni") String muni, HttpServletResponse response) {
        if (muni == null) {
            return ResponseEntity.badRequest().build();
        }
        try {
            atendenteService.setMunicio(response, muni);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/create-acc")
    public ResponseEntity<?> createAttAccount(@RequestBody String cpf) {
        if (cpf == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(atendenteService.createAttAccount(cpf));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAtendimentoById(@PathVariable String id) {
        atendimentoService.deleteAtendimentoById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/pre-preenchimento")
    public ResponseEntity<AtendimentoPre> createPrePreenchimento(
            @RequestBody @Valid AtendimentoPreRequest atendimentoPreRequest,
           Principal principal) {
        if (atendimentoPreRequest == null) {
            return ResponseEntity.badRequest().build();
        }
        try {
            var admin = principal != null ? adminService.findByUsername(principal.getName()) : null;
            AtendimentoPre created = atendimentoService.criarPrePreenchimento(atendimentoPreRequest, admin);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}