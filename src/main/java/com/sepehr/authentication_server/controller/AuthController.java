package com.sepehr.authentication_server.controller;

import com.sepehr.authentication_server.bussiness.EmailVerifierSender;
import com.sepehr.authentication_server.bussiness.JWTManager;
import com.sepehr.authentication_server.controller.dto.ResponseStateDTO;
import com.sepehr.authentication_server.model.entity.User;
import com.sepehr.authentication_server.model.io.UserIO;
import com.sepehr.authentication_server.model.service.UserService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("${api.path}")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    private final EmailVerifierSender emailVerifierSender;

    private final JWTManager jwtManager;

    @PostMapping("/users")
    public ResponseStateDTO temporarySaveUser(@RequestBody UserIO userIO){
        Pair<String, String> emailTokenResult = userService.temporarySave(userIO);
        Pair<String, String> sourceDestination = emailVerifierSender.sendVerifyEmail(emailTokenResult);
        return new ResponseStateDTO(
                "User registration",
                "Verifier email sent to the user",
                Map.of("source", sourceDestination.getFirst(),
                        "destination", sourceDestination.getSecond())
        );
    }

    @PostMapping("/users/{email}/{verifier}")
    public ResponseStateDTO saveUser(@PathVariable String email, @PathVariable String verifier){
        User user = userService.saveByEmailAndVerifier(email, verifier);
        return new ResponseStateDTO(
            "User registration",
            "User saved with the given email",
                Map.of("email", email,
                        "token", jwtManager.generateUserToken(user))
        );
    }

    @GetMapping("/users/{email}/{password}")
    public ResponseStateDTO verifyUser(@PathVariable String email, @PathVariable String password){
        User user = userService.verifyByEmailAndPassword(email, password);
        return new ResponseStateDTO(
            "User verification",
            "User successfully verified with the given email and password",
                Map.of("email", email,
                        "token", jwtManager.generateUserToken(user))
        );
    }
}
