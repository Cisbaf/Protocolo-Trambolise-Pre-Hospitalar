package com.viteprotocolo.auth.entity;

import lombok.Builder;

@Builder
public record UserResponse(
        long id,
        String username,
        String password
) {
}
