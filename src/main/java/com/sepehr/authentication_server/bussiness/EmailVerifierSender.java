package com.sepehr.authentication_server.bussiness;

import com.sepehr.authentication_server.controller.MailTransferException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmailVerifierSender {

    @Value("${spring.mail.username}")
    private String source;

    private final JavaMailSender mailSender;

    public Pair<String, String> sendVerifyEmail(Pair<String, String> temporaryResult){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(source);
        message.setSubject("Authentication");
        message.setTo(temporaryResult.getFirst());
        message.setText("Your auth code: " + temporaryResult.getSecond());
        try {
            mailSender.send(message);
        }catch (MailException mailException){
            throw new MailTransferException(source, temporaryResult.getFirst(), mailException.getMessage());
        }
        return Pair.of(source, temporaryResult.getFirst());
    }
}
