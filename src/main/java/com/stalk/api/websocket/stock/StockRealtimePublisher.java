package com.stalk.api.websocket.stock;

import com.stalk.api.websocket.stock.dto.ChartPointAddedMessage;
import com.stalk.api.websocket.stock.dto.QuoteUpdatedMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockRealtimePublisher {

    private final SimpMessagingTemplate messagingTemplate;

    public void publishQuote(QuoteUpdatedMessage message) {
        String destination = "/sub/stocks/" + message.symbol();
        log.debug("[WEBSOCKET_STOCK] Broadcasting quote to topic={}, symbol={}", destination, message.symbol());
        messagingTemplate.convertAndSend(destination, message);
    }

    public void publishChartPointAdded(String symbol, String interval, OffsetDateTime time, BigDecimal close) {
        ChartPointAddedMessage message = ChartPointAddedMessage.of(symbol, interval, time, close);
        String destination = "/sub/stocks/chart/" + symbol;
        log.debug("[WEBSOCKET_STOCK] Broadcasting chart point to topic={}, symbol={}, time={}, close={}", 
                destination, symbol, time, close);
        messagingTemplate.convertAndSend(destination, message);
    }
}
