package com.fitmeal.project.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fitmeal.project.entity.User;
import com.fitmeal.project.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin" )
@RequiredArgsConstructor
public class AdminController {
	
	private final UserService userService;
	
	@GetMapping("/users")
	public ResponseEntity<List<User>> getUsersForAdmin() {
		List<User> users = userService.findAllUsers();
		return ResponseEntity.ok(users);
	}

}
