package com.sjk.sns.service;

import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.sjk.sns.exception.ErrorCode;
import com.sjk.sns.exception.SnsApplicationException;
import com.sjk.sns.fixture.PostEntityFixture;
import com.sjk.sns.fixture.UserEntityFixture;
import com.sjk.sns.model.entity.PostEntity;
import com.sjk.sns.model.entity.UserEntity;
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

	@DisplayName("포스트수정이 정상적으로 이루어진 경우")
	@Test
	void given_PostInfo_when_EditingPost_then_EditPost() {
		//given
		String title = "title";
		String body = "body";
		String userName = "userName";
		Integer postId = 1;

		PostEntity postEntity = PostEntityFixture.get(userName, postId, 1);
		UserEntity userEntity = postEntity.getUser();

		//when
		when(userEntityRepository.findByUsername(userName)).thenReturn(Optional.of(userEntity));
		when(postEntityRepository.findById(postId)).thenReturn(Optional.of(postEntity));
		when(postEntityRepository.saveAndFlush(postEntity)).thenReturn(postEntity);

		//then
		Assertions.assertDoesNotThrow(() -> postService.modify(title, body, userName, postId));
	}

	@DisplayName("포스트수정을 할때 포스트가 존재하지 않는 경우")
	@Test
	void given_NonexistentPost_when_EditingPost_then_ThrowsException() {
		//given
		String title = "title";
		String body = "body";
		String userName = "userName";
		Integer postId = 1;

		PostEntity postEntity = PostEntityFixture.get(userName, postId, 1);
		UserEntity userEntity = postEntity.getUser();

		//when
		when(userEntityRepository.findByUsername(userName)).thenReturn(Optional.of(userEntity));
		when(postEntityRepository.findById(postId)).thenReturn(Optional.empty());

		//then
		SnsApplicationException e = Assertions.assertThrows(SnsApplicationException.class,
			() -> postService.modify(title, body, userName, postId));
		Assertions.assertEquals(ErrorCode.POST_NOT_FOUND, e.getErrorCode());
	}

	@DisplayName("포스트수정시 권한이 없는 경우")
	@Test
	void given_UnAuthorized_when_EditingPost_then_ThrowsException() {
		//given
		String title = "title";
		String body = "body";
		String userName = "userName";
		Integer postId = 1;

		PostEntity postEntity = PostEntityFixture.get(userName, postId, 1);
		UserEntity userEntity = postEntity.getUser();
		UserEntity writer = UserEntityFixture.get("userName1", "password", 2);

		//when
		when(userEntityRepository.findByUsername(userName)).thenReturn(Optional.of(writer));
		when(postEntityRepository.findById(postId)).thenReturn(Optional.of(postEntity));

		//then
		SnsApplicationException e = Assertions.assertThrows(SnsApplicationException.class,
			() -> postService.modify(title, body, userName, postId));
		Assertions.assertEquals(ErrorCode.INVALID_PERMISSION, e.getErrorCode());
	}

	@DisplayName("포스트삭제가 정상적으로 이루어진 경우")
	@Test
	void given_PostInfo_when_DeletingPost_then_DeletePost() {
		//given
		String userName = "userName";
		Integer postId = 1;

		PostEntity postEntity = PostEntityFixture.get(userName, postId, 1);
		UserEntity userEntity = postEntity.getUser();

		//when
		when(userEntityRepository.findByUsername(userName)).thenReturn(Optional.of(userEntity));
		when(postEntityRepository.findById(postId)).thenReturn(Optional.of(postEntity));

		//then
		Assertions.assertDoesNotThrow(() -> postService.delete(userName, postId));
	}

	@DisplayName("포스트삭제 시 포스트가 존재하지 않는 경우")
	@Test
	void given_NonexistentPost_when_DeletingPost_then_ThrowsException() {
		//given
		String userName = "userName";
		Integer postId = 1;

		PostEntity postEntity = PostEntityFixture.get(userName, postId, 1);
		UserEntity userEntity = postEntity.getUser();

		//when
		when(userEntityRepository.findByUsername(userName)).thenReturn(Optional.of(userEntity));
		when(postEntityRepository.findById(postId)).thenReturn(Optional.empty());

		//then
		SnsApplicationException e = Assertions.assertThrows(SnsApplicationException.class,
			() -> postService.delete(userName, postId));
		Assertions.assertEquals(ErrorCode.POST_NOT_FOUND, e.getErrorCode());
	}

	@DisplayName("포스트삭제 시 권한이 없는 경우")
	@Test
	void given_UnAuthorized_when_DeletingPost_then_ThrowsException() {
		//given
		String userName = "userName";
		Integer postId = 1;

		PostEntity postEntity = PostEntityFixture.get(userName, postId, 1);
		UserEntity userEntity = postEntity.getUser();
		UserEntity writer = UserEntityFixture.get("userName1", "password", 2);

		//when
		when(userEntityRepository.findByUsername(userName)).thenReturn(Optional.of(writer));
		when(postEntityRepository.findById(postId)).thenReturn(Optional.of(postEntity));

		//then
		SnsApplicationException e = Assertions.assertThrows(SnsApplicationException.class,
			() -> postService.delete(userName, postId));
		Assertions.assertEquals(ErrorCode.INVALID_PERMISSION, e.getErrorCode());
	}

	@DisplayName("피드목록요청이 정상적으로 이루어진 경우")
	@Test
	void given_None_when_RequestFeeds_then_NotThrows() {
		//given

		//when
		Pageable pageable = mock(Pageable.class);
		when(postEntityRepository.findAll(pageable)).thenReturn(Page.empty());

		//then
		Assertions.assertDoesNotThrow(() -> postService.list(pageable));
	}

	@DisplayName("내 피드목록요청이 정상적으로 이루어진 경우")
	@Test
	void given_None_when_RequestMyFeeds_then_NotThrows() {
		//given

		//when
		UserEntity user = mock(UserEntity.class);
		Pageable pageable = mock(Pageable.class);
		when(userEntityRepository.findByUsername(any())).thenReturn(Optional.of(user));
		when(postEntityRepository.findAllByUser(user, pageable)).thenReturn(Page.empty());

		//then
		Assertions.assertDoesNotThrow(() -> postService.my("", pageable));
	}

}
