package com.stalk.api.kis.stock;

import org.springframework.stereotype.Component;

import java.util.Map;


@Component
public class StockMasterProvider {

    private final Map<String, StockMaster> map = Map.of(
            "000660", new StockMaster("000660", "SK하이닉스", "KOSPI"),
            "005930", new StockMaster("005930", "삼성전자", "KOSPI"),
            "009150", new StockMaster("009150", "삼성전기", "KOSPI"),
            "034730", new StockMaster("034730", "SK", "KOSPI"),
            "005380", new StockMaster("005380", "현대차", "KOSPI"),
            "079550", new StockMaster("079550", "LIG넥스원", "KOSPI"),
            "272210", new StockMaster("272210", "한화시스템", "KOSPI"),
            "064350", new StockMaster("064350", "현대로템", "KOSPI"),
            "011200", new StockMaster("011200", "HMM", "KOSPI"),
            "006800", new StockMaster("006800", "미래에셋증권", "KOSPI")
    );

    public StockMaster getStockMasterFromCode(String code) {
        return map.get(code);
    }
}
