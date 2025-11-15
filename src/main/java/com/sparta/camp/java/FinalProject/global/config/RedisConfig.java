package com.sparta.camp.java.FinalProject.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;
import redis.clients.jedis.Jedis;

@Configuration
//@EnableRedisHttpSession(maxInactiveIntervalInSeconds = 5)
public class RedisConfig {

    @Value("${spring.data.redis.host}")
    private String redisHost;

    @Value("${spring.data.redis.port}")
    private int redisPort;

    @Value("${spring.data.redis.password:}")
    private String redisPassword;

    @Bean
    public Jedis jedis() {
        Jedis jedis = new Jedis(redisHost, redisPort);

        if (redisPassword != null && !redisPassword.isEmpty()) {
            jedis.auth(redisPassword); // 비밀번호가 있다면 인증
        }

        return jedis;
    }

    @Bean
    public CookieSerializer cookieSerializer() {
        DefaultCookieSerializer cookieSerializer = new DefaultCookieSerializer();
//        cookieSerializer.setUseSecureCookie(true);
        cookieSerializer.setUseHttpOnlyCookie(true);
        cookieSerializer.setSameSite("Strict");
        return cookieSerializer;
    }
}