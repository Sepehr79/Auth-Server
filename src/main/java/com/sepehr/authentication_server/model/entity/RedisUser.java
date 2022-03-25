package com.sepehr.authentication_server.model.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.util.List;

@RedisHash(timeToLive = 60)
@Data
public class RedisUser {

    @Id
    private String email;

    private String password;

    private List<String> roles;

    private List<String> authorities;

    private String token;

}
