package com.sepehr.authentication_server.controller.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class MailTransferException extends RuntimeException{
    private final String source;
    private final String destination;
    private final String message;
}
