package com.sepehr.authentication_server.controller.exception_handler;

import com.sepehr.authentication_server.controller.exception.MailTransferException;
import com.sepehr.authentication_server.controller.dto.ResponseStateDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;

@ControllerAdvice
public class ControllerHandler {

    @ExceptionHandler(MailTransferException.class)
    public ResponseEntity<ResponseStateDTO> mailException(MailTransferException mailTransferException){
        var responseDTO = ResponseStateDTO.builder()
                .subject("Failed to send email")
                .message(mailTransferException.getMessage())
                .properties(Map.of(
                        "source", mailTransferException.getSource(),
                        "destination", mailTransferException.getDestination()
                )).build();

        return new ResponseEntity<>(responseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
