package com.ebiz.tableorder.common;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    @GetMapping("/health")
    public CommonResponse<String> health() {
        return CommonResponse.success("pong", "헬스체크 성공");
    }
}