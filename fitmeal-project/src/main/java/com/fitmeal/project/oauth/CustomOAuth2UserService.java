package com.fitmeal.project.oauth;

import java.util.Collections;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;

import com.fitmeal.project.entity.User;
import com.fitmeal.project.repository.UserRepository;
import com.fitmeal.project.service.UserService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
	private final UserRepository userRepository;
	private final UserService userService;
	
	@Override
	@Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
    	OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
    	OAuth2User oAuth2User = delegate.loadUser(userRequest);
    	
    	// [진단 코드!] 어떤 데이터가 들어오는지, 우리 눈으로 직접 확인합시다.
        System.out.println("====================================================");
        System.out.println("LOGIN PLATFORM: " + userRequest.getClientRegistration().getRegistrationId());
        System.out.println("--- Attributes from Spring Security ---");
        oAuth2User.getAttributes().forEach((k, v) -> {
            System.out.println("KEY: " + k + ", VALUE: " + v.toString());
        });
        System.out.println("====================================================");
    	
    	String registrationId = userRequest.getClientRegistration().getRegistrationId();
    	String userNameAttributeName = userRequest.getClientRegistration()
    			.getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();
    	
    	OAuthAttributes attributes = OAuthAttributes.of(registrationId, userNameAttributeName, oAuth2User.getAttributes());
    	
    	
    	 // [진단 코드!] 진짜 이메일이 잘 들어오는지 확인
        System.out.println("====================================================");
        System.out.println("LOGIN PLATFORM: " + registrationId);
        System.out.println("USER REAL EMAIL: " + attributes.getEmail()); 
        System.out.println("====================================================");
        
        
        
        User user = userRepository.findByEmail(attributes.getEmail())
                .map(existingUser -> {
                    existingUser.update(attributes.getName(), attributes.getPicture());
                    return existingUser;
                })
                .orElseGet(() -> {
                    User newUser = attributes.toEntity();
                    // 태그 닉네임 적용
                    String taggedNickName = userService.generateTaggedNickName(attributes.getName());
                    newUser.updateNickName(taggedNickName); // Setter 대신 엔티티 메서드 사용
                    return newUser;
                });

        userRepository.save(user);
    	
    	return new DefaultOAuth2User(
    			Collections.singleton(new SimpleGrantedAuthority(user.getRole().getKey())),
    			attributes.getAttributes(),
    			attributes.getNameAttributeKey());
    }
    
//    private User saveOrUpdate(OAuthAttributes attributes) {
//    	User user = userRepository.findByEmail(attributes.getEmail())
//    			.map(entity -> entity.update(attributes.getName(), attributes.getPicture()))
//    			.orElse(attributes.toEntity());
//    	return userRepository.save(user);
//    }
}
