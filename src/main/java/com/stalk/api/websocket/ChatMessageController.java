package com.stalk.api.websocket;

import com.stalk.api.websocket.dto.ChatMessageResponse;
import com.stalk.api.websocket.dto.ChatSendRequest;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.time.Instant;
import java.util.UUID;

@Controller
public class ChatMessageController {

    private final SimpMessagingTemplate messagingTemplate;

    public ChatMessageController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * Client SEND -> /pub/chat.send
     * Server PUSH -> /sub/chat.{symbol}
     */
    @MessageMapping("/chat.send")
    public void send(ChatSendRequest req) {
        if (req == null) return;

        String symbol = trim(req.symbol());
        String sender = trim(req.sender());
        String content = trim(req.content());

        /**
         * validation 추가하기
         */

        final ChatStockSymbol stock;
        try {
            stock = ChatStockSymbol.fromSymbol(symbol);
        } catch (IllegalArgumentException e) {
            return;
        }

        ChatMessageResponse payload = new ChatMessageResponse(
                UUID.randomUUID().toString(),
                stock.getSymbol(),
                content,
                sender,
                Instant.now()
        );

        // 종목별 토픽으로 브로드캐스팅
        messagingTemplate.convertAndSend("/sub/chat." + stock.getSymbol(), payload);
    }

    private static String trim(String s) {
        return s == null ? "" : s.trim();
    }
}
