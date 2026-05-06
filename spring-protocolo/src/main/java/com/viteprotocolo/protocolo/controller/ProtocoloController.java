package com.viteprotocolo.protocolo.controller;

import com.viteprotocolo.protocolo.entity.Protocolo;
import com.viteprotocolo.protocolo.entity.ProtocoloPre;
import com.viteprotocolo.protocolo.entity.dto.protocolo.ProtocoloRequest;
import com.viteprotocolo.protocolo.entity.dto.protocolo.ProtocoloResponse;
import com.viteprotocolo.protocolo.entity.preDto.ProtocoloPreRequest;
import com.viteprotocolo.protocolo.service.AtendenteService;
import com.viteprotocolo.protocolo.service.ProtocoloService;
import jakarta.annotation.Nullable;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/protocolo")
@RequiredArgsConstructor
public class ProtocoloController {
    private final ProtocoloService protocoloService;
    private final AtendenteService atendenteService;

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
    public ResponseEntity<Page<ProtocoloResponse>> getProtocolo(@RequestParam(defaultValue = "0") int page,
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
    public ResponseEntity<Page<ProtocoloResponse>> getProtocoloByIdWithParams(@RequestParam @Nullable String id,
                                                                              @RequestParam @Nullable String nomeUnidade,
                                                                              @RequestParam @Nullable String numeroOcorrencia,
                                                                              @RequestParam @Nullable LocalDate aberturaChamado,
                                                                              @RequestParam @Nullable String municipio,
                                                                              @RequestParam(defaultValue = "0") int page,
                                                                              @RequestParam(defaultValue = "10") int size,
                                                                              @RequestParam(defaultValue = "id") String sort
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort));

        return ResponseEntity.ok(protocoloService.getProtocoloByIdWithParams(id, nomeUnidade, numeroOcorrencia, aberturaChamado, municipio, pageable));
    }
    @GetMapping("/ocorrencia/{numeroOcorrencia}")
    public ResponseEntity<ProtocoloResponse> getProtocoloByOcorrencia(@PathVariable String numeroOcorrencia) {
        try {
            ProtocoloResponse response = protocoloService.findProtocoloByOcorrencia(numeroOcorrencia);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/home")
    public ResponseEntity<Page<ProtocoloResponse>> getProtocolos(HttpServletRequest request,
                                                                 @PageableDefault(size = 4, sort = "id") @Nullable Pageable pageable) {

        Page<ProtocoloResponse> protocolos = protocoloService.getAllProtocolosByMunicipio(request, pageable);
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
    public ResponseEntity<Void> deleteProtocoloById(@PathVariable String id) {
        protocoloService.deleteProtocoloById(id);
        return ResponseEntity.noContent().build();
    }
    @PostMapping("/pre-preenchimento")
    public ResponseEntity<ProtocoloPre> createPrePreenchimento(@RequestBody @Valid ProtocoloPreRequest protocoloPreRequest) {
        if (protocoloPreRequest == null) {
            return ResponseEntity.badRequest().build();
        }
        try {
            ProtocoloPre created = protocoloService.criarPrePreenchimento(protocoloPreRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Protocolo> editProtocolo(@PathVariable String id, @RequestBody Protocolo protocoloRequest) {
        if (protocoloRequest == null) {
            return ResponseEntity.badRequest().build();
        }
        try {
            protocoloRequest.setId(id);

            Protocolo updated = protocoloService.editProtocolo(protocoloRequest);
            return ResponseEntity.ok(updated);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}