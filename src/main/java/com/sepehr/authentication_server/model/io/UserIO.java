package com.sepehr.authentication_server.model.io;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class UserIO {

    private String email;

    private String password;

    private List<String> role;

    private List<String> authority;

}
