package com.viteprotocolo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class ViteProtocoloApplication {

    public static void main(String[] args) {
        SpringApplication.run(ViteProtocoloApplication.class, args);
    }

}
