package com.stalk.api.websocket.chat.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, UUID> {

    /**
     * 특정 채팅방(symbol)의 최근 메시지를 보낸 시간 오름차순으로 최대 50건 조회합니다.
     */
    List<ChatMessage> findTop50BySymbolOrderBySentAtAsc(String symbol);
}
