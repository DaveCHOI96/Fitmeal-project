package com.fitmeal.project.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class PasswordChangeRequestDto {
	
	@NotBlank(message = "이메일은 필수입니다.")
	private String email;
	
	@NotBlank(message = "임시 비밀번호를 입력해주세요")
	private String tempPassword;
	
	@NotBlank(message = "새 비밀번호를 입력주세요.")
	@Size(min = 8, message = "비밀번호는 최소8자 이상이어야 합니다.")
	private String newPassword;

}
