package com.sepehr.authentication_server.model.repo;

import com.sepehr.authentication_server.model.entity.MongoUser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MongoRepoTest {

    private static final String EMAIL = "cess.kashanu@gmail.com";

    @Autowired
    MongoUserRepo mongoUserRepo;

    @Test
    void saveUser(){
        MongoUser mongoUser = MongoUser.builder()
                .email(EMAIL)
                .password("1234")
                .build();

        mongoUserRepo.save(mongoUser);

        assertTrue(mongoUserRepo.existsById(EMAIL));

        Optional<MongoUser> mongoUserOptional = mongoUserRepo.findById(EMAIL);
        assertEquals("1234", mongoUserOptional.get().getPassword());

        mongoUserRepo.deleteById(EMAIL);

        assertFalse(mongoUserRepo.existsById(EMAIL));

    }

}
