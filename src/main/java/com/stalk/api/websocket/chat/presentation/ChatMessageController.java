package com.stalk.api.websocket.chat.presentation;

import com.stalk.api.websocket.chat.application.ChatService;
import com.stalk.api.websocket.chat.presentation.dto.ChatMessageRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.security.Principal;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatMessageController {

    private final ChatService chatService;

    @EventListener
    public void onSessionConnected(SessionConnectedEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = accessor.getSessionId();
        Principal principal = accessor.getUser();

        log.info("[STOMP] CONNECT sessionId={}, user={}",
                sessionId,
                principal != null ? principal.getName() : "anonymous");
    }

    @EventListener
    public void onSessionDisconnect(SessionDisconnectEvent event) {
        String sessionId = event.getSessionId();
        Principal principal = event.getUser();

        log.info("[STOMP] DISCONNECT sessionId={}, user={}, closeStatus={}",
                sessionId,
                principal != null ? principal.getName() : "anonymous",
                event.getCloseStatus());
    }

    /**
     * Client SEND -> /pub/chat.send
     * Server PUSH -> /sub/chat.{symbol}
     */
    @MessageMapping("/chat.send")
    public void send(@Payload @Validated ChatMessageRequest req) {
        chatService.processAndSendMessage(req);
    }
}
