
package com.yara.ragchat.repo;

import com.yara.ragchat.entity.ChatSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatSessionRepository extends JpaRepository<ChatSession, String> {
    List<ChatSession> findByUserId(String userId);
}
