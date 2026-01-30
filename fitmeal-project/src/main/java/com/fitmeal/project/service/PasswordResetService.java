package com.fitmeal.project.service;

import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fitmeal.project.entity.User;
import com.fitmeal.project.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class PasswordResetService {

	private final UserRepository userRepository;
	private final StringRedisTemplate stringRedisTemplate;
	private final PasswordEncoder passwordEncoder;
	private final EmailService emailService;
	
	//임시 비밀번호 유효시간 10분
	private static final long TEMP_PW_EXPIRE_MINUTES = 10;
	//임시 비밀번호 길이 10자
	private static final int TEMP_PASSWORD_LENGTH = 10;
	//임시 비밀번호에 사용할 문자 집합
	private static final String CHAR_POOL = 
			"ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
	        "abcdefghijklmnopqrstuvwxyz" +
			"0123456789";

	//임시 비밀번호 생성 메서드
	public String generateTempPassword() {
		//SecureRandom = 일반 Random보다 보안적으로 안전한 난수 생성기
		SecureRandom random = new SecureRandom();
		//문자열 효율적 이어붙이기 위한 객체
		StringBuilder password = new StringBuilder();
		
		for (int i=0; i<TEMP_PASSWORD_LENGTH; i++) {
			//CHAR_POOL 길이 범위 내에서 랜덤 인덱스 생성
			int index = random.nextInt(CHAR_POOL.length());
			//랜덤으로 뽑은 문자 하나를 비밀번호에 추가
			password.append(CHAR_POOL.charAt(index));
		}
		//완성된 임시 비밀번호 반환
		return password.toString();
	}
	
	//USER가 이메일 입력 후, 비밀번호 재설정 요청 메서드
	public void requestPasswordReset(String email) {
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이메일입니다."));
		
		if (user.getProvider() != User.ProviderType.LOCAL) {
			throw new IllegalStateException("소셜 로그인 계정은 비밀번호 재설정이 불가능합니다.");
		}
		//생성된 임시 비밀번호 tempPassword에 담기 
		String tempPassword = generateTempPassword();
		
		//Redis에 임시 비밀번호 저장
		stringRedisTemplate.opsForValue().set(
				"TEMP_PW:" + email,
				passwordEncoder.encode(tempPassword),
				TEMP_PW_EXPIRE_MINUTES,
				TimeUnit.MINUTES
		);
		//생성된 임시 비밀번호 이메일 전송
		emailService.sendTempPassword(email, tempPassword);
	}
	
	//임시 비밀번호 검증 및 새 비밀번호 설정 메서드
	public void resetPassword(String email, String inputTempPassword, String newPassword) {
		//Redis에서 email로 저장된 임시 비밀번호 조회
		String key = "TEMP_PW:" + email;
		String savedEncodedTempPw = stringRedisTemplate.opsForValue().get(key);
		
		if (savedEncodedTempPw == null) {
			throw new IllegalArgumentException("임시 비밀번호가 만료되었거나 존재하지 않습니다.");
		}
		//임시 비밀번호 일치 여부 확인
		if (!passwordEncoder.matches(inputTempPassword, savedEncodedTempPw)) {
			throw new IllegalArgumentException("임시 비밀번호가 일치하지 않습니다.");
		}
		//사용자 재조회
		User user = userRepository.findByEmail(email)
	            .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
		//새 비밀번호로 변경
		user.updatePassword(passwordEncoder.encode(newPassword));
		
		//Redis 임시 비밀번호 삭제
		stringRedisTemplate.delete(key);
	}

}
