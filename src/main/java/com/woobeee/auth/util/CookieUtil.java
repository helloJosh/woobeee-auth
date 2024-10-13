package com.woobeee.auth.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

/**
 * 쿠키 유틸리티 클래스
 *
 * @author 김병우
 */
@Component
public class CookieUtil {
    /**
     * 쿠키를 생성한다.
     *
     * @param key 쿠키 이름
     * @param value 쿠키 값
     * @return 해당 쿠키
     */
    public Cookie createCookie(String key, String value) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(24 * 60 * 60); // 24 시간
        cookie.setHttpOnly(true);
        cookie.setPath("/");

        return cookie;
    }

    /**
     * 쿠기 삭제.
     *
     * @param response 응답
     * @param cookieName 쿠키이릅
     */
    public void deleteCookie(HttpServletResponse response, String cookieName) {
        Cookie cookie = new Cookie(cookieName, "");
        cookie.setMaxAge(0); // 쿠키 만료 시간 0으로 설정하여 삭제
        cookie.setPath("/"); // 쿠키 경로 설정 (쿠키가 생성된 경로와 일치해야 삭제 가능)
        response.addCookie(cookie); // 클라이언트에 삭제된 쿠키 전송
    }

    /**
     * 쿠키에서 Refresh 토큰 가져오기.
     *
     * @param request 요청
     * @return 토큰
     */
    public String getRefreshTokenFromCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }

        for (Cookie cookie : cookies) {
            if ("Refresh".equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }

    /**
     * 쿠키에서 Access 토큰 가져오기.
     *
     * @param request 요청
     * @return 토큰
     */
    public String getAccessTokenFromCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }

        for (Cookie cookie : cookies) {
            if ("Access".equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }
}
