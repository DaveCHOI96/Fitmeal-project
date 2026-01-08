package com.fitmeal.project.common;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;

@Getter
@MappedSuperclass //이 클래스를 다른 Entity의 공통 매핑 정보로만 사용한다
@EntityListeners(AuditingEntityListener.class) //Auditing 기능 클래스에 적용
public abstract class BaseTimeEntity {
	
	@CreatedDate
	@Column(name = "CREATED_DATE", updatable = false) //업데이트 불가 설정
	private LocalDateTime createDate;
	
	@LastModifiedDate //데이터 수정 시각 자동 저장
	@Column(name = "UPDATED_DATE")
	private LocalDateTime updatedDate;
}
