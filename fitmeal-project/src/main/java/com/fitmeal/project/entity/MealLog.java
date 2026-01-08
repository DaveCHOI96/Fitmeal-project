package com.fitmeal.project.entity;

import java.time.LocalDateTime;

import com.fitmeal.project.common.BaseTimeEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "MEAL_LOG")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MealLog extends BaseTimeEntity {
	
	@Id
	@Column(name = "MEAL_LOG_ID")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "meal_log_seq_generator")
	@SequenceGenerator(name = "meal_log_seq_generator", sequenceName = "SEQ_MEAL_LOG_ID")
	private Long mealLogId;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "USER_ID")
	private User user;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "FOOD_ID")
	private Food food;
	
	@Column(name = "INTAKE_AMOUNT")
	private Long intakeAmount;
	
	@Column(name = "MEAL_TYPE")
	private String mealType;
	
	@Column(name = "INTAKE_DATE", nullable = false)
	private LocalDateTime intakeDate;

}
