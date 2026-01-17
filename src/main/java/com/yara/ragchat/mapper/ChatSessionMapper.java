package com.yara.ragchat.mapper;

import com.yara.ragchat.dto.ChatSessionDto;
import com.yara.ragchat.entity.ChatSession;

public class ChatSessionMapper {

    public static ChatSessionDto toDto(ChatSession s) {
        return new ChatSessionDto(
                s.getId(),
                s.getUserId(),
                s.getName(),
                s.isFavorite(),
                s.getCreatedAt().toInstant()
        );
    }
}
