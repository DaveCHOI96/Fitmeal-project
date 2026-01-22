package com.fitmeal.project.config;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.fitmeal.project.entity.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;



@Component
public class JwtTokenProvider {
	
	private final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);
	private final SecretKey secretkey;
	private final long accessTokenValidityMilliseconds;
	private final long refreshTokenValidityMilliseconds;
	
	public JwtTokenProvider(@Value("${jwt.secret}") String secret) {
		this.secretkey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
		this.accessTokenValidityMilliseconds = 60 * 60 * 1000;
		this.refreshTokenValidityMilliseconds = 14 * 24 * 60 * 60 * 1000L;
	}
	
	//User 정보 받아 Access Token 생성
	public String createAccessToken(User user) {
		Date now = new Date();
		Date validity = new Date(now.getTime() + accessTokenValidityMilliseconds);
		
		return Jwts.builder()
				.claims()
				.subject(user.getUserId().toString())
				.issuedAt(now)
				.expiration(validity)
				.add("email", user.getEmail())
				.add("role", user.getRole().toString())
				.and()
				.signWith(secretkey)
				.compact();
	}
	
	public String createRefreshToken() {
		Date now = new Date();
		Date validity = new Date(now.getTime() + refreshTokenValidityMilliseconds);
		
		return Jwts.builder()
				.issuedAt(now)
				.expiration(validity)
				.signWith(secretkey)
				.compact();
	}
	
	public Authentication getAuthentication(String accessToken) {
		// 토큰을 복호화하여 클레임(정보 묶음)을 가져옵니다.
		Claims claims = parseClaims(accessToken);
		
		if (claims.get("role") == null) {
			throw new RuntimeException("권한 정보가 없는 토큰입니다.");
		}
		// 클레임에서 권한 정보 가져오기
		Collection<? extends GrantedAuthority> authorities =
				Arrays.stream(claims.get("role").toString().split(","))
				.map(SimpleGrantedAuthority::new)
				.collect(Collectors.toList());
		
		// UserDetails 객체를 만들어서 Authentication 리턴
        // 여기서 User는 Spring Security의 UserDetails 인터페이스를 구현한 User 객체입니다.
		UserDetails principal = new org.springframework.security.core.userdetails.User(claims.getSubject(), "", authorities);
		return new UsernamePasswordAuthenticationToken(principal, "", authorities);
	}
	
	public boolean validateToken(String token) {
		try {
			Jwts.parser().verifyWith(secretkey).build().parseSignedClaims(token);
			return true;
		} catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
			logger.info("잘못된 JWT 서명입니다.");
		} catch (ExpiredJwtException e) {
			logger.info("만료된 JWT 토큰입니다.");
		} catch (UnsupportedJwtException e) {
			logger.info("지원되지 않는 JWT 토큰입니다.");
		} catch (IllegalArgumentException e) {
			logger.info("JWT 토큰이 잘못되었습니다.");
		}
		return false;
	}
	
	private Claims parseClaims(String accessToken) {
		try {
			return Jwts.parser().verifyWith(secretkey).build().parseSignedClaims(accessToken).getPayload();
		} catch (ExpiredJwtException e) {
			return e.getClaims();
		}
	}

}
