package com.fitmeal.project.entity;

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
@Table(name = "POST")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Post extends BaseTimeEntity {
	
	public enum BoardType {
		FREE_BOARD,
		QNA_BOARD,
		INFO_BOARD
	}
	
	@Id
	@Column(name = "POST_ID")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "post_seq_generator")
	@SequenceGenerator(name = "post_seq_generator", sequenceName = "SEQ_POST_ID", allocationSize = 1)
	private Long postId;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "USER_ID", nullable = false)
	private User user;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "BOARD_TYPE", nullable = false)
	private BoardType boardType;
	
	@Column(name = "TITLE", nullable = false)
	private String title;
	
	@Lob
	@Column(name = "CONTENT")
	private String content;
	
	@Column(name = "IMAGE_URL")
	private String imageUrl;
	
	public void updatePost(String title, String content, String imageUrl) {
		this.title = title;
		this.content = content;
		this.imageUrl = imageUrl;
	}

}
