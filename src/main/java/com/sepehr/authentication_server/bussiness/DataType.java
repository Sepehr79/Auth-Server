package com.sepehr.authentication_server.bussiness;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum DataType {
    TEMPORARY("Redis"),
    PERMANENT("Mongodb");

    private final String databaseName;
}
