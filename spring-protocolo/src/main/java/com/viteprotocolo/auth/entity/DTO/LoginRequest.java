package com.viteprotocolo.auth.entity.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record LoginRequest(
        @NotBlank @Size(min = 6)
        String username,
        @NotBlank @Size(min = 6)
        String password
) {
}
