package com.fitmeal.project.oauth;

import java.util.Map;
import java.util.UUID;

import com.fitmeal.project.common.UserRole;
import com.fitmeal.project.entity.User;

import lombok.Getter;
import lombok.Builder;

@Getter
public class OAuthAttributes {
	private Map<String, Object> attributes; // OAuth2 반환하는 유저 정보 Map
	private String nameAttributeKey;
	private String name;
	private String email;
	private String picture;
	private String provider;
	private String oauthId; // 각 플랫폼이 제공하는 고유 ID
	
	@Builder
	public OAuthAttributes(Map<String, Object> attributes, String nameAttributeKey, String name, String email, String picture, String provider, String oauthId) {
		this.attributes = attributes;
		this.nameAttributeKey = nameAttributeKey;
		this.name = name;
		this.email = email;
		this.picture = picture;
		this.provider = provider;
		this.oauthId = oauthId;
	}
	
	public static OAuthAttributes of(String registrationId, String userNameAttributeName, Map<String, Object> attributes) {
		// 여기서 네이버와 카카오 등 다른 소셜 로그인을 추가할 수 있습니다.
		if ("kakao".equals(registrationId)) {
			return ofKakao(userNameAttributeName, attributes);
		}
		if ("naver".equals(registrationId)) {
			return ofNaver("id", attributes);
		}
		return ofGoogle(userNameAttributeName, attributes); // 그 외에는 ofGoogle() 호출
	}
	
	private static OAuthAttributes ofGoogle(String userNameAttributeName, Map<String, Object> attributes) {
		return OAuthAttributes.builder()
				.name((String) attributes.get("name"))
				.email((String) attributes.get("email"))
				.picture((String) attributes.get("picture"))
				.provider("google")
				.oauthId((String) attributes.get("sub")) // 구글의 고유 ID는 'sub' 키에 들어있습니다.
				.attributes(attributes)
				.nameAttributeKey(userNameAttributeName)
				.build();
	}
	
	@SuppressWarnings("unchecked")
	private static OAuthAttributes ofKakao(String userNameAttributeName, Map<String, Object> attributes) {
		// 카카오가 주는 '사용자 정보'는, 'kakao_account' 라는 꾸러미 안에 들어있습니다.
		Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
		 // 그리고, 그 'kakao_account' 꾸러미 안에, 또 'profile' 이라는 꾸러미가 들어있습니다. (이중 포장)
		Map<String, Object> kakaoProfile = (Map<String, Object>) kakaoAccount.get("profile");
		
		return OAuthAttributes.builder()
				.name((String) kakaoProfile.get("nickname"))
				.email((String) kakaoAccount.get("email"))
				.picture((String) kakaoProfile.get("profile_image_url"))
				.provider("kakao")
				// 카카오의 고유 ID는, 'attributes' 전체 꾸러미의 'id' 키에, Long 타입으로 들어있습니다.
				.oauthId(String.valueOf(attributes.get("id")))
				.attributes(attributes)
				.nameAttributeKey(userNameAttributeName)
				.build();
	}
	
	@SuppressWarnings("unchecked")
	private static OAuthAttributes ofNaver(String userNameAttributeName, Map<String, Object> attributes) {
		// 네이버가 주는 정보는 'response'라는 꾸러미 안에 한번 더 포장되어 있습니다.
		Map<String, Object> response = (Map<String, Object>) attributes.get("response");
		
		if (response == null) {
			response = attributes;
		}
		return OAuthAttributes.builder()
				 // 네이버가 'name'으로 줄 수도, 'nickname'으로 줄 수도 있으니,
	            // 둘 다 확인해서, 있는 값을 사용하도록 수정합니다.
				.name((String) response.getOrDefault("name", (String) response.get("nickname")))
				.email((String) response.get("email"))
				.picture((String) response.getOrDefault("profile_image", (String) attributes.get("profile_image")))
				.provider("naver")
				.oauthId((String) response.get("id")) // 네이버의 고유 ID는 'id' 키에 들어있습니다.
				.attributes(response)
				.nameAttributeKey(userNameAttributeName)
				.build();
	}
	
	//toEntity() 메소드가, 이제 '이메일 중복 방지'라는 막중한 임무를 수행합니다.
	public User toEntity() {
//		String uniqueEmail = this.provider.toLowerCase() + "_" + this.oauthId + "@social.fitmeal";
		return User.builder()
				.nickName(this.name) // 구글이든 네이버든, 받은 name을 닉네임으로 사용
				.email(this.email) 
				.picture(this.picture)
				.password("SOCIAL_USER_PASSWORD_" + UUID.randomUUID().toString())
				.role(UserRole.USER)
				.provider(User.ProviderType.valueOf(this.provider.toUpperCase()))
				.providerId(this.oauthId)
				.level(1)
				.experience(0L)
				.build();
	}
	

}
