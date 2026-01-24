package com.fitmeal.project.service;


import java.util.List;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fitmeal.project.common.UserRole;
import com.fitmeal.project.config.JwtTokenProvider;
import com.fitmeal.project.dto.TokenResponseDto;
import com.fitmeal.project.dto.UserSignupRequestDto;
import com.fitmeal.project.entity.User;
import com.fitmeal.project.repository.UserRepository;

@Service
@Transactional(readOnly = true)
public class UserService {
	
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtTokenProvider jwtTokenProvider;
	
	public UserService(UserRepository userRepository, JwtTokenProvider jwtTokenProvider) {
		this.userRepository = userRepository;
		this.jwtTokenProvider = jwtTokenProvider;
		this.passwordEncoder = new BCryptPasswordEncoder();
	}
	
	@Transactional
    public Long signup(UserSignupRequestDto requestDto) {
    	if (userRepository.existsByEmail(requestDto.getEmail())) {
    		throw new IllegalArgumentException("이미 사용 중인 이메일 입니다.");
    	}
    	
    	if (userRepository.existsByNickName(requestDto.getNickName())) {
    		throw new IllegalArgumentException("이미 사용 중인 닉네임 입니다.");
    	}
    	
    	String encryptedPassword = passwordEncoder.encode(requestDto.getPassword());
    	
    	User newUser = User.builder()
    			.email(requestDto.getEmail())
    			.password(encryptedPassword)
    			.nickName(requestDto.getNickName())
    			.provider(User.ProviderType.LOCAL)
    			.role(UserRole.USER)
    			.level(1)
    			.experience(0L)
    			.build();
    	
    	User savedUser = userRepository.save(newUser);
    	return savedUser.getUserId();
    }
	
	@Transactional
	public TokenResponseDto login(String email, String password) {
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new IllegalArgumentException("가입되지 않은 이메일입니다."));
		if (!passwordEncoder.matches(password, user.getPassword())) {
			throw new IllegalArgumentException("잘못된 비빌번호 입니다.");
		}
		
		String accessToken = jwtTokenProvider.createAccessToken(user);
		String refreshToken = createAndSaveRefreshToken(user);
		//생성된 Refresh Token을 사용자의 DB 정보에 업데이트(저장)합니다.
//		user.updateRefreshToken(refreshToken);
		
		//두 토큰을 DTO에 담아 반환.
		return new TokenResponseDto(accessToken, refreshToken);
	}
	
	@Transactional 
	public TokenResponseDto reissueTokens(String refreshToken) {
		if (!jwtTokenProvider.validateToken(refreshToken)) {
			throw new IllegalArgumentException("유효하지 않은 Refresh Token 입니다.");
		}
		//DB에서 해당 Refresh Token을 가진 사용자를 찾습니다.
		User user = userRepository.findByRefreshToken(refreshToken)
				.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 Refresh Token 입니다."));
		
		String newAccessToken = jwtTokenProvider.createAccessToken(user);
		String newRefreshToken = createAndSaveRefreshToken(user);
		
		return new TokenResponseDto(newAccessToken, newRefreshToken);
	}
	
	@Transactional
	public void logout(Long userId) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다. ID: " + userId));
		user.updateRefreshToken(null);
	}
	
	@Transactional(readOnly = true)
	public List<User> findAllUsers() {
		return userRepository.findAll();
	}
	
	@Transactional
	public String createAndSaveRefreshToken(User user) {
		String token = jwtTokenProvider.createRefreshToken();
		user.updateRefreshToken(token);
		userRepository.save(user);
		return token;
	}
	

}
