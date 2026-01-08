package com.fitmeal.project.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.Getter;

@Entity
@Table(name ="FOOD_NUTRIENT")
@Getter
public class FoodNutrient {
	
	@EmbeddedId //다른 곳에 정의된 id 클래스를 포함
	private FoodNutrientId id;
	
	@ManyToOne(fetch = FetchType.LAZY) //관계 설정 FoodNutrient 입장에서 Food는 다:1
	@MapsId("foodId") //id클래스의 foodId 필드를 이 관계에 매핑
	@JoinColumn(name = "FOOD_ID")
	private Food food;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@MapsId("nutrientId")
	@JoinColumn(name = "NUTRIENT_ID")
	private Nutrient nutrient;
	
	@Column(name ="AMOUNT", nullable = false)
	private BigDecimal amount;

}
