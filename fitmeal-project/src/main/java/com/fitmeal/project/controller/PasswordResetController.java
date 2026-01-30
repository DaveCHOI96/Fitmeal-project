package com.fitmeal.project.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fitmeal.project.dto.PasswordChangeRequestDto;
import com.fitmeal.project.dto.PasswordResetRequestDto;
import com.fitmeal.project.service.PasswordResetService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth/password")
@RequiredArgsConstructor
public class PasswordResetController {
	
	private final PasswordResetService passwordResetService;
	
	//임시 비밀번호 발급 요청
	@PostMapping("/reset")
	public ResponseEntity<String> requestReset(
			@Valid @RequestBody PasswordResetRequestDto dto) {
		passwordResetService.requestPasswordReset(dto.getEmail());
		return ResponseEntity.ok().build();
	}
	
	//임시 비밀번호를 새 비밀번호로 변경 요청
	@PostMapping("/change")
	public ResponseEntity<String> resetPassword(
			@Valid @RequestBody PasswordChangeRequestDto dto) {
		passwordResetService.resetPassword(
				dto.getEmail(),
				dto.getTempPassword(),
				dto.getNewPassword()
		);
		return ResponseEntity.ok().build();
	}
	
	

}
