package com.stalk.api.websocket.chat.application;

import com.stalk.api.websocket.chat.domain.ChatMessage;
import com.stalk.api.websocket.chat.domain.ChatMessageRepository;
import com.stalk.api.websocket.chat.presentation.dto.ChatMessageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatHistoryService {

    private final ChatMessageRepository chatMessageRepository;
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    public List<ChatMessageResponse> getRecentMessages(String symbol) {
        List<ChatMessage> messages = chatMessageRepository.findTop50BySymbolOrderBySentAtAsc(symbol);
        
        return messages.stream()
                .map(msg -> {
                    String timeLabel = OffsetDateTime.ofInstant(msg.getSentAt(), ZoneId.of("Asia/Seoul"))
                            .format(TIME_FORMATTER);
                    return new ChatMessageResponse(
                            msg.getId().toString(),
                            msg.getSymbol(),
                            msg.getContent(),
                            msg.getSender(),
                            timeLabel
                    );
                })
                .toList();
    }
}
