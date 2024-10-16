package com.woobeee.auth.dto.response;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.List;

/**
 * Bookstore 서버로부터 받아 오는 로그인을 위한 멤버 정보 DTO
 *
 * @author 오연수
 */
@Builder
public record MemberAuthResponse(
	@NotNull String email,
	@NotNull String password,
	@NotNull List<String> auth,
	@NotNull Long memberId
) {
}