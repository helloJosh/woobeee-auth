package com.woobeee.auth.util;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * Redis 에 저장할 정보
 * 액세스 토큰의 Uuid 를 Key 로 불러올 수 있다.
 *
 * @author 김병우
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class TokenDetails implements Serializable {
	private String username;
	private List<String> auths;
	private Long memberId;
}
