package com.woobeee.auth.service.impl;

import com.woobeee.auth.api.Response;
import com.woobeee.auth.dto.request.MemberAuthRequest;
import com.woobeee.auth.dto.response.MemberAuthResponse;
import com.woobeee.auth.feign.LoginRequest;
import com.woobeee.auth.util.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * 커스텀 유저 디테일 서비스
 *
 * @author 김병우
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {
	private final LoginRequest loginRequest;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Response<MemberAuthResponse> response = null;
		try {
			response = loginRequest.memberLogin(new MemberAuthRequest(username));
		} catch (Exception e) {
			throw new UsernameNotFoundException("유저 아이디를 찾을 수 없습니다");
		}
		return new CustomUserDetails(response.getBody().getData());
	}
}
