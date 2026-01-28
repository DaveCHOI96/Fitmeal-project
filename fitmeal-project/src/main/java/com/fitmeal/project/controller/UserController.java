package com.fitmeal.project.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fitmeal.project.dto.TokenResponseDto;
import com.fitmeal.project.dto.UserSignupRequestDto;
import com.fitmeal.project.service.EmailService;
import com.fitmeal.project.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
	
	private final UserService userService;
	private final EmailService emailService;
	
	@PostMapping("/signup")
	public ResponseEntity<String> signup(@RequestBody UserSignupRequestDto requestDto) {
		try {
			userService.signup(requestDto);
			return ResponseEntity.ok("회원가입이 성공적으로 완료되었습니다.");
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	
	@PostMapping("/login")
	public ResponseEntity<TokenResponseDto> login(@RequestBody Map<String, String> loginRequest) {
		String email = loginRequest.get("email");
		String password = loginRequest.get("password");
		TokenResponseDto tokenDto = userService.login(email, password);
		
		return ResponseEntity.ok(tokenDto);
	}
	
	@PostMapping("/logout")
	public ResponseEntity<String> logout(@AuthenticationPrincipal org.springframework.security.core.userdetails.User principal) {
		Long userId = Long.parseLong(principal.getUsername());
		userService.logout(userId);
		return ResponseEntity.ok("성공적으로 로그아웃 되었습니다.");
	}
	
	@GetMapping("/me")
	public ResponseEntity<String> getMyInfo(@AuthenticationPrincipal User user) {
		// user.getUsername()은 우리가 JWT의 subject에 저장했던 '사용자 ID'를 반환합니다.
		String userInfo = "인증된 사용자 ID: " + user.getUsername();
		return ResponseEntity.ok(userInfo);
	}
	
	//인증 코드 발송 API
	@PostMapping("/email/send-code")
	public ResponseEntity<?> sendAuthCode(@RequestBody Map<String, String> request) {
		String email = request.get("email");
		
		if (userService.isEmailDuplicate(email)) {
			return ResponseEntity.badRequest().body("이미 가입된 이메일 입니다");
		}
		emailService.sendAuthCode(email);
		return ResponseEntity.ok("인증 코드가 발송되었습니다.");
	}
	
	//인증 코드 검증 API
	@PostMapping("/email/verify-code")
	public ResponseEntity<?> verifyAuthCode(@RequestBody Map<String, String> request) {
		String email = request.get("email");
		String code = request.get("code");
		
		boolean isVerified = emailService.verifyAuthCode(email, code);
		
		if (isVerified) {
			return ResponseEntity.ok("인증에 성공하였습니다.");
		} else {
			return ResponseEntity.badRequest().body("인증 코드가 일치하지 않거나 만료되었습니다");
		}
	}

}
