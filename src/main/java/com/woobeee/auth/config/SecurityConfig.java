package com.woobeee.auth.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.woobeee.auth.filter.CustomAuthenticationFilter;
import com.woobeee.auth.filter.CustomLogoutFilter;
import com.woobeee.auth.repository.RedisRepository;
import com.woobeee.auth.service.TokenService;
import com.woobeee.auth.util.CookieUtil;
import com.woobeee.auth.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {

	private final AuthenticationConfiguration authenticationConfiguration;
	private final ObjectMapper objectMapper;
	private final TokenService tokenService;
	private final JwtUtil jwtUtil;
	private final CookieUtil cookieUtil;
	private final RedisRepository redisRepository;

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws
		Exception {
		return authenticationConfiguration.getAuthenticationManager();
	}

	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			.csrf(AbstractHttpConfigurer::disable);
		http
			.formLogin(AbstractHttpConfigurer::disable);
		http
			.httpBasic(AbstractHttpConfigurer::disable);

		http.authorizeHttpRequests(
			authorize -> authorize.anyRequest().permitAll()
		);

		http
			.addFilterAt(
					new CustomAuthenticationFilter(
							authenticationManager(authenticationConfiguration),
							objectMapper,
							tokenService,
							cookieUtil,
							redisRepository
					),
					UsernamePasswordAuthenticationFilter.class
			);

		http
			.addFilterBefore(
					new CustomLogoutFilter(jwtUtil, cookieUtil, redisRepository),
					LogoutFilter.class
			);

		http
			.sessionManagement(session -> session
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

		return http.build();
	}
}
