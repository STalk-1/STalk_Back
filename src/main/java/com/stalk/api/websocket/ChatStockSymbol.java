package com.stalk.api.websocket;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum ChatStockSymbol {

    SAMSUNG("005930", "삼성전자"),
    SK_HYNIX("000660", "SK하이닉스"),
    HYUNDAI("005380", "현대차"),
    LG_ENERGY("373220", "LG에너지솔루션");

    private final String symbol;
    private final String displayName;

    ChatStockSymbol(String symbol, String displayName) {
        this.symbol = symbol;
        this.displayName = displayName;
    }

    public static ChatStockSymbol fromSymbol(String symbol) {
        return Arrays.stream(values())
                .filter(s -> s.symbol.equals(symbol))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid stock symbol: " + symbol));
    }

}
