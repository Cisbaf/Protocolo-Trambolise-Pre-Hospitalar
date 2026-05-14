package com.viteprotocolo.auth.entity.DTO;

import lombok.Builder;

@Builder
public record UserResponse(
        String username,
        String role,
        String municipio
) {}
