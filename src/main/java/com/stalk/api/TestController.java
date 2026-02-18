package com.stalk.api;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
@Tag(name = "Test API", description = "Swagger 확인용 API")
public class TestController {

    /**
     * OCI 확인용 단순 GET API
     */
    @GetMapping("/ping")
    public Map<String, Object> ping() {
        return Map.of(
                "status", "ok",
                "message", "working",
                "timestamp", Instant.now()
        );
    }

    /**
     * Swagger 요청 바디 테스트용 POST API
     */
    @PostMapping("/echo")
    public Map<String, Object> echo(@RequestBody Map<String, Object> body) {
        return Map.of(
                "received", body,
                "timestamp", Instant.now()
        );
    }


}
