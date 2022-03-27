package com.sepehr.authentication_server.model.service;

import com.sepehr.authentication_server.bussiness.NumberGenerator;
import com.sepehr.authentication_server.controller.exception.UserNotFoundException;
import com.sepehr.authentication_server.controller.exception.WrongVerifierException;
import com.sepehr.authentication_server.model.entity.RedisUser;
import com.sepehr.authentication_server.model.io.UserIO;
import com.sepehr.authentication_server.model.repo.MongoUserRepo;
import com.sepehr.authentication_server.model.repo.RedisUserRepo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserServiceTest {

    private static final String EMAIL = "sepehrmsm1379@gmail.com";

    @Autowired
    UserService userService;

    @SpyBean
    RedisUserRepo redisUserRepo;

    @Autowired
    MongoUserRepo mongoUserRepo;

    @MockBean
    NumberGenerator numberGenerator;

    @AfterEach
    void clearData(){
        redisUserRepo.deleteAll();
        mongoUserRepo.deleteAll();
    }

    @Test
    void temporarySaveUserTest(){
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

    @Test
    void saveUserTest(){
        final String verifierCode = "2131";
        Mockito.when(numberGenerator.generateUserVerifierCode())
                .thenReturn(verifierCode);
        UserIO userIO = new UserIO();
        userIO.setEmail(EMAIL);
        userIO.setPassword("1234");
        userIO.setRole(List.of("ADMIN"));

        userService.temporarySave(userIO);
        userService.saveByEmailAndVerifier(EMAIL, verifierCode);

        assertTrue(mongoUserRepo.existsById(EMAIL));
    }

    @Test
    void saveUserWithWrongEmail(){
        try {
            userService.saveByEmailAndVerifier("Wrong", "any");
            fail();
        }catch (Exception exception){
            assertTrue(exception instanceof UserNotFoundException);
            assertEquals("Wrong" ,((UserNotFoundException) exception).getEmail());
        }
    }

    @Test
    void saveUserWithWrongVerifier(){
        RedisUser redisUser = new RedisUser();
        redisUser.setEmail(EMAIL);
        redisUser.setPassword("1234");
        redisUser.setToken("Sample");

        Mockito.when(redisUserRepo.findById(EMAIL)).thenReturn(Optional.of(redisUser));
        ReflectionTestUtils.setField(userService, "redisUserRepo", redisUserRepo);

        try {
            userService.saveByEmailAndVerifier(EMAIL, "wrong"); // Wrong verifier
            fail();
        }catch (WrongVerifierException wrongVerifierException){
            assertEquals(EMAIL ,wrongVerifierException.getEmail());
            assertEquals("wrong", wrongVerifierException.getVerifier());
        }

        Mockito.reset(redisUserRepo);
    }


}
