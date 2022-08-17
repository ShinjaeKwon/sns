package com.sjk.sns.fixture;

import com.sjk.sns.exception.model.entity.UserEntity;

public class UserEntityFixture {

	public static UserEntity get(String userName, String password) {
		UserEntity result = new UserEntity();
		result.setId(1);
		result.setUsername(userName);
		result.setPassword(password);
		return result;
	}
}
