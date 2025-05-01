package com.ebiz.tableorder.common;

import lombok.Getter;

@Getter
public class ReportableError extends RuntimeException{
    private final int code;

    public ReportableError(int code, String message){
        super(message)
        this.code = code;
    }

    public CommonResponse<Object> toResponse(){
        return CommonResponse.error(getMessage(),code);
    }
}
