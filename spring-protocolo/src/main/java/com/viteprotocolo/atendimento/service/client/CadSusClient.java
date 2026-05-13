package com.viteprotocolo.atendimento.service.client;

import com.viteprotocolo.atendimento.entity.cadSus.CadSusRequest;
import com.viteprotocolo.atendimento.entity.cadSus.CadSusResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;

@Service
@FeignClient(name = "CadSus", url = "https://cadsusapi.cisbaf.org.br/")
public interface CadSusClient {
    @PostMapping
    CadSusResponse getCadSus(CadSusRequest cadSusDTO);
}
