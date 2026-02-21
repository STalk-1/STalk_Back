package com.stalk.api.websocket;

import com.stalk.api.websocket.dto.ChatMessageResponse;
import com.stalk.api.websocket.dto.ChatSendRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.security.Principal;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Controller
public class ChatMessageController {

    private final SimpMessagingTemplate messagingTemplate;

    private static final Set<String> CONNECTED_SESSIONS = ConcurrentHashMap.newKeySet();

    @EventListener
    public void onSessionConnected(SessionConnectedEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = accessor.getSessionId();
        Principal principal = accessor.getUser();

        if (sessionId != null) {
            CONNECTED_SESSIONS.add(sessionId);
        }

        log.info("[STOMP] CONNECT sessionId={}, user={}, connedtedCount={}",
                sessionId,
                principal != null? principal.getName():"anonymous",
                CONNECTED_SESSIONS.size());
    }

    @EventListener
    public void onSessionDisconnect(SessionDisconnectEvent event) {
        String sessionId = event.getSessionId();
        Principal principal = event.getUser();

        if (sessionId != null) {
            CONNECTED_SESSIONS.remove(sessionId);
        }

        log.info("[STOMP] DISCONNECT sessionId={}, user={}, closeStatus={}. connectedCount={},",
                sessionId,
                principal != null? principal.getName():"anonymous",
                event.getCloseStatus(),
                CONNECTED_SESSIONS.size());
    }


    public ChatMessageController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * Client SEND -> /pub/chat.send
     * Server PUSH -> /sub/chat.{symbol}
     */
    @MessageMapping("/chat.send")
    public void send(ChatSendRequest req) {
        if (req == null) {
            log.warn("[CHAT] send called with null payload");
            return;
        }

        String symbol = trim(req.symbol());
        String sender = trim(req.sender());
        String content = trim(req.content());

        /**
         * validation 추가하기
         */

        log.info("[CHAT] Message received. symbol={}, sender={}, contentLen={}",
                symbol, sender, content.length());

        final ChatStockSymbol stock;
        try {
            stock = ChatStockSymbol.fromSymbol(symbol);
        } catch (IllegalArgumentException e) {
            log.warn("[CHAT] Unknown stock symbol. symbol={}", symbol);
            return;
        }

        ChatMessageResponse payload = new ChatMessageResponse(
                UUID.randomUUID().toString(),
                stock.getSymbol(),
                content,
                sender,
                Instant.now()
        );

        String topic = "/sub/chat." + stock.getSymbol();
        log.info("[CHAT] Broadcasting to topic={}", topic);

        // 종목별 토픽으로 브로드캐스팅
        messagingTemplate.convertAndSend(topic, payload);
    }

    private static String trim(String s) {
        return s == null ? "" : s.trim();
    }
}
