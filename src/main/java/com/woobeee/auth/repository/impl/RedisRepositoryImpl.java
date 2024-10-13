package com.woobeee.auth.repository.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.woobeee.auth.repository.RedisRepository;
import com.woobeee.auth.util.TokenDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@Repository
public class RedisRepositoryImpl implements RedisRepository {
    private final String TOKEN_DETAILS = "TOKEN_DETAILS";
    private final String REFRESH_TOKEN = "REFRESH_TOKEN";
    private final String ACCESS_TOKEN = "ACCESS_TOKEN";

    private final ObjectMapper objectMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public void save(String key, String val, String uuid, Long time) {
        redisTemplate.opsForHash().put(TOKEN_DETAILS, uuid, val);
        redisTemplate.expire(TOKEN_DETAILS, time, TimeUnit.HOURS);
    }

    @Override
    public TokenDetails getTokenDetails(String uuid) {
        String data = (String)redisTemplate.opsForHash().get(TOKEN_DETAILS, uuid);
        try {
            return objectMapper.readValue(data, TokenDetails.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteTokenDetail(String uuid) {
        redisTemplate.opsForHash().delete(TOKEN_DETAILS, uuid);
    }

    @Override
    public String getRefreshToken(String uuid) {
        return (String)redisTemplate.opsForHash().get(REFRESH_TOKEN, uuid);
    }

    @Override
    public void deleteRefreshToken(String uuid) {
        redisTemplate.opsForHash().delete(REFRESH_TOKEN, uuid);
    }

    @Override
    public boolean existsRefreshToken(String uuid, String inputRefresh) {
        String storedRefresh = getRefreshToken(uuid);
        return Objects.nonNull(storedRefresh) && inputRefresh.equals(storedRefresh);
    }
}
