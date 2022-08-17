package com.sjk.sns.service;

import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.sjk.sns.exception.ErrorCode;
import com.sjk.sns.exception.SnsApplicationException;
import com.sjk.sns.exception.model.entity.PostEntity;
import com.sjk.sns.exception.model.entity.UserEntity;
import com.sjk.sns.repository.PostEntityRepository;
import com.sjk.sns.repository.UserEntityRepository;

@SpringBootTest
public class PostServiceTest {

	@Autowired
	private PostService postService;

	@MockBean
	private PostEntityRepository postEntityRepository;

	@MockBean
	private UserEntityRepository userEntityRepository;

	@DisplayName("포스트작성이 정상적으로 이루어진 경우")
	@Test
	void given_PostInfo_when_WritingPost_then_WritesPost() {
		//given
		String title = "title";
		String body = "body";
		String userName = "userName";

		//when
		when(userEntityRepository.findByUsername(userName)).thenReturn(Optional.of(mock(UserEntity.class)));
		when(postEntityRepository.save(any())).thenReturn(mock(PostEntity.class));

		//then
		Assertions.assertDoesNotThrow(() -> postService.create(title, body, userName));
	}

	@DisplayName("포스트작성시 요청한 유저가 존재하지 않는 경우")
	@Test
	void given_NonexistentUser_when_WritingPost_then_throws() {
		//given
		String title = "title";
		String body = "body";
		String userName = "userName";

		//when
		when(userEntityRepository.findByUsername(userName)).thenReturn(Optional.empty());
		when(postEntityRepository.save(any())).thenReturn(mock(PostEntity.class));

		//then
		SnsApplicationException e = Assertions.assertThrows(SnsApplicationException.class,
			() -> postService.create(title, body, userName));

		Assertions.assertEquals(ErrorCode.USER_NOT_FOUND, e.getErrorCode());
	}

}
