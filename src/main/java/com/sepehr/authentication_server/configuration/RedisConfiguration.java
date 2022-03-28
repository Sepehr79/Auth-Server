package com.sepehr.authentication_server.configuration;

import com.sepehr.authentication_server.model.entity.RedisUser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;

@Configuration
public class RedisConfiguration {

    @Value("${redis.host}")
    private String host;

    @Value("${redis.port}")
    private int port;

    @Bean
    public JedisConnectionFactory jedisConnectionFactory(){
        var redisConfiguration = new RedisStandaloneConfiguration(host, port);
        return new JedisConnectionFactory(redisConfiguration);
    }

    @Bean
    public RedisTemplate<String, RedisUser> redisTemplate(JedisConnectionFactory jedisConnectionFactory){
        var template = new RedisTemplate<String, RedisUser>();
        template.setConnectionFactory(jedisConnectionFactory);
        template.setKeySerializer(new Jackson2JsonRedisSerializer<>(RedisUser.class));
        return template;
    }

}
