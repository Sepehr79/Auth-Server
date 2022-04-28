package com.sepehr.authentication_server.controller;

import com.sepehr.authentication_server.bussiness.EmailVerifierSender;
import com.sepehr.authentication_server.bussiness.JWTManager;
import com.sepehr.authentication_server.controller.dto.ResponseStateDTO;
import com.sepehr.authentication_server.controller.exception.MailTransferException;
import com.sepehr.authentication_server.model.entity.User;
import com.sepehr.authentication_server.model.io.UserIO;
import com.sepehr.authentication_server.model.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
@RequestMapping("${api.path}")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final UserService userService;

    private final EmailVerifierSender emailVerifierSender;

    private final JWTManager jwtManager;

    @PostMapping("/users")
    @Operation(summary = "New user registration in cache",
            description = "This path is used when we want to register a new user or change the user password")
    public ResponseEntity<ResponseStateDTO> temporarySaveUser(@RequestBody UserIO userIO){
        Pair<String, String> emailTokenResult = userService.temporarySave(userIO);
        ExecutorService executorService = Executors.newCachedThreadPool();
        executorService.submit(() -> {
            try {
                Pair<String, String> sourceDestination = emailVerifierSender.sendVerifyEmail(emailTokenResult);
                log.info("Processed email source: {}, destination: {}", sourceDestination.getFirst(), sourceDestination.getSecond());
            }catch (MailTransferException mailTransferException){
                log.info("Problem with sending email source: {}, destination: {}, message: {}" ,
                        mailTransferException.getSource(), mailTransferException.getDestination(), mailTransferException.getMessage());
            }
        });
        return new ResponseEntity<>(
                ResponseStateDTO.builder().message("ACCEPTED").build() , HttpStatus.ACCEPTED
        );
    }

    @PostMapping("/users/{email}/{verifier}")
    @Operation(summary = "New user registration in permanent memory")
    public ResponseStateDTO saveUser(@PathVariable String email, @PathVariable String verifier){
        User user = userService.saveByEmailAndVerifier(email, verifier);
        return new ResponseStateDTO(
            "User registration",
            "User saved with the given email",
                Map.of("email", email,
                        "token", jwtManager.generateUserToken(user))
        );
    }

    @PostMapping("/users/{email}/{verifier}/{newPassword}")
    @Operation(summary = "Changing user password")
    public ResponseStateDTO changePassword(@PathVariable String email,
                                           @PathVariable String verifier,
                                           @PathVariable String newPassword){
        userService.changePasswordByEmailAndVerifier(email, verifier, newPassword);
        return new ResponseStateDTO(
            "Password modification",
            "User password successfully changed",
            Map.of("email", email)
        );
    }

    @GetMapping("/users/{email}/{password}")
    @Operation(summary = "Verify user and get token")
    public ResponseStateDTO verifyUser(@PathVariable String email, @PathVariable String password){
        User user = userService.verifyByEmailAndPassword(email, password);
        return new ResponseStateDTO(
            "User verification",
            "User successfully verified with the given email and password",
                Map.of("email", email,
                        "token", jwtManager.generateUserToken(user))
        );
    }

    @DeleteMapping("/users/{email}")
    @Operation(summary = "Delete user from permanent memory")
    public ResponseStateDTO deleteUser(@PathVariable String email){
        userService.deleteUserByEmail(email);
        return new ResponseStateDTO(
            "User deletion",
            "User successfully deleted by the given email",
            Map.of("email", email)
        );
    }

}
