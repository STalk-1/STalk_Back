package com.stalk.api.kis.quote;

import com.stalk.api.kis.quote.dto.Top10QuoteResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class Top10VolumeController {

    private final Top10VolumeService service;

    @GetMapping("/top10/volume")
    public Top10QuoteResponse top10Volume() {
        return service.getTop10();
    }
}
