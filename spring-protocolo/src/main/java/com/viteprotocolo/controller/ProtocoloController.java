package com.viteprotocolo.controller;

import com.viteprotocolo.entity.Protocolo;
import com.viteprotocolo.service.ProtocoloService;
import com.viteprotocolo.entity.dto.protocolo.ProtocoloRequest;
import com.viteprotocolo.entity.dto.protocolo.ProtocoloResponse;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/protocolo")
@RequiredArgsConstructor
public class ProtocoloController {
    private final ProtocoloService protocoloService;

    @PostMapping
    public ResponseEntity<ProtocoloResponse> createProtocolo(@RequestBody @Valid ProtocoloRequest protocoloRequest) {
        if (protocoloRequest == null){
            return ResponseEntity.badRequest().build();
        }
        try {
            return ResponseEntity.ok(protocoloService.createProtocolo(protocoloRequest));
        }catch (Exception e){
            return ResponseEntity.badRequest().build();
        }
    }
    @GetMapping
    public ResponseEntity<List<ProtocoloResponse>> getProtocolo(){
        try{
        return ResponseEntity.ok(protocoloService.getAllProtocolos());
        }catch (Exception e){
            return ResponseEntity.noContent().build();
        }
    }
    @GetMapping("/{id}")
    public ResponseEntity<ProtocoloResponse> getProtocoloById(@PathVariable String id){
        try{
        return ResponseEntity.ok(protocoloService.getProtocoloById(id));}
        catch (Exception e){
            return ResponseEntity.notFound().build();
        }
    }
    @GetMapping("/params")
    public ResponseEntity<List<Protocolo>> getProtocoloByIdWithParams(@RequestParam @Nullable String id, @RequestParam @Nullable String nomeUnidade, @RequestParam @Nullable String numeroOcorrencia){
        return ResponseEntity.ok(protocoloService.getProtocoloByIdWithParams(id, nomeUnidade, numeroOcorrencia));
    }
    @PostMapping("/erro")
    public ResponseEntity<?> testeError(@RequestBody ProtocoloRequest protocoloRequest){
        throw new NullPointerException();
    }
}
