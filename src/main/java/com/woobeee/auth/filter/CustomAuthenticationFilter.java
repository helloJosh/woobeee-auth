package com.woobeee.auth.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.woobeee.auth.api.ErrorResponse;
import com.woobeee.auth.api.Response;
import com.woobeee.auth.dto.request.LoginRequest;
import com.woobeee.auth.dto.response.LoginResponse;
import com.woobeee.auth.repository.RedisRepository;
import com.woobeee.auth.service.TokenService;
import com.woobeee.auth.util.CookieUtil;
import com.woobeee.auth.util.CustomUserDetails;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * 커스텀한 인증 필터
 * /auth/login 경로로 들어오면 동작한다.
 * email, password 로 로그인 동작하며,
 * 성공 시 JWT 생성 후 응답 헤더에 Authorization Header 와 Refresh cookie 가 추가된다.
 *
 * @author 김병우
 */
public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

	private final AuthenticationManager authenticationManager;
	private final ObjectMapper objectMapper;
	private final CookieUtil cookieUtil;
	private final TokenService tokenService;
	private final RedisRepository redisRepository;

	public CustomAuthenticationFilter(AuthenticationManager authenticationManager, ObjectMapper objectMapper, TokenService tokenService, CookieUtil cookieUtil, RedisRepository redisRepository) {
		this.authenticationManager = authenticationManager;
		this.objectMapper = objectMapper;
		this.tokenService = tokenService;
		this.cookieUtil = cookieUtil;
		this.redisRepository = redisRepository;
		this.setFilterProcessesUrl("/api/v1/auth/login");
	}

	@Override
	public Authentication attemptAuthentication (HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException {
		LoginRequest loginRequest = null;
		try {
			loginRequest = objectMapper.readValue(request.getInputStream(), LoginRequest.class);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(loginRequest.username(),
			loginRequest.password());
		return authenticationManager.authenticate(authToken);
	}

	@Override
	protected void successfulAuthentication
			(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult)
			throws IOException, ServletException {

		CustomUserDetails customUserDetails = (CustomUserDetails)authResult.getPrincipal();

		String username = customUserDetails.getUsername();
		Long memberId = customUserDetails.getMemberId();

		Collection<? extends GrantedAuthority> authorities = authResult.getAuthorities();

		List<String> auths = authorities.stream()
			.map(GrantedAuthority::getAuthority)
			.toList();

		String refresh = cookieUtil.getRefreshTokenFromCookies(request);
		String access = cookieUtil.getAccessTokenFromCookies(request);

		if (!(Objects.nonNull(refresh) && Objects.nonNull(access) && tokenService.isAccessTokenActive(access))) {
			List<String> tokens = tokenService.generateToken(username, auths, memberId);
			access = tokens.get(0);
			refresh = tokens.get(1);
		} else {
			String uuid = tokenService.getUuid(refresh);

			if (redisRepository.getRefreshToken(uuid).equals(refresh)) {
				// 리프레쉬 토큰 다르면 에러 출력 -> 변조

				ErrorResponse errorResponse = ErrorResponse.builder()
						.title("리프레쉬 토큰이 다릅니다. 변조 위험이 있습니다.")
						.status(HttpStatus.UNAUTHORIZED.value())
						.timestamp(LocalDateTime.now()).build();

				ObjectMapper objectMapper = new ObjectMapper();
				String jsonResponse = objectMapper.writeValueAsString(errorResponse);

				response.setStatus(HttpStatus.UNAUTHORIZED.value());
				response.setContentType("application/json");
				response.setCharacterEncoding("UTF-8");
				response.getWriter().write(jsonResponse);

				return;
			}

		}

		SecurityContextHolder.getContext().setAuthentication(authResult);

		response.addHeader("Authorization", "Bearer " + access);
		response.addCookie(cookieUtil.createCookie("Refresh", refresh));
		response.addCookie(cookieUtil.createCookie("Access", access));
		response.setStatus(HttpStatus.OK.value());
		Response<LoginResponse> apiResponse = Response.success(new LoginResponse("인증 성공"));

		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentType("application/json;charset=UTF-8");
		response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
	}

	@Override
	protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
		AuthenticationException failed) throws IOException, ServletException {

		ErrorResponse errorResponse = ErrorResponse.builder()
				.title("로그인 실패")
				.status(HttpStatus.UNAUTHORIZED.value())
				.timestamp(LocalDateTime.now()).build();

		ObjectMapper objectMapper = new ObjectMapper();
		String jsonResponse = objectMapper.writeValueAsString(errorResponse);

		response.setStatus(HttpStatus.UNAUTHORIZED.value());
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(jsonResponse);
	}
}
