package com.viteprotocolo.atendimento.controller;

import com.viteprotocolo.atendimento.entity.Municipio;
import com.viteprotocolo.atendimento.service.AtendenteService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/atendentes")
@RequiredArgsConstructor
public class AtendenteController {

    private final AtendenteService atendenteService;

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

    @PostMapping("/registeratt")
    public ResponseEntity<?> createAttAccount(@RequestBody String cpf) {
        if (cpf == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(atendenteService.createAttAccount(cpf));
    }
    @GetMapping("/municipio")
    public ResponseEntity<Municipio> getMunicipio(HttpServletRequest request){
        var service = atendenteService.isMunicipio(request);
        if (service == null){
            return ResponseEntity.status(403).build();
        }
        return ResponseEntity.ok(service);
    }
}
