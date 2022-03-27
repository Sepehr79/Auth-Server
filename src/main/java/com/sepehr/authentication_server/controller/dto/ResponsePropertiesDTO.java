package com.sepehr.authentication_server.controller.dto;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.Map;

@Builder
@Getter
public class ResponsePropertiesDTO {

    private String subject;
    private String message;
    private Map<String, String> properties;
    private HttpStatus httpStatus;

}
