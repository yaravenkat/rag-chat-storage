package com.yara.ragchat.service;

import com.yara.ragchat.entity.ChatMessage;
import com.yara.ragchat.entity.ChatSession;
import com.yara.ragchat.repo.ChatMessageRepository;
import com.yara.ragchat.repo.ChatSessionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;

@Service
public class ChatService {
    private static final Logger log = LoggerFactory.getLogger(ChatService.class);

    private final ChatSessionRepository sessionRepo;
    private final ChatMessageRepository messageRepo;
    private final CacheManager cacheManager;

    @Autowired
    public ChatService(ChatSessionRepository sessionRepo,
                       ChatMessageRepository messageRepo,
                       CacheManager cacheManager) {
        this.sessionRepo = sessionRepo;
        this.messageRepo = messageRepo;
        this.cacheManager = cacheManager;
    }

    // -------------------------
    // Read operations
    // -------------------------

    public Optional<ChatSession> getSession(String id) {
        return sessionRepo.findById(id);
    }

    /**
     * Returns list of sessions for a user. Cached under "sessionSummaries" by userId.
     */
    @Cacheable(value = "sessionSummaries", key = "#userId")
    public List<ChatSession> getSessionSummaries(String userId) {
        return sessionRepo.findByUserId(userId);
    }

    // -------------------------
    // Cache invalidation
    // -------------------------

    /**
     * Invalidate cached session summary for a user.
     * Evicts key userId from cache "sessionSummaries". Also logs the action.
     */
    public void invalidateSummary(String userId) {
        if (userId == null) {
            log.debug("invalidateSummary called with null userId - ignoring");
            return;
        }

        if (cacheManager == null) {
            log.warn("CacheManager is not configured; cannot evict sessionSummaries cache for user {}", userId);
            return;
        }

        Cache cache = cacheManager.getCache("sessionSummaries");
        if (cache != null) {
            cache.evict(userId);
            log.debug("Evicted cache 'sessionSummaries' for userId={}", userId);
        } else {
            log.debug("Cache 'sessionSummaries' not found; nothing to evict for userId={}", userId);
        }
    }

    // -------------------------
    // Mutation operations
    // -------------------------

    @Transactional
    public ChatSession createSession(String userId, String name) {
        ChatSession s = new ChatSession();
        s.setUserId(userId);
        s.setName(name == null ? "New Session" : name);
        ChatSession saved = sessionRepo.save(s);

        // Invalidate cached summary for this user
        invalidateSummary(userId);

        return saved;
    }

    @Transactional
    public ChatSession renameSession(String id, String newName) {
        ChatSession s = sessionRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("Session not found"));
        s.setName(newName);
        ChatSession saved = sessionRepo.save(s);

        if (saved.getUserId() != null) invalidateSummary(saved.getUserId());
        return saved;
    }

    @Transactional
    public ChatSession setFavorite(String id, boolean favorite) {
        ChatSession s = sessionRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("Session not found"));
        s.setFavorite(favorite);
        ChatSession saved = sessionRepo.save(s);

        if (saved.getUserId() != null) invalidateSummary(saved.getUserId());
        return saved;
    }

    @Transactional
    public void deleteSession(String id) {
        ChatSession s = sessionRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("Session not found"));
        String userId = s.getUserId();
        sessionRepo.delete(s);

        if (userId != null) invalidateSummary(userId);
    }

    @Transactional
    public ChatMessage addMessage(String sessionId, ChatMessage message) {
        ChatSession s = sessionRepo.findById(sessionId).orElseThrow(() -> new IllegalArgumentException("Session not found"));

        message.setSession(s);
        ChatMessage saved = messageRepo.save(message);

        // maintain bidirectional relation on session side if entity has such list
        try {
            s.addMessage(saved);
            sessionRepo.save(s);
        } catch (Exception ex) {
            // if session entity does not have addMessage, ignore - message is persisted and FK is set
            log.debug("ChatSession.addMessage not available or failed; message persisted with session FK. {}", ex.getMessage());
        }

        if (s.getUserId() != null) invalidateSummary(s.getUserId());
        return saved;
    }



    // -------------------------
    // Message retrieval
    // -------------------------

    public Page<ChatMessage> getMessages(String sessionId, int page, int size) {
        ChatSession s = sessionRepo.findById(sessionId).orElseThrow(() -> new IllegalArgumentException("Session not found"));
        Pageable p = PageRequest.of(page, size, Sort.by("createdAt").ascending());
        return messageRepo.findBySessionOrderByCreatedAtAsc(s, p);
    }
}