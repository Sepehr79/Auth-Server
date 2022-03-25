package com.sepehr.authentication_server.bussiness;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
class NumberGeneratorTest {

    private static final NumberGenerator NUMBER_GENERATOR = new NumberGenerator();

    AtomicReference<String> verifierContainer = new AtomicReference<>();

    @Test
    void numberGeneratorTest(){

        IntStream.rangeClosed(0, 10000)
                .forEach(i -> {
                    verifierContainer.set(NUMBER_GENERATOR.generateUserVerifierCode());
                    log.info("Generated code: {}", verifierContainer.get());
                    assertEquals(4 ,verifierContainer.get().length());
                });
    }

}
