package com.sepehr.authentication_server.controller.exception;

import lombok.Getter;

@Getter
public class WrongPasswordException extends UserNotFoundException {
    private final String password;

    public WrongPasswordException(String email, String password) {
        super(email);
        this.password = password;
    }
}
