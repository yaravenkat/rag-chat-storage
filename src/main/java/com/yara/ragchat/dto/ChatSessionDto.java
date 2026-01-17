package com.yara.ragchat.dto;

import java.time.Instant;


public record ChatSessionDto(
        String id,
        String userId,
        String sessionName,
        boolean favorite,
        Instant createdAt
) {}
