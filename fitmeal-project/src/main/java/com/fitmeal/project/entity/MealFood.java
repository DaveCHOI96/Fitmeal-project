package com.fitmeal.project.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "MEAL_FOOD")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class MealFood {
	
	@EmbeddedId //복합키를 id로 사용
	private MealFoodId id;
	
	@MapsId("mealId")
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "MEAL_ID", nullable = false)
	private Meal meal;
	
	@MapsId("foodId")
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "FOOD_ID", nullable = false)
	private Food food;
	
	@Column(name = "AMOUNT", nullable = false)
	private Double amount;

}
