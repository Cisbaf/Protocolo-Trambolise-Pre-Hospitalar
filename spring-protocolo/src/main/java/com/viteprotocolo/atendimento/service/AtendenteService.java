package com.viteprotocolo.atendimento.service;

import com.viteprotocolo.atendimento.entity.Municipio;
import com.viteprotocolo.auth.entity.Municipios;
import com.viteprotocolo.atendimento.entity.AtendenteEntity;
import com.viteprotocolo.atendimento.entity.cadSus.CadSusRequest;
import com.viteprotocolo.atendimento.repository.AtendenteRepository;
import com.viteprotocolo.atendimento.service.client.CadSusClient;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.text.Normalizer;

import static com.viteprotocolo.atendimento.service.AtendimentoService.getCookieValue;

@Service
@RequiredArgsConstructor
public class AtendenteService {

    private static final Logger log = LoggerFactory.getLogger(AtendenteService.class);

    private final AtendenteRepository atendenteRepository;
    private final CadSusClient cadSusClient;


    public AtendenteEntity createAttAccount(String cpf) {
        if (cpf == null) {
            return null;
        }

        cpf = cpf.replaceAll("\\D", "");
        if (cpf.length() < 11) {
            throw new IllegalArgumentException("CPF inválido: menos de 11 dígitos");
        }

        var existe = findByCpf(cpf);
        if (existe != null) {
            return existe;
        }

        try {
            var cadSus = cadSusClient.getCadSus(new CadSusRequest("cpf", cpf));
            return atendenteRepository.save(new AtendenteEntity(cadSus.getFull_name(), cadSus.getCpf()));
        } catch (Exception e) {
            log.warn("CadSus indisponível ou CPF não encontrado ({}): {}. Atendente não vinculado.", cpf, e.getMessage());
            return null;
        }
    }

    private AtendenteEntity findByCpf(String cpf) {
        return atendenteRepository.findByCpf(cpf).orElse(null);
    }

    public void setMunicio(HttpServletResponse response, String municipio) {
        if (municipio.length() < 3) return;
        var normalizado = Normalizer.normalize(municipio, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
        var muni = Municipios.valueOf(normalizado.trim().toUpperCase().substring(0, 3));

        long expirationTime = 60 * 60 * 24 * 365;
        var time = Integer.parseInt(Long.toString(expirationTime));

        addCookie(response, muni.name(), time);
    }

    public Municipio isMunicipio(HttpServletRequest request){
        String cookie = getCookieValue(request);

        if (cookie == null || cookie.isBlank()) {
            return null;
        }

        try {

            Municipios municipio = Municipios.valueOf(cookie.toUpperCase());

            return Municipio.builder()
                    .nome(municipio.getNomeExibicao().toUpperCase())
                    .codigo(cookie)
                    .build();

        } catch (IllegalArgumentException e) {

            return null;
        }
    }

    private static void addCookie(HttpServletResponse response,
                                  String value, int maxAge) {
        Cookie cookie = new Cookie("municipio_atendente", value);
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);
        cookie.setAttribute("SameSite", "Strict");

        response.addCookie(cookie);
    }



}
