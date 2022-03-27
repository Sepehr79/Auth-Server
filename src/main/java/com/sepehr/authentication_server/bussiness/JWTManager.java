package com.sepehr.authentication_server.bussiness;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.sepehr.authentication_server.model.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.util.Date;

@Component
public class JWTManager {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.duration}")
    private Duration duration;

    private Algorithm algorithm;

    @PostConstruct
    void configAlgorithm(){
        algorithm = Algorithm.HMAC256(secret);
    }

    public String generateUserToken(User user){
        return JWT.create()
                .withClaim("email", user.getEmail())
                .withClaim("authorities", user.getAuthorities())
                .withClaim("roles", user.getRoles())
                .withExpiresAt(new Date(System.currentTimeMillis() + duration.toMillis()))
                .sign(algorithm);
    }
}
