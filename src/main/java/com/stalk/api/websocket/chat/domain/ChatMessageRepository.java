package com.stalk.api.websocket.chat.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, UUID> {

    /**
     * 특정 채팅방(symbol)의 최근 메시지를 보낸 시간 내림차순으로 최대 50건 조회합니다.
     * 결과를 시간 오름차순으로 표시하려면 서비스 레이어에서 역순 정렬이 필요합니다.
     */
    List<ChatMessage> findTop50BySymbolOrderBySentAtDesc(String symbol);
}
