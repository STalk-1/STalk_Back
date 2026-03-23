package com.stalk.api.websocket.chat.application;

import com.stalk.api.websocket.chat.domain.ChatMessage;
import com.stalk.api.websocket.chat.domain.ChatMessageRepository;
import com.stalk.api.websocket.chat.presentation.dto.ChatMessageRequest;
import com.stalk.api.websocket.chat.presentation.dto.ChatMessageResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMessageRepository chatMessageRepository;

    @Transactional
    public void processAndSendMessage(ChatMessageRequest request) {
        log.info("[CHAT] Processing message. symbol={}, sender={}, contentLen={}",
                request.symbol(), request.sender(), request.content().length());

        // 1. 엔티티 생성 및 DB 저장
        ChatMessage chatMessage = ChatMessage.builder()
                .symbol(request.symbol().trim())
                .sender(request.sender().trim())
                .content(request.content().trim())
                .build();
        
        ChatMessage savedMessage = chatMessageRepository.save(chatMessage);

        // 2. 한국 시간 기준으로 시:분 포맷 설정 (저장된 시간 기준)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        String timeLabel = OffsetDateTime.ofInstant(savedMessage.getSentAt(), ZoneId.of("Asia/Seoul")).format(formatter);

        ChatMessageResponse response = new ChatMessageResponse(
                savedMessage.getId().toString(),
                savedMessage.getSymbol(),
                savedMessage.getContent(),
                savedMessage.getSender(),
                timeLabel
        );

        String topic = "/sub/chat." + response.symbol();
        log.info("[CHAT] Broadcasting to topic={}, messageId={}", topic, response.messageId());

        messagingTemplate.convertAndSend(topic, response);
    }
}
