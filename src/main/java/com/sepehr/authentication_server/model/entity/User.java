package com.sepehr.authentication_server.model.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;

import java.util.List;


@NoArgsConstructor
@SuperBuilder(toBuilder = true)
@Setter
@Getter
public abstract class User {

    @Id
    private String email;

    private String password;

    private List<String> roles;

    private List<String> authorities;
}
