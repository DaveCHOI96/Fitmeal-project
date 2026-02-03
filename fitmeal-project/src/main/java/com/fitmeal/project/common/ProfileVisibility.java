package com.fitmeal.project.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProfileVisibility {
	PUBLIC("공개"),
	PRIVATE("비공개");
	
	private final String title;

}
