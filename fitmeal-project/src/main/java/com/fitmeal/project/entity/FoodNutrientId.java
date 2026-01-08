package com.fitmeal.project.entity;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class FoodNutrientId implements Serializable {
	
	private static final long serialVersionUID = 4285385835825L;
	
	private Long foodId;
	private Long nutrientId;
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null || getClass() != obj.getClass()) return false;
		FoodNutrientId that = (FoodNutrientId) obj;
		return Objects.equals(foodId, that.foodId) && Objects.equals(nutrientId, that.nutrientId);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(foodId, nutrientId);
	}

}
