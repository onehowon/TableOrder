package com.ebiz.tableorder.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RootController {
    private static final Logger log = LoggerFactory.getLogger(RootController.class);

    @GetMapping("/")
    public CommonResponse<String> root() {
        log.info("### RootController#root() invoked ###");
        return CommonResponse.success("OK", "루트 정상 작동");
    }
}