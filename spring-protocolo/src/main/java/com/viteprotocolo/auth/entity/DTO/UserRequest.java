package com.viteprotocolo.auth.entity.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import org.aspectj.weaver.ast.Not;

@Builder
public record UserRequest(
        @NotBlank @Size(min = 6)
        String username,
        @NotBlank @Size(min = 6)
        String password,
        @NotBlank
        String role,
        String municipio
) {
}
