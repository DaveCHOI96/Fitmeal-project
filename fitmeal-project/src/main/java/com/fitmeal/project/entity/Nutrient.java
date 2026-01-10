package com.fitmeal.project.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "NUTRIENT")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Nutrient {
	
	@Id
	@Column(name = "NUTRIENT_ID")
	private Long nutrientId;
	
	@Column(name = "NUTRIENT_NAME", nullable = false, unique = true)
	private String nutrientName;
	
	@Column(name = "UNIT", nullable = false)
	private String unit;
	
	@Column(name = "DAILY_RECOMMENDED_AMOUNT")
	private BigDecimal dailyRecommendAmount;

}
