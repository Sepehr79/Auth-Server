package com.sepehr.authentication_server.controller;

import com.sepehr.authentication_server.bussiness.EmailVerifierSender;
import com.sepehr.authentication_server.controller.dto.ResponseDTO;
import com.sepehr.authentication_server.model.io.UserIO;
import com.sepehr.authentication_server.model.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("${api.path}")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    private final EmailVerifierSender emailVerifierSender;

    @PostMapping("/users")
    public ResponseDTO temporarySaveUser(@RequestBody UserIO userIO){
        Pair<String, String> emailTokenResult = userService.temporarySave(userIO);
        Pair<String, String> sourceDestination = emailVerifierSender.sendVerifyEmail(emailTokenResult);
        return new ResponseDTO(
                "Successful",
                "Verifier email sent",
                Map.of("Source", sourceDestination.getFirst(),
                        "destination", sourceDestination.getSecond())
        );
    }

}
