package com.sjk.sns.fixture;

import com.sjk.sns.model.entity.PostEntity;
import com.sjk.sns.model.entity.UserEntity;

public class PostEntityFixture {

	public static PostEntity get(String userName, Integer postId, Integer userId) {
		UserEntity user = new UserEntity();
		user.setId(userId);
		user.setUsername(userName);

		PostEntity result = new PostEntity();
		result.setUser(user);
		result.setId(postId);
		return result;
	}
}
