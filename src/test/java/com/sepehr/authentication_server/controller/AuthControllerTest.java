package com.sepehr.authentication_server.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sepehr.authentication_server.bussiness.EmailVerifierSender;
import com.sepehr.authentication_server.bussiness.JWTManager;
import com.sepehr.authentication_server.controller.dto.ResponseStateDTO;
import com.sepehr.authentication_server.controller.exception.MailTransferException;
import com.sepehr.authentication_server.controller.exception.WrongPasswordException;
import com.sepehr.authentication_server.controller.exception.WrongVerifierException;
import com.sepehr.authentication_server.model.entity.MongoUser;
import com.sepehr.authentication_server.model.entity.RedisUser;
import com.sepehr.authentication_server.model.entity.User;
import com.sepehr.authentication_server.model.io.UserIO;
import com.sepehr.authentication_server.model.repo.MongoUserRepo;
import com.sepehr.authentication_server.model.repo.RedisUserRepo;
import com.sepehr.authentication_server.model.service.UserService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.util.Pair;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    private static final String SOURCE_EMAIL = "cess.kashanu@gmail.com";
    private static final String DISTANCE_EMAIL = "sepehrmsm1379@gmail.com";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final UserIO USER_IO = new UserIO();
    private static final Pair<String, String> EMAIL_TOKEN_RESULT = Pair.of(DISTANCE_EMAIL, "Token");

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

    @SpyBean
    RedisUserRepo redisUserRepo;

    @SpyBean
    MongoUserRepo mongoUserRepo;

    @MockBean
    JWTManager jwtManager;


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
                    ResponseStateDTO responseDTO = OBJECT_MAPPER.readValue(result.getResponse().getContentAsString(), ResponseStateDTO.class);
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
                    ResponseStateDTO responseDTO = OBJECT_MAPPER.readValue(result.getResponse().getContentAsString(), ResponseStateDTO.class);
                    assertEquals("Failed to send email", responseDTO.getSubject());
                    Map<String, String> sourceDistance = responseDTO.getProperties();
                    assertEquals(sourceDistance.get("source"), SOURCE_EMAIL);
                    assertEquals(sourceDistance.get("destination"), DISTANCE_EMAIL);
                });
    }

    @Test
    void saveUserTest() throws Exception {
        final String email = "email";
        final String token = "token";
        RedisUser redisUser = RedisUser.builder().email(email).token(token).build();
        Mockito.when(userService.saveByEmailAndVerifier(email, token))
                .thenReturn(redisUser);
        Mockito.when(jwtManager.generateUserToken(redisUser))
                .thenReturn("Token");

        MvcResult mvcResult = mockMvc.perform(post(path + "/users/" + email + "/" + token))
                .andExpect(status().isOk())
                .andReturn();
        var response = OBJECT_MAPPER.readValue(mvcResult.getResponse().getContentAsString(), ResponseStateDTO.class);
        assertEquals("Token" ,response.getProperties().get("token"));
    }

    @Test
    void saveByWrongVerifier() throws Exception {
        final String email = "email";
        final String token = "token";
        Mockito.doThrow(new WrongVerifierException(email, token))
                .when(userService).saveByEmailAndVerifier(email, token);

        MvcResult mvcResult = mockMvc.perform(post(path + "/users/" + email + "/" + token))
                .andExpect(status().isBadRequest())
                .andReturn();
        var stateDto = OBJECT_MAPPER.readValue(mvcResult.getResponse().getContentAsString(), ResponseStateDTO.class);
        assertEquals("Wrong verifier with the given email", stateDto.getMessage());
        assertEquals(email, stateDto.getProperties().get("email"));
        assertEquals(token, stateDto.getProperties().get("verifier"));
    }

    @Test
    void verifyWithWrongPasswordTest() throws Exception {
        final String email = "email";
        final String password = "password";

        Mockito.doThrow(new WrongPasswordException(email, password))
                .when(userService).verifyByEmailAndPassword(email, password);

        MvcResult mvcResult = mockMvc.perform(get(path + "/users/" + email + "/" + password))
                .andExpect(status().isBadRequest())
                .andReturn();
        var response = OBJECT_MAPPER.readValue(mvcResult.getResponse().getContentAsString(), ResponseStateDTO.class);
        assertEquals(email, response.getProperties().get(email));
    }

    @Test
    void verifyUserTest() throws Exception {
        final User user = MongoUser.builder().email(DISTANCE_EMAIL).roles(List.of("ADMIN", "MANAGER")).build();
        Mockito.when(userService.verifyByEmailAndPassword(DISTANCE_EMAIL, "1234"))
                .thenReturn(user);
        Mockito.when(jwtManager.generateUserToken(user))
                .thenReturn("Token");

        MvcResult mvcResult = mockMvc.perform(get(path + "/users/" + DISTANCE_EMAIL + "/1234"))
                .andExpect(status().isOk())
                .andReturn();
        ResponseStateDTO responseStateDTO = OBJECT_MAPPER.readValue(mvcResult.getResponse().getContentAsString(), ResponseStateDTO.class);
        assertEquals("Token" ,responseStateDTO.getProperties().get("token"));
    }

    @Test
    void changePassword() throws Exception {
        final String token = "token";
        final String newPassword = "new";
        Mockito.doNothing().when(userService).changePasswordByEmailAndVerifier((DISTANCE_EMAIL), (token), (newPassword));
        MvcResult mvcResult = mockMvc.perform(post(path + "/users/" + DISTANCE_EMAIL + "/" + token + "/" + newPassword))
                .andExpect(status().isOk())
                .andReturn();
        var state = OBJECT_MAPPER.readValue(mvcResult.getResponse().getContentAsString(), ResponseStateDTO.class);
        assertEquals(DISTANCE_EMAIL, state.getProperties().get("email"));
    }

    @Test
    void deleteUserTest() throws Exception {
        Mockito.doNothing().when(userService).deleteUserByEmail(DISTANCE_EMAIL);
        MvcResult mvcResult = mockMvc.perform(delete(path + "/users/" + DISTANCE_EMAIL))
                .andExpect(status().isOk())
                .andReturn();
         ResponseStateDTO dto = OBJECT_MAPPER.readValue(mvcResult.getResponse().getContentAsString(), ResponseStateDTO.class);
         assertEquals("User deletion" ,dto.getSubject());
    }


}
