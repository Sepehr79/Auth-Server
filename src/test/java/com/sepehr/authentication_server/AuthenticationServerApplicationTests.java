package com.sepehr.authentication_server;

import com.sepehr.authentication_server.bussiness.EmailVerifierSender;
import com.sepehr.authentication_server.controller.dto.ResponseStateDTO;
import com.sepehr.authentication_server.model.io.UserIO;
import com.sepehr.authentication_server.model.repo.MongoUserRepo;
import com.sepehr.authentication_server.model.repo.RedisUserRepo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthenticationServerApplicationTests {

	private static final String EMAIL = "cess.kashanu@gmail.com";

	@LocalServerPort
	private int port;

	@Value("${api.path}")
	private String path;

	@Autowired
	TestRestTemplate testRestTemplate;

	@Autowired
	MongoUserRepo mongoUserRepo;

	@Autowired
	RedisUserRepo redisUserRepo;

	@MockBean
	EmailVerifierSender emailVerifierSender;

	@AfterEach
	void clear(){
		mongoUserRepo.deleteAll();
		redisUserRepo.deleteAll();
	}

	@Test
	void contextLoads() {
		final String url = String.format("http://localhost:%d", port) + path + "/users";
		final var user = new UserIO();
		user.setEmail(EMAIL);
		user.setPassword("1234");
		user.setRole(List.of("ADMIN", "MANAGER"));
		user.setAuthority(List.of("READ", "WRITE"));
		Mockito.when(emailVerifierSender.sendVerifyEmail(any()))
				.thenReturn(Pair.of(EMAIL, EMAIL));

		ResponseEntity<ResponseStateDTO> responseStateDTOResponseEntity = testRestTemplate.postForEntity(url, user, ResponseStateDTO.class);
		assertEquals(HttpStatus.OK, responseStateDTOResponseEntity.getStatusCode());
		assertEquals("User registration", Objects.requireNonNull(responseStateDTOResponseEntity.getBody()).getSubject());
	}

}
