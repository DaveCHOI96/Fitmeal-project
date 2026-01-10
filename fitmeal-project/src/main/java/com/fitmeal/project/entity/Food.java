package com.fitmeal.project.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "FOOD")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Food {
	
	@Id
	@Column(name = "FOOD_ID")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "food_seq_generator")
	@SequenceGenerator(name = "food_seq_generator", sequenceName = "SEQ_FOOD_ID", allocationSize = 1)
	private Long foodId;
	
	@Column(name = "FOOD_NAME", nullable = false, unique = true)
	private String foodName;
	
	@Column(name = "CATEGORY", nullable = false)
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
