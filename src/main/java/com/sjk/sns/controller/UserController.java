package com.sjk.sns.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sjk.sns.controller.request.UserJoinRequest;
import com.sjk.sns.controller.request.UserLoginRequest;
import com.sjk.sns.controller.response.Response;
import com.sjk.sns.controller.response.UserJoinResponse;
import com.sjk.sns.controller.response.UserLoginResponse;
import com.sjk.sns.model.User;
import com.sjk.sns.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;

	@PostMapping("/join")
	public Response<UserJoinResponse> join(@RequestBody UserJoinRequest request) {
		User user = userService.join(request.getUserName(), request.getPassword());
		return Response.success(UserJoinResponse.fromUser(user));
	}

	@PostMapping("/login")
	public Response<UserLoginResponse> login(@RequestBody UserLoginRequest request) {
		String token = userService.login(request.getUserName(), request.getPassword());
		return Response.success(new UserLoginResponse(token));
	}
}
