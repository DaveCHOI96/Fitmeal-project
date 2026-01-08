package com.fitmeal.project.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "FOOD")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Food {
	
	@Id
	@Column(name = "FOOD_ID")
	private Long foodId;
	
	@Column(name = "FOOD_NAME")
	private String foodName;
	
	@Column(name = "CATEGORY")
	private String category;
	
	@Column(name = "ALLERGY_INFO")
	private String allergyInfo;
	
	@Lob
	@Column(name = "BENEFITS")
	private String benefits;
	
	@Lob
	@Column(name = "SIDE_EFFECTS")
	private String sideEffects;

}
