package com.stalk.api.websocket.stock;

import com.stalk.api.websocket.stock.dto.QuoteUpdatedMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StockRealtimePublisher {

    private final SimpMessagingTemplate messagingTemplate;

    public void publishQuote(QuoteUpdatedMessage message) {
        String destination = "/sub/stocks" + message.symbol();
        messagingTemplate.convertAndSend(destination, message);
    }
}
