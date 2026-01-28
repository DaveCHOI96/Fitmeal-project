package com.fitmeal.project.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fitmeal.project.oauth.CustomOAuth2UserService;
import com.fitmeal.project.oauth.OAuth2SuccessHandler;

import lombok.RequiredArgsConstructor;
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
	
	private final JwtTokenProvider jwtTokenProvider;
	private final CustomOAuth2UserService customOAuth2UserService;
	private final OAuth2SuccessHandler oAuth2SuccessHandler;
	
//	@Bean
//	public PasswordEncoder passwordEncoder() {
//		return new BCryptPasswordEncoder();
//	}
	
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
		//CSRF(Cross-Site Request Forgery ) 보호 비활성화
		.csrf(csrf -> csrf.disable())
		.httpBasic(httpBasic -> httpBasic.disable())
		.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
		.authorizeHttpRequests(authz -> authz
				.requestMatchers("/", "/login/**", "/oauth2/**").permitAll()
				.requestMatchers("/api/users/signup", "/api/users/login", "/api/auth/reissue").permitAll()
				.requestMatchers("/api/users/email/**").permitAll()
				.requestMatchers("/api/admin/**").hasAuthority("ROLE_ADMIN")
				.anyRequest().hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")
				)
		        .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class)
		        .oauth2Login(oauth2 -> oauth2
		        		.userInfoEndpoint(userInfo -> userInfo
		        				.userService(customOAuth2UserService)
		        		)
		        		.successHandler(oAuth2SuccessHandler)
		        );
		return http.build();
		
		
	}

}
