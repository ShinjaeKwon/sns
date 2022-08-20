package com.sjk.sns.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sjk.sns.exception.ErrorCode;
import com.sjk.sns.exception.SnsApplicationException;
import com.sjk.sns.model.Post;
import com.sjk.sns.model.entity.PostEntity;
import com.sjk.sns.model.entity.UserEntity;
import com.sjk.sns.repository.PostEntityRepository;
import com.sjk.sns.repository.UserEntityRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class PostService {

	private final PostEntityRepository postEntityRepository;
	private final UserEntityRepository userEntityRepository;

	@Transactional
	public void create(String title, String body, String userName) {
		UserEntity userEntity = userEntityRepository.findByUsername(userName)
			.orElseThrow(
				() -> new SnsApplicationException(ErrorCode.USER_NOT_FOUND, String.format("%s not founded", userName)));
		postEntityRepository.save(PostEntity.of(title, body, userEntity));
	}

	@Transactional
	public Post modify(String title, String body, String userName, Integer postId) {
		UserEntity userEntity = userEntityRepository.findByUsername(userName)
			.orElseThrow(
				() -> new SnsApplicationException(ErrorCode.USER_NOT_FOUND, String.format("%s not founded", userName)));

		PostEntity postEntity = postEntityRepository.findById(postId)
			.orElseThrow(
				() -> new SnsApplicationException(ErrorCode.POST_NOT_FOUND, String.format("%s not founded", postId)));

		if (postEntity.getUser() != userEntity) {
			throw new SnsApplicationException(ErrorCode.INVALID_PERMISSION,
				String.format("%s has no permission with %s", userName, postId));
		}

		postEntity.setTitle(title);
		postEntity.setBody(body);

		return Post.fromEntity(postEntityRepository.saveAndFlush(postEntity));
	}

	@Transactional
	public void delete(String userName, Integer postId) {
		UserEntity userEntity = userEntityRepository.findByUsername(userName)
			.orElseThrow(
				() -> new SnsApplicationException(ErrorCode.USER_NOT_FOUND, String.format("%s not founded", userName)));

		PostEntity postEntity = postEntityRepository.findById(postId)
			.orElseThrow(
				() -> new SnsApplicationException(ErrorCode.POST_NOT_FOUND, String.format("%s not founded", postId)));

		if (postEntity.getUser() != userEntity) {
			throw new SnsApplicationException(ErrorCode.INVALID_PERMISSION,
				String.format("%s has no permission with %s", userName, postId));
		}

		postEntityRepository.delete(postEntity);
	}

	public Page<Post> list(Pageable pageable) {
		return postEntityRepository.findAll(pageable).map(Post::fromEntity);
	}

	public Page<Post> my(String userName, Pageable pageable) {
		UserEntity userEntity = userEntityRepository.findByUsername(userName)
			.orElseThrow(
				() -> new SnsApplicationException(ErrorCode.USER_NOT_FOUND, String.format("%s not founded", userName)));

		return postEntityRepository.findAllByUser(userEntity, pageable).map(Post::fromEntity);
	}

}
