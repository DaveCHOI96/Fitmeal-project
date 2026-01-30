package com.fitmeal.project.entity;

import java.time.LocalDate;

import com.fitmeal.project.common.BaseTimeEntity;
import com.fitmeal.project.common.UserRole;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "FM_USER")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class User extends BaseTimeEntity {
	
	public enum ProviderType {
		LOCAL, KAKAO, NAVER, GOOGLE
	}
	
	@Id
	@Column(name = "USER_ID")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq_generator")
	@SequenceGenerator(name = "user_seq_generator", sequenceName = "SEQ_USER_ID", allocationSize = 1)
	private Long userId;
	
	@Column(name = "EMAIL", nullable = false, unique = true)
	private String email;
	
	@Column(name = "PASSWORD", nullable = false)
	private String password;
	
	@Column(name = "NICKNAME", nullable = false, unique = true)
	private String nickName;
	
	@Column
	private String picture;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "USER_ROLE", nullable = false)
	private UserRole role;
	
	@Column(name = "USER_LEVEL", nullable = false)
	private Integer level;
	
	@Column(name = "USER_EXPERIENCE", nullable = false)
	private Long experience;
	
	@Column(name = "GENDER")
	private String gender;
	
	@Column(name = "BIRTH_DATE")
	private LocalDate birthDate;
	
	@Column(name = "HEIGHT")
	private Double height;
	
	@Column(name = "WEIGHT")
	private Double weight;
	
	@Column(name = "ACTIVITY_LEVEL")
	private String activityLevel;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "PROVIDER", nullable = false)
	private ProviderType provider;
	
	@Column(name = "PROVIDER_ID")
	private String providerId;
	
	@Column(length = 1000)
	private String refreshToken;
	
	public void updateProfile(String nickName, Double height, Double weight, String activityLevel) {
		this.nickName = nickName;
		this.height = height;
		this.weight = weight;
		this.activityLevel = activityLevel;
	}
	
	public void addExperience(Long exp) {
		this.experience += exp;
	}
	
	public void updateRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}
	
	public User update(String name, String picture) {
		this.nickName = name;
		this.picture = picture;
		return this;
	}
	
	// 새로 추가: 닉네임 태그 업데이트
    public void updateNickName(String taggedNickName) {
        this.nickName = taggedNickName;
    }
    
    public void updatePassword(String encodedPassword) {
    	this.password = encodedPassword;
    }

}