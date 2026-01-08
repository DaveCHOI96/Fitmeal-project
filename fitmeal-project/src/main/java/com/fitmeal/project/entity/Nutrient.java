package com.fitmeal.project.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "NUTRIENT")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Nutrient {
	
	@Id
	@Column(name = "NUTRIENT_ID")
	private Long nutrientId;
	
	@Column(name = "NUTRIENT_NAME")
	private String nutrientName;
	
	@Column(name = "UNIT")
	private String unit;
	
	@Column(name = "DAILY_RECOMMENDED_AMOUNT")
	private BigDecimal dailyRecommendAmount;

}
