package com.fitmeal.project.entity;

import java.time.LocalDate;

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
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "DAILY_ACTIVITY_LOG")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class DailyActivityLog {
	
	@Id
	@Column(name = "ACTIVITY_LOG_ID")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "activity_seq_generator")
	@SequenceGenerator(name = "activity_seq_generator", sequenceName = "SEQ_ACTIVITY_LOG_ID", allocationSize = 1)
	private Long activityId;
	
	@Column(name = "LOG_DATE", nullable = false)
	private LocalDate logDate;
	
	@Column(name = "STEPS", nullable = false)
	private int steps;
	
	@Column(name = "BURNED_CALORIES", nullable = false)
	private double burnedCalories;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "USER_ID")
	private User user;

}
