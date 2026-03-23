package com.stalk.api.websocket.chat.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Getter
@Entity
@Table(name = "chat_messages", indexes = {
        @Index(name = "idx_chat_messages_symbol_sent_at", columnList = "symbol, sent_at")
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "symbol", nullable = false, length = 20)
    private String symbol;

    @Column(name = "sender", nullable = false, length = 20)
    private String sender;

    @Column(name = "content", nullable = false, length = 1000)
    private String content;

    @CreationTimestamp
    @Column(name = "sent_at", updatable = false, nullable = false)
    private Instant sentAt;

    @Builder
    private ChatMessage(String symbol, String sender, String content) {
        this.symbol = symbol;
        this.sender = sender;
        this.content = content;
    }
}
