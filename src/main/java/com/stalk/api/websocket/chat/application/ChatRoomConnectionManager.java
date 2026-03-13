package com.stalk.api.websocket.chat.application;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class ChatRoomConnectionManager {

    // symbol -> Set of session IDs
    // 특정 채팅방에 현재 누가 있는지 확인하는 용도(채팅방 인원수 계산)
    private final Map<String, Set<String>> roomSessions = new ConcurrentHashMap<>();
    
    // session ID -> Set of symbols
    // 특정 유저가 어떤 방들에 들어가 있는지 기억하는 용도(나갈떄 청소용)
    private final Map<String, Set<String>> sessionRooms = new ConcurrentHashMap<>();

    // session ID -> (subscription ID -> symbol)
    // 특정 유저의 구독 ID가 어떤 방(symbol)을 가리키는지 기억하는 용도(unsub 처리용)
    private final Map<String, Map<String, String>> sessionSubscriptions = new ConcurrentHashMap<>();

    private static final String DESTINATION_PREFIX = "/sub/chat.";

    @EventListener
    public void onSessionSubscribe(SessionSubscribeEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = accessor.getSessionId();
        String destination = accessor.getDestination();
        String subscriptionId = accessor.getSubscriptionId();

        if (sessionId != null && destination != null && destination.startsWith(DESTINATION_PREFIX)) {
            String symbol = destination.substring(DESTINATION_PREFIX.length());
            
            roomSessions.computeIfAbsent(symbol, k -> Collections.synchronizedSet(new HashSet<>())).add(sessionId);
            sessionRooms.computeIfAbsent(sessionId, k -> Collections.synchronizedSet(new HashSet<>())).add(symbol);
            
            if (subscriptionId != null) {
                sessionSubscriptions.computeIfAbsent(sessionId, k -> new ConcurrentHashMap<>()).put(subscriptionId, symbol);
            }

            log.info("[STOMP] SUBSCRIBE sessionId={}, symbol={}, subId={}, roomCount={}", 
                    sessionId, symbol, subscriptionId, getConnectedUserCount(symbol));
        }
    }

    @EventListener
    public void onSessionUnsubscribe(SessionUnsubscribeEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = accessor.getSessionId();
        String subscriptionId = accessor.getSubscriptionId();
        
        if (sessionId != null && subscriptionId != null) {
            Map<String, String> subscriptions = sessionSubscriptions.get(sessionId);
            if (subscriptions != null) {
                String symbol = subscriptions.remove(subscriptionId);
                if (symbol != null) {
                    Set<String> sessionsInRoom = roomSessions.get(symbol);
                    if (sessionsInRoom != null) {
                        sessionsInRoom.remove(sessionId);
                        if (sessionsInRoom.isEmpty()) {
                            roomSessions.remove(symbol);
                        }
                    }
                    Set<String> rooms = sessionRooms.get(sessionId);
                    if (rooms != null) {
                        rooms.remove(symbol);
                        if (rooms.isEmpty()) {
                            sessionRooms.remove(sessionId);
                        }
                    }
                    if (subscriptions.isEmpty()) {
                        sessionSubscriptions.remove(sessionId);
                    }
                    log.info("[STOMP] UNSUBSCRIBE Cleanup symbol={}, sessionId={}, subId={}, roomCount={}", 
                            symbol, sessionId, subscriptionId, getConnectedUserCount(symbol));
                } else {
                    log.info("[STOMP] UNSUBSCRIBE sessionId={}, subId={} (No matching topic symbol found)", sessionId, subscriptionId);
                }
            } else {
                log.info("[STOMP] UNSUBSCRIBE sessionId={}, subId={} (No subscriptions map found for session)", sessionId, subscriptionId);
            }
        } else {
            log.info("[STOMP] UNSUBSCRIBE sessionId={}, subId={} (Missing params)", sessionId, subscriptionId);
        }
    }

    @EventListener
    public void onSessionDisconnect(SessionDisconnectEvent event) {
        String sessionId = event.getSessionId();

        if (sessionId != null) {
            sessionSubscriptions.remove(sessionId);
            Set<String> symbols = sessionRooms.remove(sessionId);
            if (symbols != null) {
                for (String symbol : symbols) {
                    Set<String> sessionsInRoom = roomSessions.get(symbol);
                    if (sessionsInRoom != null) {
                        sessionsInRoom.remove(sessionId);
                        log.info("[STOMP] DISCONNECT Cleaned up sessionId={} from symbol={}, roomCount={}", 
                                sessionId, symbol, sessionsInRoom.size());
                        
                        if (sessionsInRoom.isEmpty()) {
                            roomSessions.remove(symbol);
                        }
                    }
                }
            }
        }
    }

    public int getConnectedUserCount(String symbol) {
        Set<String> sessions = roomSessions.get(symbol);
        return sessions != null ? sessions.size() : 0;
    }
}
