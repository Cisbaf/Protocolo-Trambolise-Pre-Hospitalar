package com.viteprotocolo.protocolo.service;

import com.viteprotocolo.auth.entity.Municipios;
import com.viteprotocolo.protocolo.entity.AtendenteEntity;
import com.viteprotocolo.protocolo.entity.cadSus.CadSusRequest;
import com.viteprotocolo.protocolo.repository.AtendenteRepository;
import com.viteprotocolo.protocolo.service.client.CadSusClient;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.Normalizer;

@Service
@RequiredArgsConstructor
public class AtendenteService {

    @Value("${jwt.expiration}")
    private long expirationTime;
    private final AtendenteRepository atendenteRepository;
    private final CadSusClient cadSusClient;


    public AtendenteEntity createAttAccount(String cpf) {
        if (cpf == null) {
            return null;
        }

        cpf = cpf.replaceAll("\\D", "");
        System.out.println("cpf: " + cpf);
        if (cpf.length() < 11) {
            throw new IllegalArgumentException();
        }


        var existe = findByCpf(cpf);
        if (existe != null) {
            return existe;
        }
        var cadSus = cadSusClient.getCadSus(new CadSusRequest("cpf", cpf));
        return atendenteRepository.save(new AtendenteEntity(cadSus.getFull_name(), cadSus.getCpf()));
    }

    private AtendenteEntity findByCpf(String cpf) {
        return atendenteRepository.findByCpf(cpf).orElse(null);
    }

    public void setMunicio(HttpServletResponse response, String municipio) {
        var normalizado = Normalizer.normalize(municipio, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
        var muni = Municipios.valueOf(normalizado.trim().toUpperCase().substring(0, 3));

        var time = Integer.parseInt(Long.toString(expirationTime));

        addCookie(response, muni.name(), time);
    }

    private static void addCookie(HttpServletResponse response,
                                  String value, int maxAge) {
        Cookie cookie = new Cookie("municipio_protocolo", value);
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);
        cookie.setAttribute("SameSite", "Strict");

        response.addCookie(cookie);
    }

}
