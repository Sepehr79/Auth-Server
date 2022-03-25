package com.sepehr.authentication_server.configuration;

import com.sepehr.authentication_server.model.entity.RedisUser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;

@Configuration
public class RedisConfiguration {

    @Bean
    public JedisConnectionFactory jedisConnectionFactory(){
        return new JedisConnectionFactory();
    }

    @Bean
    public RedisTemplate<String, RedisUser> redisTemplate(JedisConnectionFactory jedisConnectionFactory){
        var template = new RedisTemplate<String, RedisUser>();
        template.setConnectionFactory(jedisConnectionFactory);
        template.setKeySerializer(new Jackson2JsonRedisSerializer<>(RedisUser.class));
        return template;
    }

}
