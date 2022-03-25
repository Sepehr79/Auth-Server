package com.sepehr.authentication_server.model.service;

import com.sepehr.authentication_server.bussiness.NumberGenerator;
import com.sepehr.authentication_server.model.entity.RedisUser;
import com.sepehr.authentication_server.model.io.UserIO;
import com.sepehr.authentication_server.model.repo.RedisUserRepo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class UserServiceTest {

    private static final String EMAIL = "sepehrmsm1379@gmail.com";

    @Autowired
    UserService userService;

    @Autowired
    RedisUserRepo redisUserRepo;

    @MockBean
    NumberGenerator numberGenerator;

    @AfterEach
    void clearData(){
        redisUserRepo.deleteAll();
    }

    @Test
    void saveUserTest(){
        Mockito.when(numberGenerator.generateUserVerifierCode())
                .thenReturn("1000");

        UserIO userIO = new UserIO();
        userIO.setEmail(EMAIL);
        userIO.setPassword("1234");
        userIO.setRole(List.of("ADMIN"));

        userService.temporarySave(userIO);

        Optional<RedisUser> redisUserOptional = redisUserRepo.findById(EMAIL);

        assertTrue(redisUserOptional.isPresent());
        assertEquals("1000" ,redisUserOptional.get().getToken());
        assertEquals("ADMIN", redisUserOptional.get().getRoles().get(0));
    }

}
