package com.stalk.api.websocket.stock;

import com.stalk.api.websocket.stock.dto.ChartPointAddedMessage;
import com.stalk.api.websocket.stock.dto.QuoteUpdatedMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class StockRealtimePublisher {

    private final SimpMessagingTemplate messagingTemplate;

    public void publishQuote(QuoteUpdatedMessage message) {
        String destination = "/sub/stocks/" + message.symbol();
        messagingTemplate.convertAndSend(destination, message);
    }

    public void publishChartPointAdded(String symbol, String interval, OffsetDateTime time, BigDecimal close) {
        ChartPointAddedMessage message = ChartPointAddedMessage.of(symbol, interval, time, close);
        String destination = "/sub/stocks/chart/" + symbol;
        messagingTemplate.convertAndSend(destination, message);
    }
}
