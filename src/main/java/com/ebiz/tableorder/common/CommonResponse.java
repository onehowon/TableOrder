package com.ebiz.tableorder.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class CommonResponse {
    private final int code;
    private final String message;
    private final T data;

    public static <T> CommonResponse<T> success(T data){
        return CommonResponse.<T>builder()
                .code(200)
                .message(message)
                .data(data)
                .build();
    }

    public static <T> CommonResponse<T> error(String message, int code){
        return CommonResponse.<T>builder()
                .code(code)
                .message(message)
                .data(null)
                .build();
    }
}
