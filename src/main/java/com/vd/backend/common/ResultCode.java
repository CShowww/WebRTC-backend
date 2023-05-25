package com.vd.backend.common;

public enum ResultCode {
    // success
    SUCCESS(200, "success"),
    // error
    NOT_FOUND(404, "Not Found"),
    // INTERNAL_ERROR
    INTERNAL_ERROR(500, "Inter service error"),
    //PARAMETER_EXCEPTION
    PARAMETER_EXCEPTION(501, "Parameters Exception"),
    // USER_NOT_EXIST_ERROR
    USER_NOT_EXIST_ERROR(10001, "User not exist."),
    ;

    private Integer code;
    private String message;

    public Integer code() {
        return this.code;
    }

    public String message() {
        return this.message;
    }

    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
