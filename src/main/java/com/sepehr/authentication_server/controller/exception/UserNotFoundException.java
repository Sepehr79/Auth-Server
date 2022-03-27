package com.sepehr.authentication_server.controller.exception;

import com.sepehr.authentication_server.bussiness.DataType;
import lombok.Getter;

@Getter
public class UserNotFoundException extends RuntimeException {

    private final String email;
    private final DataType dataType;

    public UserNotFoundException(String email){
        this.email = email;
        dataType = null;
    }

    public UserNotFoundException(String email, DataType databaseType){
        this.email = email;
        this.dataType = databaseType;
    }
}
