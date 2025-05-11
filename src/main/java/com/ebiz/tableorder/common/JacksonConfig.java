package com.ebiz.tableorder.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.ZoneId;
import java.util.TimeZone;

@Configuration
public class JacksonConfig {

    @Bean
    public ObjectMapper jacksonObjectMapper() {
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        // LocalDateTime → ISO 포맷(+09:00) 포함하도록 설정
        // (필요하다면 개별 포맷터 등록도 가능)

        ObjectMapper om = new ObjectMapper()
                .registerModule(javaTimeModule)
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // 전역 타임존 설정: Asia/Seoul
        om.setTimeZone(TimeZone.getTimeZone(ZoneId.of("Asia/Seoul")));

        return om;
    }
}