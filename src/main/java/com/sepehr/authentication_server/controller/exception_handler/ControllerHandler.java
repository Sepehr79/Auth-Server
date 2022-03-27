package com.sepehr.authentication_server.controller.exception_handler;

import com.sepehr.authentication_server.controller.dto.ResponsePropertiesDTO;
import com.sepehr.authentication_server.controller.exception.MailTransferException;
import com.sepehr.authentication_server.controller.dto.ResponseStateDTO;
import com.sepehr.authentication_server.controller.exception.UserNotFoundException;
import com.sepehr.authentication_server.controller.exception.WrongPasswordException;
import com.sepehr.authentication_server.controller.exception.WrongVerifierException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;

@ControllerAdvice
public class ControllerHandler {

    @ExceptionHandler(MailTransferException.class)
    public ResponseEntity<ResponseStateDTO> mailException(MailTransferException mailTransferException){
        return getResponse(
                ResponsePropertiesDTO.builder()
                        .subject("Failed to send email")
                        .message(mailTransferException.getMessage())
                        .properties(Map.of("source", mailTransferException.getSource(),
                                "destination", mailTransferException.getDestination()))
                        .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                        .build()
        );
    }

    @ExceptionHandler({UserNotFoundException.class, WrongPasswordException.class})
    public ResponseEntity<ResponseStateDTO> userNotFoundException(UserNotFoundException userNotFoundException){
        return getResponse(
                ResponsePropertiesDTO.builder()
                        .subject("User details")
                        .message("Invalid email or password")
                        .properties(Map.of("email", userNotFoundException.getEmail()))
                        .httpStatus(HttpStatus.BAD_REQUEST)
                        .build()
        );
    }

    @ExceptionHandler(WrongVerifierException.class)
    public ResponseEntity<ResponseStateDTO> wrongUserVerifierException(WrongVerifierException wrongVerifierException){
        return getResponse(
                ResponsePropertiesDTO.builder()
                        .subject("Invalid verifier")
                        .message("Wrong verifier with the given email")
                        .properties(Map.of("email", wrongVerifierException.getEmail(), "verifier", wrongVerifierException.getVerifier()))
                        .httpStatus(HttpStatus.BAD_REQUEST)
                        .build()
        );
    }


    private ResponseEntity<ResponseStateDTO> getResponse(ResponsePropertiesDTO responsePropertiesDTO){
        var responseDTO = ResponseStateDTO.builder()
                .subject(responsePropertiesDTO.getSubject())
                .message(responsePropertiesDTO.getMessage())
                .properties(responsePropertiesDTO.getProperties())
                .build();

        return new ResponseEntity<>(responseDTO, responsePropertiesDTO.getHttpStatus());
    }

}
