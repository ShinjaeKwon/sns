package com.sjk.sns.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sjk.sns.exception.ErrorCode;
import com.sjk.sns.exception.SnsApplicationException;
import com.sjk.sns.exception.model.entity.PostEntity;
import com.sjk.sns.exception.model.entity.UserEntity;
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

}
