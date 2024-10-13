package com.woobeee.auth.repository;

import com.woobeee.auth.util.TokenDetails;

/**
 * 레디스 저장소.
 *
 * @author 김병우
 */
public interface RedisRepository {

    /**
     * 레디스 저장.
     *
     * @param key 레디스 키 값
     * @param val 레디스 저장할 데이터
     * @param uuid 고유번호
     * @param time 만료시간
     */
    void save(String key, String val, String uuid, Long time);

    /**
     * Redis 에서 uuid 로 TokenDetails 객체를 가져온다.
     *
     * @param uuid the uuid
     * @return the token details
     */
    TokenDetails getTokenDetails(String uuid);

    /**
     * Redis 에서 uuid 로 저장된 객체 삭제한다.
     *
     * @param uuid the uuid
     */
    void deleteTokenDetail(String uuid);

    /**
     * Redis 에서 uuid 로 RefreshToken 을 가져온다.
     *
     * @param uuid the uuid
     * @return the refresh token
     */
    String getRefreshToken(String uuid);

    /**
     * Redis 에서 uuid 로 RefreshToken 을 삭제한다.
     *
     * @param uuid the uuid
     */
    void deleteRefreshToken(String uuid);

    /**
     * Redis 에서 uuid, refresh token 가 저장되어 있는지 확인한다.
     *
     * @param uuid the uuid
     * @param inputRefresh refresh token
     * @return 저장 여부
     */
    boolean existsRefreshToken(String uuid, String inputRefresh);
}
