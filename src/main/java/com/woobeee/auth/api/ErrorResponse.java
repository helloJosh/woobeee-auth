package com.woobeee.auth.api;

import lombok.Builder;

import java.time.LocalDateTime;

/**
 * 예외 처리 폼.
 *
 * @param title 에러 이름
 * @param status 에러 코드
 * @param timestamp 에러 시간
 */
@Builder
public record ErrorResponse(
        String title,
        int status,
        LocalDateTime timestamp) {
}