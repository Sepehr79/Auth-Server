package com.sepehr.authentication_server.model.repo;

import com.sepehr.authentication_server.model.entity.RedisUser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class RedisRepoTest {

    private static final String EMAIL = "sepehrmsm1379@gmail.com";

    @Autowired
    RedisUserRepo redisUserRepo;

    @Test
    void crudUserTest(){
        RedisUser redisUser = new RedisUser();
        redisUser.setEmail(EMAIL);
        redisUser.setPassword("1234");
        redisUser.setAuthorities(List.of("READ", "WRITE"));
        redisUser.setRoles(List.of("ADMIN", "MANAGER"));
        redisUser.setToken("1234");

        redisUserRepo.save(redisUser);

        assertTrue(redisUserRepo.existsById(EMAIL));

        redisUserRepo.deleteById(EMAIL);

        assertFalse(redisUserRepo.existsById(EMAIL));
    }

}
