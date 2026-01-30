package com.fitmeal.project.service;

import java.time.Duration;
import java.util.Random;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailService {
	
	private final JavaMailSender mailSender;
	private final StringRedisTemplate redisTemplate;
	private static final String AUTH_CODE_PREFIX = "AuthCode_";
	private static final String AUTH_SUCCESS_PREFIX = "AuthSuccess_";
	
	public String sendAuthCode(String email) {
		String authCode = createAuthCode();
		String subject = "[FitMeal] 회원가입 인증 코드 안내";
        String text = "FitMeal에 가입해주셔서 감사합니다.\n"
                    + "아래의 인증 코드를 입력하여 회원가입을 완료해주세요.\n\n"
                    + "인증 코드: " + authCode + "\n\n"
                    + "이 코드는 5분 동안 유효합니다.";
        
        redisTemplate.opsForValue().set(
        		AUTH_CODE_PREFIX + email,
        		authCode,
        		Duration.ofMinutes(5)
        );
        
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
        
        return authCode;
	}
	
	//사용자가 입력한 인증 코드가 유효한지 검증하는 메소드
	public boolean verifyAuthCode(String email, String inputCode) {
		String storedCode = redisTemplate.opsForValue().get(AUTH_CODE_PREFIX + email);
		
		if (storedCode != null && storedCode.equals(inputCode)) {
			redisTemplate.delete(AUTH_CODE_PREFIX + email);
			
			//인증 완료 마크 저장 (5분 TTL) 
			redisTemplate.opsForValue().set(
					AUTH_SUCCESS_PREFIX + email,
					"VERIFIED",
					Duration.ofMinutes(5)
			);
			return true;
		}
		return false;
	}
	
	private String createAuthCode() {
		Random random = new Random();
		int number = 100000 + random.nextInt(900000);
		return String.valueOf(number);
	}
	
	public boolean isEmailVerified(String email) {
		return redisTemplate.hasKey(AUTH_SUCCESS_PREFIX + email);
	}
	
	public void removeVerificationMark(String email) {
		redisTemplate.delete(AUTH_SUCCESS_PREFIX + email);
	}
	
	public void sendTempPassword(String email, String tempPassword) {
		String subject = "[FitMeal] 임시 비밀번호 안내";
	    String content = """
	        안녕하세요. FitMeal 입니다.

	        요청하신 임시 비밀번호는 아래와 같습니다.

	        임시 비밀번호: %s

	        로그인 후 반드시 비밀번호를 변경해주세요.
	        """.formatted(tempPassword);
	    SimpleMailMessage message = new SimpleMailMessage();
	    message.setTo(email);
	    message.setSubject(subject);
	    message.setText(content);
	    mailSender.send(message);
	}

}
