package com.stalk.api.websocket.chat.application;

import com.stalk.api.websocket.chat.presentation.dto.ChatMessageRequest;
import com.stalk.api.websocket.chat.presentation.dto.ChatMessageResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private final SimpMessagingTemplate messagingTemplate;

    public void processAndSendMessage(ChatMessageRequest request) {
        log.info("[CHAT] Processing message. symbol={}, sender={}, contentLen={}",
                request.symbol(), request.sender(), request.content().length());

        // 한국 시간 기준으로 시:분 포맷 설정
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        String timeLabel = OffsetDateTime.now(ZoneId.of("Asia/Seoul")).format(formatter);

        ChatMessageResponse response = new ChatMessageResponse(
                UUID.randomUUID().toString(),
                request.symbol().trim(),
                request.content().trim(),
                request.sender().trim(),
                timeLabel
        );

        String topic = "/sub/chat." + response.symbol();
        log.info("[CHAT] Broadcasting to topic={}", topic);

        messagingTemplate.convertAndSend(topic, response);
    }
}
