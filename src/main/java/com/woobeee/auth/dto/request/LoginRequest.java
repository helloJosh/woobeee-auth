package com.woobeee.auth.dto.request;

import lombok.Builder;

/**
 * Front에서 받아오는 리퀘스트
 *
 * @param username 유저 이름
 * @param password 비밀번호
 */
@Builder
public record LoginRequest(String username, String password) {
}
