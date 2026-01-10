package com.fitmeal.project.entity;

import java.io.Serializable;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class MealFoodId implements Serializable {
	
	private static final long serialVersionUID = 528582842842L;
	
	private Long mealId;
	private Long foodId;

}
