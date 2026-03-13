package com.stalk.api.websocket.chat.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChatMessageRequest(
        @NotBlank(message = "종목 코드는 필수입니다.")
        String symbol,

        @NotBlank(message = "발신자 이름은 필수입니다.")
        @Size(max = 20, message = "발신자 이름은 20자를 초과할 수 없습니다.")
        String sender,

        @NotBlank(message = "메시지 내용은 필수입니다.")
        @Size(max = 1000, message = "메시지 내용은 1000자를 초과할 수 없습니다.")
        String content
) {
}
