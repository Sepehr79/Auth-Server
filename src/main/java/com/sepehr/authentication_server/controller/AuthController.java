package com.sepehr.authentication_server.controller;

import com.sepehr.authentication_server.model.io.UserIO;
import com.sepehr.authentication_server.model.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${api.path}")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/users")
    public void temporarySaveUser(@RequestBody UserIO userIO){
        Pair<String, String> temporarySavingResult = userService.temporarySave(userIO);
        // TODO send email
    }

}
