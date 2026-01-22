package com.fitmeal.project.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserSignupRequestDto {
	
	private String email;
	private String password;
	private String nickName;
	
	// 나중에 Service에서 User Entity로 변환할 때 사용할 수 있는 메소드 (선택 사항이지만 유용함)
    // public User toEntity(String encryptedPassword) {
    //     return User.builder()
    //             .email(this.email)
    //             .password(encryptedPassword)
    //             .nickName(this.nickName)
    //             .provider(User.Provider.LOCAL) // 자체 회원가입이므로 'LOCAL'로 고정
    //             .role(User.UserRole.USER)     // 기본 역할은 'USER'
    //             .level(1)
    //             .experience(0L)
    //             .build();
    // }

}
