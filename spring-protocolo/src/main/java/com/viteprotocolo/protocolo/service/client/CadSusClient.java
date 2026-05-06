package com.viteprotocolo.protocolo.service.client;

import com.viteprotocolo.protocolo.entity.cadSus.CadSusRequest;
import com.viteprotocolo.protocolo.entity.cadSus.CadSusResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;

@Service
@FeignClient(name = "CadSus", url = "https://cadsusapi.cisbaf.org.br/")
public interface CadSusClient {
    @PostMapping
    CadSusResponse getCadSus(CadSusRequest cadSusDTO);
}
