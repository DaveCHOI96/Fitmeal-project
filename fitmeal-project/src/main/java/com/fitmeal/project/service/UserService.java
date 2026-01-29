package com.fitmeal.project.service;


import java.util.List;
import java.util.Random;

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
@Transactional(readOnly = true) //모든 메서드 기본 읽기 전용 
public class UserService {
	
	//의존성 주입
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtTokenProvider jwtTokenProvider;
	private final EmailService emailService;
	
	public UserService(UserRepository userRepository, JwtTokenProvider jwtTokenProvider, EmailService emailService) {
		this.userRepository = userRepository;
		this.jwtTokenProvider = jwtTokenProvider;
		this.passwordEncoder = new BCryptPasswordEncoder();
		this.emailService = emailService;
	}
	
	@Transactional //update or create 작성용으로 사용허가 
    public Long signup(UserSignupRequestDto requestDto) {
		
		//클라이언트가 보낸 email을 emailService.isEmailVerified메서드로 boolean으로 인증 true or false반환
		if (!emailService.isEmailVerified(requestDto.getEmail())) {
			throw new IllegalArgumentException("이메일 인증이 완료되지 않았습니다.");
		}
		//클라이언트가 보낸 email을 existsByEmail메서드로 중복 확인
    	if (userRepository.existsByEmail(requestDto.getEmail())) {
    		throw new IllegalArgumentException("이미 사용 중인 이메일 입니다.");
    	}
    	
    	//generateTaggedNickName메서드 호출, 클라이언트로부터 받은 nickname에 태그#0000 입력
    	String taggedNickName = generateTaggedNickName(requestDto.getNickName());
    	//passwordEncoder.encode 호출, 클라이언트로부터 받은 패스워드 암호화(복호화 불가)
    	String encryptedPassword = passwordEncoder.encode(requestDto.getPassword());
    	
    	//User객체 생성 
    	User newUser = User.builder()
    			.email(requestDto.getEmail())
    			.password(encryptedPassword)
    			.nickName(taggedNickName)
    			.provider(User.ProviderType.LOCAL)
    			.role(UserRole.USER)
    			.level(1)
    			.experience(0L)
    			.build();
    	
    	//userRepository.save호출, newUser를 담아 데이터베이스에 insert
    	User savedUser = userRepository.save(newUser);
    	
    	//removeVerificationMark메서드 호출, 클라이언트로부터 받은 이메일에 인증DB/캐시 기록 제거(재사용 방지)
    	emailService.removeVerificationMark(requestDto.getEmail());
    	//데이터베이스에 생성된 User의 ID를 반환(회원가입 완료 후 클라이언트/프론트에서 ID참조 가능)
    	return savedUser.getUserId();
    }
	
	//로그인시 인증과, JWT토큰을 발급하는 메서드 
	@Transactional
	public TokenResponseDto login(String email, String password) {
		//user 데이터베이스에서 email 기준으로 user 객체를 검색
		User user = userRepository.findByEmail(email)
				//orElseThrow : email 기준으로 User 조회 및 확보 불가능하면 예외발생
				.orElseThrow(() -> new IllegalArgumentException("가입되지 않은 이메일입니다."));
		//matches : 데이터 베이스 유저객체가 가진 password와 클라이언트로 받은 password가 같은지 해시 비교
		if (!passwordEncoder.matches(password, user.getPassword())) {
			throw new IllegalArgumentException("잘못된 비빌번호 입니다.");
		}
		
		//accessToken을 생성하여 User 객체에 넣어준다.
		String accessToken = jwtTokenProvider.createAccessToken(user);
		String refreshToken = createAndSaveRefreshToken(user);
		//생성된 Refresh Token을 사용자의 DB 정보에 업데이트(저장)합니다.
		
		//두 토큰을 DTO에 담아 반환(클라이언트가 로그인 후 JWT를 사용할 수 있도록 전달
		return new TokenResponseDto(accessToken, refreshToken);
	}
	
	//클라이언트가 만료 직전 RefreshToken으로 AccessToken 재발급 요청 메서드
	@Transactional 
	public TokenResponseDto reissueTokens(String refreshToken) {
		//validateToken 메서드 호출, refreshToken 유효 평가 
		if (!jwtTokenProvider.validateToken(refreshToken)) {
			throw new IllegalArgumentException("유효하지 않은 Refresh Token 입니다.");
		}
		//DB에서 해당 Refresh Token을 가진 사용자를 찾습니다. RefreshToken이 DB에 없으면 예외 발생
		User user = userRepository.findByRefreshToken(refreshToken)
				.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 Refresh Token 입니다."));
		
		String newAccessToken = jwtTokenProvider.createAccessToken(user);
		String newRefreshToken = createAndSaveRefreshToken(user);
		
		//DTO에 새로운 토큰 담아서 클라이언트로 반환
		return new TokenResponseDto(newAccessToken, newRefreshToken);
	}
	
	@Transactional
	public void logout(Long userId) {
		//데이터베이스에서 id로 유저를 조회 없으면 예외 발생
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다. ID: " + userId));
		//사용자 객체의 refreshToken을 null로 변경
		user.updateRefreshToken(null);
	}
	
	
	@Transactional(readOnly = true)
	public List<User> findAllUsers() {
		return userRepository.findAll();
	}
	
	//새로운 refreshToken 생성 후 데이터베이스에 저장
	@Transactional
	public String createAndSaveRefreshToken(User user) {
		String token = jwtTokenProvider.createRefreshToken();
		//생성된 refreshToken을 token에 담아 updateRefreshToken 메서드 사용 user에게 token발급
		user.updateRefreshToken(token);
		userRepository.save(user);
		//새로 생성한 RefreshToken 반환
		return token;
	}
	
	//이메일 중복 체크 메서드
	public boolean isEmailDuplicate(String email) {
		return userRepository.existsByEmail(email);
	}
	
	//닉네임 중복 체크 메서드
	public boolean isNickNameDuplicate(String nickName) {
		return userRepository.existsByNickName(nickName);
	}
	
	//실제 데이터베이스에 User저장, 이메일 중복 확인 후 저장(signup에 이미 들어가있는 코드라 필요성체크)
	public User join(User user) {
		if (userRepository.existsByEmail(user.getEmail())) {
			throw new IllegalStateException("이미 존재하는 이메일입니다.");
		}
		return userRepository.save(user);
	}
	
	//닉네임에 #0000 태그를 붙이는 메서드 
	public String generateTaggedNickName(String baseNickName) {
        
		//baseNickName = 클라이언트에게 받은 닉네임, 입력된 닉네임이 null이나 isBlank(빈문자열)일 경우 기본값 설정 "user"로 대처
	    if (baseNickName == null || baseNickName.isBlank()) {
	        baseNickName = "user";
	    }

	    Random random = new Random();
	    //아래 반복문에서 만들어진 후보 닉네임 임시 저장 변수
	    String candidate;
        
	    //do-while 반복문
	    do {
	        int tag = random.nextInt(10_000); //0~9999사이 랜덤 숫자 생성, tag에 저장
	        //%s : 문자열 자리, %04d : 4자리 숫자, baseNickName에 저장된 tag 결합
	        candidate = String.format("%s#%04d", baseNickName, tag);
	        //데이터베이스에 candidate에 저장된 결합 닉네임 조회, existsByNickName메서드에서 true반환하면 다시 반복
	    } while (userRepository.existsByNickName(candidate));
        //반복문에서 통과한 최종 닉네임 반환
	    return candidate;
	}
	
	

}
