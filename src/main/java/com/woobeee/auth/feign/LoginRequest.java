package com.woobeee.auth.feign;

import com.woobeee.auth.api.Response;
import com.woobeee.auth.dto.request.MemberAuthRequest;
import com.woobeee.auth.dto.response.MemberAuthResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * OpenFeign 사용한 로그인 어댑터
 */
@FeignClient(url = "${feign.client.url}/api/v1/blog", name = "LoginRequest")
@Component
public interface LoginRequest {
	/**
	 * Member login api response.
	 *
	 * @param memberAuthRequest the member auth request
	 * @return the api response
	 */
	@PostMapping("/login")
	Response<MemberAuthResponse> memberLogin(@RequestBody MemberAuthRequest memberAuthRequest);
}
