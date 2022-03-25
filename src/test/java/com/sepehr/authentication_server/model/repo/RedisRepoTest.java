package com.sepehr.authentication_server.model.repo;

import com.sepehr.authentication_server.model.entity.RedisUser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

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

        Optional<RedisUser> redisUserOptional = redisUserRepo.findById(EMAIL);
        List<String> authorities = redisUserOptional.get().getAuthorities();
        List<String> roles = redisUserOptional.get().getRoles();
        assertEquals(2 ,authorities.size());
        assertEquals(2, roles.size());
        assertEquals("ADMIN" ,roles.get(0));

        redisUserRepo.deleteById(EMAIL);

        assertFalse(redisUserRepo.existsById(EMAIL));
    }

}
