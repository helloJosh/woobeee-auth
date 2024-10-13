package com.woobeee.auth.filter;

import com.woobeee.auth.repository.RedisRepository;
import com.woobeee.auth.service.TokenService;
import com.woobeee.auth.util.CookieUtil;
import com.woobeee.auth.util.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.util.Objects;

/**
 * 커스텀한 로그아웃 필터
 * /auth/logout 경로로 들어오면 로그아웃 진행
 *
 * @author 김병우
 */
@RequiredArgsConstructor
public class CustomLogoutFilter extends GenericFilterBean {
	private final JwtUtil jwtUtil;
	private final CookieUtil cookieUtil;
	private final RedisRepository redisRepository;

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws
		IOException,
		ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;

		String requestUri = httpRequest.getRequestURI();
		if (!requestUri.equals("/api/v1/auth/logout")) {
			filterChain.doFilter(httpRequest, httpResponse);
			return;
		}

		if (!httpRequest.getMethod().equals("POST")) {
			filterChain.doFilter(httpRequest, httpResponse);
			return;
		}

		String refresh = cookieUtil.getRefreshTokenFromCookies(httpRequest);

		if(Objects.nonNull(refresh)) {
			String uuid = jwtUtil.getUuid(refresh);

			redisRepository.deleteRefreshToken(uuid);
			redisRepository.deleteTokenDetail(uuid);
		}

		cookieUtil.deleteCookie(httpResponse, "Refresh");
		httpResponse.setStatus(HttpServletResponse.SC_OK);

	}
}
