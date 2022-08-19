package com.sjk.sns.fixture;

import com.sjk.sns.model.entity.UserEntity;

public class UserEntityFixture {

	public static UserEntity get(String userName, String password, Integer userId) {
		UserEntity result = new UserEntity();
		result.setId(userId);
		result.setUsername(userName);
		result.setPassword(password);
		return result;
	}
}
