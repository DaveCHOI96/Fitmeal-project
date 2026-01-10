package com.fitmeal.project.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "FOOD_COMBINATION")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class FoodCombination {
	
	@Id
	@Column(name = "FOOD_COMBINATION_ID")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "combination_seq_generator")
	@SequenceGenerator(name = "combination_seq_generator", sequenceName = "SEQ_FOOD_COMBINATION_ID", allocationSize = 1)
	private Long combinationId;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "FOOD_A_ID", nullable = false)
	private Food foodA;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "FOOD_B_ID", nullable = false)
	private Food foodB;
	
	@Column(name = "COMBINATION_TYPE", nullable = false, length = 30)
	private String combinationType;
	
	@Lob
	@Column(name = "DESCRIPTION")
	private String description;

}
