package com.sixlv6.backend.common;

/**
 * 统一响应体。前端只需要判断 code == 0 就代表业务成功。
 *
 * <p>约定：
 * <ul>
 *   <li>HTTP 状态码表达"传输/协议层"结果（200/400/401/403/404/500）</li>
 *   <li>code 表达"业务层"结果（0 成功，非 0 为业务错误码）</li>
 * </ul>
 */
public record ApiResponse<T>(int code, String message, T data) {

    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(0, "ok", data);
    }

    public static ApiResponse<Void> ok() {
        return new ApiResponse<>(0, "ok", null);
    }

    public static <T> ApiResponse<T> error(int code, String message) {
        return new ApiResponse<>(code, message, null);
    }
}
