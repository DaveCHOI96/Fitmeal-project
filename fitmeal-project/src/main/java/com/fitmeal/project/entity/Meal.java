package com.fitmeal.project.entity;

import java.time.LocalDateTime;

import com.fitmeal.project.common.BaseTimeEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "MEAL")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Meal extends BaseTimeEntity {
	
	public enum MealType {
		BREAKFAST, LUNCH, DINNER, SNACK
	}
	
	@Id
	@Column(name = "MEAL_ID")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "meal_seq_generator")
	@SequenceGenerator(name = "meal_seq_generator", sequenceName = "SEQ_MEAL_ID", allocationSize = 1)
	private Long mealId;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "USER_ID", nullable = false)
	private User user;
	
	@Column(name = "MEAL_NAME", nullable = false, length = 100)
	private String mealName;
	
	@Column(name = "IS_PUBLIC", nullable = false)
	private boolean published;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "MEAL_TYPE", nullable = false)
	private MealType mealType;
	
	@Column(name = "INTAKE_DATE", nullable = false)
	private LocalDateTime intakeDate;
	
	public void updateMealInfo(String mealName, boolean published, MealType mealType, LocalDateTime intakeDate) {
		this.mealName = mealName;
		this.published = published;
		this.mealType = mealType;
		this.intakeDate = intakeDate;
	}
	
	public void publish() {
		this.published = true;
	}
	
	public void unpublish() {
		this.published = false;
	}

}
