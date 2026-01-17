package com.yara.ragchat.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "chat_sessions")
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = "messages")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ChatSession implements Serializable {

    @Id
    @Column(length = 36)
    @EqualsAndHashCode.Include
    private String id;

    private String userId;
    private String name;
    private boolean favorite;

    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    @OneToMany(
            mappedBy = "session",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<ChatMessage> messages = new ArrayList<>();

    public void addMessage(ChatMessage message) {
        if (message == null) return;

        if (this.messages == null) {
            this.messages = new ArrayList<>();
        }

        message.setSession(this);

        if (!this.messages.contains(message)) {
            this.messages.add(message);
        }

        this.updatedAt = OffsetDateTime.now();
    }


    @PrePersist
    protected void onCreate() {
        if (this.id == null) this.id = UUID.randomUUID().toString();
        this.createdAt = OffsetDateTime.now();
        this.updatedAt = this.createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = OffsetDateTime.now();
    }
}
