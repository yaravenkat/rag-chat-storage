package com.yara.ragchat.controller;

import com.yara.ragchat.dto.AddMessageRequest;
import com.yara.ragchat.dto.ChatSessionDto;
import com.yara.ragchat.dto.CreateSessionRequest;
import com.yara.ragchat.entity.ChatMessage;
import com.yara.ragchat.entity.Sender;
import com.yara.ragchat.mapper.ChatSessionMapper;
import com.yara.ragchat.service.ChatService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sessions")
@SecurityRequirement(name = "X-API-KEY")
public class SessionController {

    private final ChatService chatService;

    @Autowired
    public SessionController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping
    public ResponseEntity<ChatSessionDto> create(@Valid @RequestBody CreateSessionRequest req) {
        var s = chatService.createSession(req.getUserId(), req.getSessionName());
        return ResponseEntity.status(HttpStatus.CREATED).body(ChatSessionMapper.toDto(s));
    }

    @GetMapping
    public ResponseEntity<List<ChatSessionDto>> listSessions(@RequestParam String userId) {
        List<ChatSessionDto> sessions = chatService.getSessionSummaries(userId)
                .stream()
                .map(ChatSessionMapper::toDto)
                .toList();
        return ResponseEntity.ok(sessions);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ChatSessionDto> getSession(@PathVariable String id) {
        return chatService.getSession(id)
                .map(ChatSessionMapper::toDto)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/rename")
    public ResponseEntity<ChatSessionDto> rename(@PathVariable String id,
                                                 @Valid @RequestBody CreateSessionRequest body) {
        var s = chatService.renameSession(id, body.getSessionName());
        return ResponseEntity.ok(ChatSessionMapper.toDto(s));
    }

    @PatchMapping("/{id}/favorite")
    public ResponseEntity<ChatSessionDto> favorite(@PathVariable String id,
                                                   @RequestBody java.util.Map<String, Boolean> body) {
        boolean fav = body.getOrDefault("favorite", Boolean.TRUE);
        var s = chatService.setFavorite(id, fav);
        return ResponseEntity.ok(ChatSessionMapper.toDto(s));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        chatService.deleteSession(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/messages")
    public ResponseEntity<ChatMessage> addMessage(@PathVariable String id,
                                                  @Valid @RequestBody AddMessageRequest req) {
        Sender sender = Sender.from(req.getSender());
        ChatMessage m = new ChatMessage();
        m.setSender(sender);
        m.setContent(req.getContent());
        m.setContext(req.getContext());
        ChatMessage saved = chatService.addMessage(id, m);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping("/{id}/messages")
    public ResponseEntity<Page<ChatMessage>> getMessages(@PathVariable String id,
                                                         @RequestParam(defaultValue = "0") int page,
                                                         @RequestParam(defaultValue = "20") int size) {
        Page<ChatMessage> msgs = chatService.getMessages(id, page, size);
        return ResponseEntity.ok(msgs);
    }

    @PostMapping("/users/{userId}/invalidate-summary")
    public ResponseEntity<Void> invalidateSummary(@PathVariable String userId) {
        chatService.invalidateSummary(userId);
        return ResponseEntity.noContent().build();
    }
}
