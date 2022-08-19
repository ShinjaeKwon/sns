package com.sjk.sns.service;

import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.sjk.sns.exception.ErrorCode;
import com.sjk.sns.exception.SnsApplicationException;
import com.sjk.sns.fixture.UserEntityFixture;
import com.sjk.sns.model.entity.UserEntity;
import com.sjk.sns.repository.UserEntityRepository;

@SpringBootTest
public class UserServiceTest {

	@Autowired
	private UserService userService;

	@MockBean
	private UserEntityRepository userEntityRepository;

	@MockBean
	private BCryptPasswordEncoder encoder;

	@DisplayName("회원가입이 정상적으로 동작하는 경우")
	@Test
	void given_UserInfo_when_Join_then_NotThrow() {
		//given
		String userName = "userName";
		String password = "password";

		//when
		//mocking
		when(userEntityRepository.findByUsername(userName)).thenReturn(Optional.empty());
		when(encoder.encode(password)).thenReturn("encrypt_password");
		when(userEntityRepository.save(any())).thenReturn(UserEntityFixture.get(userName, password, 1));

		//then
		Assertions.assertDoesNotThrow(() -> userService.join(userName, password));
	}

	@DisplayName("회원가입시 username이 이미 존재하는 경우")
	@Test
	void given_AlreadyUsername_when_Join_then_Throw() {
		//given
		String userName = "userName";
		String password = "password";
		UserEntity fixture = UserEntityFixture.get(userName, password, 1);

		//when
		//mocking
		when(userEntityRepository.findByUsername(userName)).thenReturn(Optional.of(mock(UserEntity.class)));
		when(encoder.encode(password)).thenReturn("encrypt_password");
		when(userEntityRepository.save(any())).thenReturn(Optional.of(fixture));

		//then
		SnsApplicationException e = Assertions.assertThrows(SnsApplicationException.class,
			() -> userService.join(userName, password));
		Assertions.assertEquals(ErrorCode.DUPLICATED_USER_NAME, e.getErrorCode());
	}

	@DisplayName("로그인이 정상적으로 동작하는 경우")
	@Test
	void given_UserInfo_when_Login_then_NotThrow() {
		//given
		String userName = "userName";
		String password = "password";
		UserEntity fixture = UserEntityFixture.get(userName, password, 1);

		//when
		//mocking
		when(userEntityRepository.findByUsername(userName)).thenReturn(Optional.of(fixture));
		when(encoder.matches(password, fixture.getPassword())).thenReturn(true);

		//then
		Assertions.assertDoesNotThrow(() -> userService.login(userName, password));
	}

	@DisplayName("로그인시 userName이 존재하지 않는 경우")
	@Test
	void given_NonexistentUsername_when_Login_then_Throw() {
		//given
		String userName = "userName";
		String password = "password";

		//when
		//mocking
		when(userEntityRepository.findByUsername(userName)).thenReturn(Optional.empty());

		//then
		SnsApplicationException e = Assertions.assertThrows(SnsApplicationException.class,
			() -> userService.login(userName, password));
		Assertions.assertEquals(ErrorCode.USER_NOT_FOUND, e.getErrorCode());
	}

	@DisplayName("로그인시 userName이 존재하지만, password가 틀린 경우")
	@Test
	void given_WrongPassword_when_Login_then_Throw() {
		//given
		String userName = "userName";
		String password = "password";
		String wrongPassword = "wrongPassword";
		UserEntity fixture = UserEntityFixture.get(userName, password, 1);

		//when
		//mocking
		when(userEntityRepository.findByUsername(userName)).thenReturn(Optional.of(fixture));

		//then
		SnsApplicationException e = Assertions.assertThrows(SnsApplicationException.class,
			() -> userService.login(userName, wrongPassword));
		Assertions.assertEquals(ErrorCode.INVALID_PASSWORD, e.getErrorCode());
	}
}
