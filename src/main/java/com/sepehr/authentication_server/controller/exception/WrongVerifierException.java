package com.sepehr.authentication_server.controller.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class WrongVerifierException extends RuntimeException {
    private final String email;
    private final String verifier;
}
