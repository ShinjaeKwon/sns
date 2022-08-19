package com.sjk.sns.cotroller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sjk.sns.controller.request.UserJoinRequest;
import com.sjk.sns.controller.request.UserLoginRequest;
import com.sjk.sns.exception.ErrorCode;
import com.sjk.sns.exception.SnsApplicationException;
import com.sjk.sns.model.User;
import com.sjk.sns.service.UserService;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private UserService userService;

	@DisplayName("회원가입")
	@Test
	void given_UserInfo_when_join_then_ReturnOk() throws Exception {
		//given
		String userName = "userName";
		String password = "password";

		//when
		when(userService.join(userName, password)).thenReturn(mock(User.class));

		//then
		mockMvc.perform(post("/api/v1/users/join")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsBytes(new UserJoinRequest(userName, password)))
			).andDo(print())
			.andExpect(status().isOk());
	}

	@DisplayName("회원가입시, 이미 회원가입된 userName으로 회원가입을 하는 경우 에러 반환")
	@Test
	void given_OverlapUserInfo_when_join_then_ReturnException() throws Exception {
		//given
		String userName = "userName";
		String password = "password";

		//when
		when(userService.join(userName, password)).thenThrow(
			new SnsApplicationException(ErrorCode.DUPLICATED_USER_NAME, ""));

		//then
		mockMvc.perform(post("/api/v1/users/join")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsBytes(new UserJoinRequest(userName, password)))
			).andDo(print())
			.andExpect(status().isConflict());
	}

	@DisplayName("로그인")
	@Test
	void given_UserInfo_when_login_then_ReturnOkAndToken() throws Exception {
		//given
		String userName = "userName";
		String password = "password";

		//when
		when(userService.login(userName, password)).thenReturn("test_token");

		//then
		mockMvc.perform(post("/api/v1/users/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsBytes(new UserLoginRequest(userName, password)))
			).andDo(print())
			.andExpect(status().isOk());
	}

	@DisplayName("회원가입이 안된 username으로 로그인시 에러반환")
	@Test
	void given_NonexistentUsername_when_login_then_ReturnException() throws Exception {
		//given
		String userName = "userName";
		String password = "password";

		//when
		when(userService.login(userName, password)).thenThrow(new SnsApplicationException(ErrorCode.USER_NOT_FOUND));

		//then
		mockMvc.perform(post("/api/v1/users/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsBytes(new UserLoginRequest(userName, password)))
			).andDo(print())
			.andExpect(status().isNotFound());
	}

	@DisplayName("틀린 패스워드로 로그인시 에러반환")
	@Test
	void given_WrongPassword_when_login_then_ReturnException() throws Exception {
		//given
		String userName = "userName";
		String password = "password";

		//when
		when(userService.login(userName, password)).thenThrow(new SnsApplicationException(ErrorCode.INVALID_PASSWORD));

		//then
		mockMvc.perform(post("/api/v1/users/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsBytes(new UserLoginRequest(userName, password)))
			).andDo(print())
			.andExpect(status().isUnauthorized());
	}

}
