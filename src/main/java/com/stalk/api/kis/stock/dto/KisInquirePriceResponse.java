package com.stalk.api.kis.stock.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "국내 주식 현재가 시세 응답")
public record KisInquirePriceResponse(
        @JsonProperty("rt_cd") String rtCd,
        @JsonProperty("msg_cd") String msgCd,
        @JsonProperty("msg1") String msg1,
        @JsonProperty("output") Output output
) {
    public record Output(
            @JsonProperty("stck_prpr") String currentPrice,
            @JsonProperty("prdy_vrss") String change,
            @JsonProperty("prdy_vrss_sign") String sign,
            @JsonProperty("prdy_ctrt") String changeRate,
            @JsonProperty("acml_vol") String accVolume,
            @JsonProperty("acml_tr_pbmn") String accAmount,
            @JsonProperty("stck_oprc") String openPrice,
            @JsonProperty("stck_hgpr") String highPrice,
            @JsonProperty("stck_lwpr") String lowPrice
    ) {}
}