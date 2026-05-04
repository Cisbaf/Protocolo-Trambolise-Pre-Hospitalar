package com.viteprotocolo.auth.entity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record AdminRequest(
        @NotBlank @Size(min = 6)
        String username,
        @NotBlank @Size(min = 6)
        String password
) {
}
