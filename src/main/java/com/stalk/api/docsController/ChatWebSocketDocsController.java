package com.stalk.api.docsController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
@RestController
@RequestMapping("/api/docs/chat")
@Tag(name = "Chat (WebSocket)", description = "WebSocket(STOMP) 채팅 사용 가이드")
public class ChatWebSocketDocsController {

    @GetMapping("/stomp")
    @Operation(
            summary = "STOMP 채팅 사용 방법",
            description = """
                    Swagger는 WebSocket(@MessageMapping)을 직접 노출하지 못합니다.
                    아래 정보를 참고하여 STOMP 연결 및 메시지를 송수신하세요.

                    - WebSocket Endpoint: /ws
                    - SEND: /pub/chat.send
                    - SUBSCRIBE: /sub/chat.{symbol}
                    - 지원 종목: 005930, 000660, 005380, 373220
                    """
    )
    @ApiResponse(
            responseCode = "200",
            description = "STOMP 연결 및 메시지 예시",
            content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(
                            name = "WebSocket Example",
                            value = """
                                    {
                                      "wsEndpoint": "/ws",
                                      "sendDestination": "/pub/chat.send",
                                      "subscribeTopic": "/sub/chat.005930",
                                      "requestPayloadExample": {
                                        "symbol": "005930",
                                        "sender": "익명",
                                        "content": "안녕하세요"
                                      },
                                      "responsePayloadExample": {
                                        "messageId": "550e8400-e29b-41d4-a716-446655440000",
                                        "symbol": "005930",
                                        "content": "안녕하세요",
                                        "sender": "익명",
                                        "sentAt": "2026-02-18T06:30:00Z"
                                      }
                                    }
                                    """
                    )
            )
    )
    public Map<String, Object> stompInfo() {
        return Map.of(
                "wsEndpoint", "/ws",
                "sendDestination", "/pub/chat.send",
                "subscribeTopicPattern", "/sub/chat.{symbol}"
        );
    }
}