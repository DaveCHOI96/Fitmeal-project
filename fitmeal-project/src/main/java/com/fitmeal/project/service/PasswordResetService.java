package com.fitmeal.project.service;

import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.fitmeal.project.entity.User;
import com.fitmeal.project.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PasswordResetService {

	private final UserRepository userRepository;
	private final StringRedisTemplate stringRedisTemplate;
	private final PasswordEncoder passwordEncoder;
	private final EmailService emailService;
	
	//임시 비밀번호 유효시간 10분
	private static final long TEMP_PW_EXPIRE_MINUTES = 10;
	//임시 비밀번호 길이 10자
	private static final int TEMP_PASSWORD_LENGTH = 10;
	private static final String CHAR_POOL = 
			"ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
	        "abcdefghijklmnopqrstuvwxyz" +
			"0123456789";

	
	public String generateTempPassword() {
		SecureRandom random = new SecureRandom();
		StringBuilder password = new StringBuilder();
		
		for (int i=0; i<TEMP_PASSWORD_LENGTH; i++) {
			int index = random.nextInt(CHAR_POOL.length());
			password.append(CHAR_POOL.charAt(index));
		}
		return password.toString();
	}
	
	public void requestPasswordReset(String email) {
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이메일입니다."));
		
		if (user.getProvider() != User.ProviderType.LOCAL) {
			throw new IllegalStateException("소셜 로그인 계정은 비밀번호 재설정이 불가능합니다.");
		}
		String tempPassword = generateTempPassword();
		
		stringRedisTemplate.opsForValue().set(
				"TEMP_PW:" + email,
				passwordEncoder.encode(tempPassword),
				TEMP_PW_EXPIRE_MINUTES,
				TimeUnit.MINUTES
		);
		emailService.sendTempPassword(email, tempPassword);
	}
	
	public void resetPassword(String email, String inputTempPassword, String newPassword) {
		String key = "TEMP_PW:" + email;
		String savedEncodedTempPw = stringRedisTemplate.opsForValue().get(key);
		
		if (savedEncodedTempPw == null) {
			throw new IllegalArgumentException("임시 비밀번호가 만료되었거나 존재하지 않습니다.");
		}
		if (!passwordEncoder.matches(inputTempPassword, savedEncodedTempPw)) {
			throw new IllegalArgumentException("임시 비밀번호가 일치하지 않습니다.");
		}
		User user = userRepository.findByEmail(email)
	            .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
		
		user.updatePassword(passwordEncoder.encode(newPassword));
		
		stringRedisTemplate.delete(key);
	}

}
