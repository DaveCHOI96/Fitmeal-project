package com.fitmeal.project.entity;

import java.io.Serial;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fitmeal.project.common.BaseTimeEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name  = "PAYMENT")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Payment extends BaseTimeEntity {
	
	public enum PaymentMethod {
		CARD, KAKAO_PAY, NAVER_PAY
	}
	
	public enum PaymentStatus {
		PENDING, COMPLETED, FAILED
	}
	
	@Id
	@Column(name = "PAYMENT_ID")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "payment_seq_generator")
	@SequenceGenerator(name = "payment_seq_generator", sequenceName = "SEQ_PAYMENT_ID", allocationSize = 1)
	private Long paymentId;
	
	@Column(name = "ORDER_ID", nullable = false, unique = true)
	private String orderId;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "PAYMENT_METHOD")
	private PaymentMethod paymentMethod;
	
	@Column(name = "AMOUNT", nullable = false)
	private BigDecimal amount;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "STATUS", nullable = false)
	private PaymentStatus status;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "USER_ID", nullable = false)
	private User user;
	
	@Column(name = "PAID_AT")
	private LocalDateTime paidAt;
	
	

}
