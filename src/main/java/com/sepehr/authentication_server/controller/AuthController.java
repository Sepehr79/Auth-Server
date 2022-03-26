package com.sepehr.authentication_server.controller;

import com.sepehr.authentication_server.model.io.UserIO;
import com.sepehr.authentication_server.model.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${api.path}")
@RequiredArgsConstructor
public class AuthController {

    @Value("${spring.mail.username}")
    private String source;

    private final UserService userService;

    private final JavaMailSender javaMailSender;

    @PostMapping("/users")
    public void temporarySaveUser(@RequestBody UserIO userIO){
        Pair<String, String> temporarySavingResult = userService.temporarySave(userIO);
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(source);
        mailMessage.setSubject("Authentication");
        mailMessage.setTo(temporarySavingResult.getFirst());
        mailMessage.setText("Your auth code: " + temporarySavingResult.getSecond());
        javaMailSender.send(mailMessage);
    }

}
