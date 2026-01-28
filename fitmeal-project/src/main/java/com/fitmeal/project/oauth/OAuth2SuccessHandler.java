package com.fitmeal.project.oauth;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import com.fitmeal.project.config.JwtTokenProvider;
import com.fitmeal.project.entity.User;
import com.fitmeal.project.repository.UserRepository;
import com.fitmeal.project.service.UserService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler{
	
	private static final Logger logger = LoggerFactory.getLogger(OAuth2SuccessHandler.class);
	private final JwtTokenProvider jwtTokenProvider;
	private final UserRepository userRepository;
	private final UserService userService;
	
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
	    throws IOException, ServletException {
		
	    OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
	    OAuth2User oAuth2User = token.getPrincipal();
		
	    String registrationId = token.getAuthorizedClientRegistrationId();
	    
	    OAuthAttributes attributes = OAuthAttributes.of(registrationId, oAuth2User.getName(), oAuth2User.getAttributes());
	    String email = attributes.getEmail();
	    
	    if (email == null) {
	    	logger.error("OAuth2 인증 후 이메일을 가져올 수 없습니다. Attributes: {}", attributes.getAttributes());
	    	throw new IllegalArgumentException("소셜 로그인 정보에 이메일이 포함되어 있지 않습니다.");
	    }
		
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new IllegalArgumentException("OAuth2 인증 후 사용자를 DB에서 찾을 수 없습니다. email=" + email));
		
		String accessToken = jwtTokenProvider.createAccessToken(user);
		logger.info("생성된 Access Token: {}", accessToken);
		String refreshToken = userService.createAndSaveRefreshToken(user);
		logger.info("생성된 Refresh Token: {}", refreshToken);
		// 5. 프론트엔드로 토큰들을 전달하기 위한 최종 리디렉션 URL을 생성합니다.
		String targetUrl = UriComponentsBuilder.fromUriString("http://localhost:3000/oauth-redirect")
				.queryParam("accessToken", accessToken)
				.queryParam("refreshToken", refreshToken)
				.build()
				.encode(StandardCharsets.UTF_8)
				.toUriString();
		
		getRedirectStrategy().sendRedirect(request, response, targetUrl);
	}
	
		
		
	

}
