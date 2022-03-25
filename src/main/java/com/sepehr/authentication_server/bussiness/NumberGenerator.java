package com.sepehr.authentication_server.bussiness;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class NumberGenerator {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    public String generateUserVerifierCode(){
        return String.valueOf(1000 + SECURE_RANDOM.nextInt(9000));
    }

}
