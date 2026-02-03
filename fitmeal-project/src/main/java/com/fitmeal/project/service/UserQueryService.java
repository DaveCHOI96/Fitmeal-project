package com.fitmeal.project.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fitmeal.project.common.ProfileVisibility;
import com.fitmeal.project.common.UserRole;
import com.fitmeal.project.dto.UserSearchDto;
import com.fitmeal.project.entity.User;
import com.fitmeal.project.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserQueryService {
	
	private final UserRepository userRepository;
	
	public List<UserSearchDto> searchByNickname(
			String nickName,
			UserRole role) {
		
		List<User> users;
		
		if (role == UserRole.ADMIN) {
			users = userRepository.findByNickNameContaining(nickName);
		} else {
			users = userRepository.findByNickNameContainingAndProfileVisibility(nickName, ProfileVisibility.PUBLIC);
		}
		return users.stream()
				.map(u -> new UserSearchDto(u.getUserId(), u.getNickName()))
				.toList();
				
	}
	

}
