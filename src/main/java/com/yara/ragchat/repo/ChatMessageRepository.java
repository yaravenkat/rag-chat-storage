package com.yara.ragchat.repo;

import com.yara.ragchat.entity.ChatMessage;
import com.yara.ragchat.entity.ChatSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, String> {
    Page<ChatMessage> findBySessionOrderByCreatedAtAsc(ChatSession session, Pageable pageable);
}
