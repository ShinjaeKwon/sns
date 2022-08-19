package com.sjk.sns.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sjk.sns.exception.ErrorCode;
import com.sjk.sns.exception.SnsApplicationException;
import com.sjk.sns.model.User;
import com.sjk.sns.model.entity.UserEntity;
import com.sjk.sns.repository.UserEntityRepository;
import com.sjk.sns.util.JwtTokenUtils;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserService {

	private final UserEntityRepository userEntityRepository;
	private final BCryptPasswordEncoder encoder;

	@Value("${jwt.secret-key}")
	private String secretKey;

	@Value("${jwt.token.expired-time-ms}")
	private Long expiredTimeMS;

	public User loadUserByUserName(String userName) {
		return userEntityRepository.findByUsername(userName).map(User::fromEntity)
			.orElseThrow(() -> new SnsApplicationException(ErrorCode.USER_NOT_FOUND,
				String.format("%s is not founded", userName)));
	}

	@Transactional
	public User join(String userName, String password) {
		userEntityRepository.findByUsername(userName).ifPresent(it -> {
			throw new SnsApplicationException(ErrorCode.DUPLICATED_USER_NAME,
				String.format("$s is duplicated", userName));
		});

		UserEntity userEntity = userEntityRepository.save(UserEntity.of(userName, encoder.encode(password)));
		return User.fromEntity(userEntity);
	}

	public String login(String userName, String password) {
		UserEntity userEntity = userEntityRepository.findByUsername(userName)
			.orElseThrow(() -> new SnsApplicationException(ErrorCode.USER_NOT_FOUND,
				String.format("%s not founded", userName)));

		if (!encoder.matches(password, userEntity.getPassword())) {
			throw new SnsApplicationException(ErrorCode.INVALID_PASSWORD);
		}

		return JwtTokenUtils.generateToken(userName, secretKey, expiredTimeMS);
	}

}
