package com.sepehr.authentication_server.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sepehr.authentication_server.bussiness.EmailVerifierSender;
import com.sepehr.authentication_server.controller.dto.ResponseDTO;
import com.sepehr.authentication_server.model.io.UserIO;
import com.sepehr.authentication_server.model.service.UserService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.util.Pair;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    private static final String SOURCE_EMAIL = "cess.kashanu@gmail.com";
    private static final String DISTANCE_EMAIL = "cess.kashanu@gmail.com";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private final static UserIO USER_IO = new UserIO();
    private final static Pair<String, String> EMAIL_TOKEN_RESULT = Pair.of(DISTANCE_EMAIL, "Token");

    @Value("${api.path}")
    String path;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    AuthController authController;

    @MockBean
    EmailVerifierSender emailVerifierSender;

    @MockBean
    UserService userService;


    @BeforeAll
    static void config(){
        USER_IO.setEmail(DISTANCE_EMAIL);
        USER_IO.setPassword("1234");
    }

    @Test
    void successfulTemporarySaveTest() throws Exception {
        Mockito.when(userService.temporarySave(USER_IO))
                .thenReturn(EMAIL_TOKEN_RESULT);
        Mockito.when(emailVerifierSender.sendVerifyEmail(EMAIL_TOKEN_RESULT))
                .thenReturn(Pair.of(SOURCE_EMAIL, DISTANCE_EMAIL));

        mockMvc.perform(post(path + "/users")
                .contentType("application/json")
                .content(OBJECT_MAPPER.writeValueAsString(USER_IO)))
                .andExpect(status().isOk())
                .andDo(result -> {
                    ResponseDTO responseDTO = OBJECT_MAPPER.readValue(result.getResponse().getContentAsString(), ResponseDTO.class);
                    Map<String, String> sourceDistance = responseDTO.getProperties();
                    assertEquals(sourceDistance.get("source"), SOURCE_EMAIL);
                    assertEquals(sourceDistance.get("destination"), DISTANCE_EMAIL);
                });
    }

    @Test
    void unsuccessfulTemporarySaveUserTest() throws Exception{
        Mockito.when(userService.temporarySave(USER_IO))
                .thenReturn(EMAIL_TOKEN_RESULT);
        Mockito.doThrow(new MailTransferException(SOURCE_EMAIL, DISTANCE_EMAIL, "Failed"))
                .when(emailVerifierSender).sendVerifyEmail(EMAIL_TOKEN_RESULT);

        mockMvc.perform(post(path + "/users")
                        .contentType("application/json")
                        .content(OBJECT_MAPPER.writeValueAsString(USER_IO)))
                .andExpect(status().isInternalServerError())
                .andDo(result -> {
                    ResponseDTO responseDTO = OBJECT_MAPPER.readValue(result.getResponse().getContentAsString(), ResponseDTO.class);
                    assertEquals("Failed to send email", responseDTO.getSubject());
                    Map<String, String> sourceDistance = responseDTO.getProperties();
                    assertEquals(sourceDistance.get("source"), SOURCE_EMAIL);
                    assertEquals(sourceDistance.get("destination"), DISTANCE_EMAIL);
                });
    }


}
