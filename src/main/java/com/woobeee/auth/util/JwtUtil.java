package com.woobeee.auth.util;

import com.woobeee.auth.exception.TokenTypeNotAllowedException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;

/**
 * JWT Utility Class
 *
 * @author 김병우
 */
@Slf4j
@Component
public class JwtUtil {
    private final SecretKey secretKey = Keys.hmacShaKeyFor("aaaasqwlkljlfasdfqwerafcxvzpojijoijopjewndllllllsserrrrrrwqexc".getBytes());
    private final long ACCESS_TOKEN_EXPIRE_TIME = 1000 * 60 * 30; //30분
    private final long REFRESH_TOKEN_EXPIRE_TIME = 1000 * 60 * 60 * 24; //하루

    /**
     * Token 생성.
     *
     * @param type Access / Refresh
     * @param username 로그인아이디 / 유저이름
     * @param memberId 회원 고유 번호
     * @param auth 회원 권한
     * @return 토큰 값
     */
    public String generateToken(String type, String username, Long memberId, List<String> auth, String uuid) {
        if(type.equals("Refresh")) {
            return Jwts.builder()
                    .claim("type", type)
                    .claim("username", username)
                    .claim("memberId", memberId)
                    .claim("auth", auth)
                    .claim("uuid", uuid)
                    .issuedAt(new Date(System.currentTimeMillis()))
                    .expiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRE_TIME))
                    .signWith(secretKey)
                    .compact();
        } else if(type.equals("Access")) {
            return Jwts.builder()
                    .claim("type", type)
                    .claim("username", username)
                    .claim("memberId", memberId)
                    .claim("auth", auth)
                    .claim("uuid", uuid)
                    .issuedAt(new Date(System.currentTimeMillis()))
                    .expiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRE_TIME))
                    .signWith(secretKey)
                    .compact();
        } else {
            throw new TokenTypeNotAllowedException("토큰 타입은 Access, Refresh 둘중 하나여야합니다.");
        }
    }

    /**
     * JWT 유효 기간(만료 기간) 체크한다.
     *
     * @param token access, refresh token
     * @return 유효성
     */
    public Boolean isExpired(String token) {

        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getExpiration()
                .before(new Date());
    }

    /**
     * 토큰 타입 반환
     *
     * @param token 토큰 값
     * @return 토큰 타입
     */
    public String getTokenType(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("category", String.class);
    }

    /**
     * 유저 아이디 반환
     *
     * @param token 토큰 값
     * @return 유저 아이디
     */
    public String getUsername(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("username", String.class);
    }

    /**
     * 맴버 고유 번호 반환
     *
     * @param token 토큰 값
     * @return 맴버 고유 번호
     */
    public Long getMemberId(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("memberId", Long.class);
    }

    /**
     * 유저 권한 반환
     *
     * @param token 토큰 값
     * @return 유저 권한
     */
    public List<String> getAuth(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("auth", List.class);
    }

    /**
     * JWT 에서 멤버의 uuid 를 가져온다.
     *
     * @param token 토큰
     * @return the uuid
     */
    public String getUuid(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("uuid", String.class);
    }

}
