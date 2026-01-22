package com.fitmeal.project.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fitmeal.project.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth" )
@RequiredArgsConstructor
public class AuthController {
	
	private final UserService userService;
	
	//헤더(Header) 에 X-Refresh-Token이라는 이름으로 담겨 오는 값을 refreshToken 변수에 넣어줘!" 라는 뜻입니다. 
	@PostMapping("/reissue")
	public ResponseEntity<String> reissue(@RequestHeader("X-Refresh-Token") String refreshToken) {
		String newAccessToken = userService.reissueAccessToken(refreshToken);
		return ResponseEntity.ok(newAccessToken);
	}

}
