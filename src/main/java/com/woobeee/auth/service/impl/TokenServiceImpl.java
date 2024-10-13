package com.woobeee.auth.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.woobeee.auth.feign.LoginRequest;
import com.woobeee.auth.repository.RedisRepository;
import com.woobeee.auth.service.TokenService;
import com.woobeee.auth.util.JwtUtil;
import com.woobeee.auth.util.TokenDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 토큰 서비스 구현체
 *
 * @author 김병우
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {
	private final String TOKEN_DETAILS = "TOKEN_DETAILS";
	private final String REFRESH_TOKEN = "REFRESH_TOKEN";
	private final String ACCESS_TOKEN = "ACCESS_TOKEN";
	private final long ACCESS_TOKEN_EXPIRE_TIME = 1000 * 60 * 30; //30분
	private final long REFRESH_TOKEN_EXPIRE_TIME = 1000 * 60 * 60 * 24; //하루

	private final RedisRepository redisRepository;
	private final JwtUtil jwtUtil;
	private final RedisTemplate<String, Object> redisTemplate;
	private final ObjectMapper objectMapper;

	@Override
	public List<String> generateToken(String username, List<String> auths, Long memberId) {
		String uuid = UUID.randomUUID().toString();
		log.info("새로운 uuid: {}", uuid);

		TokenDetails tokenDetails = TokenDetails.builder()
				.username(username)
				.auths(auths)
				.memberId(memberId)
				.build();

		String accessToken = jwtUtil.generateToken("Access", username, memberId, auths, uuid);
		String refreshToken = jwtUtil.generateToken("Refresh", username, memberId, auths, uuid);


		try {
			String tokenDetailsString = objectMapper.writeValueAsString(tokenDetails);
			redisRepository.save(TOKEN_DETAILS, tokenDetailsString, uuid, ACCESS_TOKEN_EXPIRE_TIME);
			log.info("Redis id {} : access {}", uuid, accessToken);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}

		redisRepository.save(REFRESH_TOKEN, refreshToken, uuid, REFRESH_TOKEN_EXPIRE_TIME);

		return List.of(accessToken, refreshToken);
	}

	@Override
	public boolean isAccessTokenActive(String accessToken) {
        return !jwtUtil.isExpired(accessToken);
    }

	@Override
	public boolean isRefreshTokenActive(String refreshToken) {
		return !jwtUtil.isExpired(refreshToken);
	}

	@Override
	public String getUuid(String token) {
		return jwtUtil.getUuid(token);
	}
}
