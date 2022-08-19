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
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sjk.sns.controller.request.PostCreateRequest;
import com.sjk.sns.controller.request.PostModifyRequest;
import com.sjk.sns.exception.ErrorCode;
import com.sjk.sns.exception.SnsApplicationException;
import com.sjk.sns.fixture.PostEntityFixture;
import com.sjk.sns.model.Post;
import com.sjk.sns.service.PostService;

@AutoConfigureMockMvc
@SpringBootTest
public class PostControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private PostService postService;

	@DisplayName("포스트 작성 성공")
	@Test
	@WithMockUser
	void given_PostInfo_when_WritingPost_then_IsOK() throws Exception {
		//given
		String title = "title";
		String body = "body";

		//when

		//then
		mockMvc.perform(post("/api/v1/posts")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsBytes(new PostCreateRequest(title, body)))
			).andDo(print())
			.andExpect(status().isOk());
	}

	@DisplayName("포스트 작성시 로그인하지 않은 경우")
	@Test
	@WithAnonymousUser
	void given_PostInfo_when_NonLogin_then_IsUnauthorized() throws Exception {
		//given
		String title = "title";
		String body = "body";

		//when

		//then
		mockMvc.perform(post("/api/v1/posts")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsBytes(new PostCreateRequest(title, body)))
			).andDo(print())
			.andExpect(status().isUnauthorized());
	}

	@DisplayName("포스트 수정 성공")
	@Test
	@WithMockUser
	void given_PostInfo_when_EditingPost_then_IsOK() throws Exception {
		//given
		String title = "title";
		String body = "body";

		//when
		when(postService.modify(eq(title), eq(body), any(), any()))
			.thenReturn(Post.fromEntity(PostEntityFixture.get("userName", 1, 1)));

		//then
		mockMvc.perform(put("/api/v1/posts/1")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsBytes(new PostModifyRequest(title, body)))
			).andDo(print())
			.andExpect(status().isOk());
	}

	@DisplayName("포스트 수정시, 로그인 안되어 있는 경우")
	@Test
	@WithAnonymousUser
	void given_NonLogin_when_EditingPost_then_IsUnauthorized() throws Exception {
		//given
		String title = "title";
		String body = "body";

		//when

		//then
		mockMvc.perform(put("/api/v1/posts/1")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsBytes(new PostModifyRequest(title, body)))
			).andDo(print())
			.andExpect(status().isUnauthorized());
	}

	@DisplayName("포스트 수정시, 본인이 작성한 글이 아닌 경우")
	@Test
	@WithMockUser
	void given_NotWrittenByMe_when_EditingPost_then_IsUnauthorized() throws Exception {
		//given
		String title = "title";
		String body = "body";

		//when
		doThrow(new SnsApplicationException(ErrorCode.INVALID_PERMISSION)).when(postService)
			.modify(eq(title), eq(body), any(), eq(1));

		//then
		mockMvc.perform(put("/api/v1/posts/1")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsBytes(new PostModifyRequest(title, body)))
			).andDo(print())
			.andExpect(status().isUnauthorized());
	}

	@DisplayName("포스트 수정시, 수정하려는 글이 없는 경우")
	@Test
	@WithMockUser
	void given_NonexistentPost_when_EditingPost_then_IsNotFound() throws Exception {
		//given
		String title = "title";
		String body = "body";

		//when
		doThrow(new SnsApplicationException(ErrorCode.POST_NOT_FOUND)).when(postService)
			.modify(eq(title), eq(body), any(), eq(1));

		//then
		mockMvc.perform(put("/api/v1/posts/1")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsBytes(new PostModifyRequest(title, body)))
			).andDo(print())
			.andExpect(status().isNotFound());
	}

	@DisplayName("포스트 삭제 성공")
	@Test
	@WithMockUser
	void given_Nothing_when_DeletingPost_then_IsOK() throws Exception {
		//given

		//when

		//then
		mockMvc.perform(delete("/api/v1/posts/1")
				.contentType(MediaType.APPLICATION_JSON)
			).andDo(print())
			.andExpect(status().isOk());
	}

	@DisplayName("포스트 삭제시, 로그인하지 않은 경우")
	@Test
	@WithAnonymousUser
	void given_NotLogin_when_DeletingPost_then_IsUnauthorized() throws Exception {
		//given

		//when

		//then
		mockMvc.perform(delete("/api/v1/posts/1")
				.contentType(MediaType.APPLICATION_JSON)
			).andDo(print())
			.andExpect(status().isUnauthorized());
	}

	@DisplayName("포스트 삭제시, 작성자와 삭제 요청자가 다른 경우")
	@Test
	@WithMockUser
	void given_NotWrittenByMe_when_DeletingPost_then_IsUnauthorized() throws Exception {
		//given

		//when
		doThrow(new SnsApplicationException(ErrorCode.INVALID_PERMISSION)).when(postService).delete(any(), any());

		//then
		mockMvc.perform(delete("/api/v1/posts/1")
				.contentType(MediaType.APPLICATION_JSON)
			).andDo(print())
			.andExpect(status().isUnauthorized());
	}

	@DisplayName("포스트 삭제시, 포스트가 존재하지 않는 경우")
	@Test
	@WithMockUser
	void given_NonexistentPost_when_DeletingPost_then_IsNotFound() throws Exception {
		//given

		//when
		doThrow(new SnsApplicationException(ErrorCode.POST_NOT_FOUND)).when(postService).delete(any(), any());

		//then
		mockMvc.perform(delete("/api/v1/posts/1")
				.contentType(MediaType.APPLICATION_JSON)
			).andDo(print())
			.andExpect(status().isNotFound());
	}

}
