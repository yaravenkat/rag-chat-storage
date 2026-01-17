
package com.yara.ragchat.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "chat_messages")
@Getter
@Setter
@NoArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ChatMessage {

    @Id
    @Column(length = 36)
    @EqualsAndHashCode.Include
    @ToString.Include
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    @JsonIgnore
    private ChatSession session;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Sender sender;

    @Column(columnDefinition = "text", nullable = false)
    private String content;

    @Column(columnDefinition = "text")
    private String context;

    @Column(nullable = false)
    private OffsetDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (this.id == null) this.id = UUID.randomUUID().toString();
        this.createdAt = OffsetDateTime.now();
    }
}
