package com.fitmeal.project.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fitmeal.project.service.PasswordResetService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth/password")
@RequiredArgsConstructor
public class PasswordResetController {
	
	private final PasswordResetService passwordResetService;
	
	//임시 비밀번호 발급 요청
	@PostMapping("/reset-request")
	public ResponseEntity<String> requestReset(@RequestParam String email) {
		passwordResetService.requestPasswordReset(email);
		return ResponseEntity.ok("임시 비밀번호가 이메일로 전송되었습니다.");
	}
	
	public ResponseEntity<String> resetPassword(
			@RequestParam String email,
			@RequestParam String tempPassword,
			@RequestParam String newPassword
		) {
		passwordResetService.resetPassword(email, tempPassword, newPassword);
		return ResponseEntity.ok("비밀번호가 성공적으로 변경되었습니다.");
	}
	
	

}
