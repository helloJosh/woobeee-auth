package com.woobeee.auth.service;

import com.woobeee.auth.util.TokenDetails;

import java.util.List;

/**
 * 토큰 서비스 인터페이스
 *
 * @author 김병우
 */
public interface TokenService {
	/**
	 * 액세스 토큰, 리프레시 토큰을 새로 만들어 리스트를 반환한다.
	 *
	 * @param username 이메일
	 * @param auths 권한 스트링 리스트
	 * @param memberId 멤버 아이디
	 * @return 토큰 값 담긴 리스트
	 */
	List<String> generateToken(String username, List<String> auths, Long memberId);

	/**
	 * 액서스토큰 만료 여부 조회.
	 *
	 * @param accessToken 액서스토큰
	 * @return 참거짓
	 */
	boolean isAccessTokenActive(String accessToken);

	/**
	 * 리프레쉬 토큰 만료 여부.
	 *
	 * @param refreshToken 리프레쉬 토큰
	 * @return 참거짓
	 */
	boolean isRefreshTokenActive(String refreshToken);

	/**
	 * uuid 조회.
	 *
	 * @param token 토큰
	 * @return uuid
	 */
	String getUuid(String token);

}
