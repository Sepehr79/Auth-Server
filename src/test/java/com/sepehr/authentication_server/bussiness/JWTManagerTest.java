package com.sepehr.authentication_server.bussiness;


import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.sepehr.authentication_server.model.entity.MongoUser;
import com.sepehr.authentication_server.model.entity.RedisUser;
import com.sepehr.authentication_server.model.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JWTManagerTest {

    private static final JWTManager JWT_MANAGER = new JWTManager();
    private static final String EMAIL = "SAMPLE@gmail.com";
    private static final String SECRET = "sample";
    private static final Duration DURATION = Duration.ofSeconds(10);

    @Test
    void generateUserTokenTest(){
        ReflectionTestUtils.setField(JWT_MANAGER, "secret", SECRET);
        ReflectionTestUtils.setField(JWT_MANAGER, "duration", DURATION);
        JWT_MANAGER.configAlgorithm();

        User user = MongoUser.builder()
                .email(EMAIL)
                .password("1234")
                .authorities(List.of("READ"))
                .roles(List.of("ADMIN"))
                .build();

        String token = JWT_MANAGER.generateUserToken(user);
        DecodedJWT decode = JWT.decode(token);
        assertEquals(EMAIL, decode.getClaim("email").asString());
    }

}
