package com.fitmeal.project.security;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fitmeal.project.common.UserRole;
import com.fitmeal.project.entity.User;

public class CustomUserDetails implements UserDetails {
	
	private final Long userId;
	private final String email;
	private final UserRole role;
	
	public CustomUserDetails(User user) {
		this.userId = user.getUserId();
		this.email = user.getEmail();
		this.role = user.getRole();
	}
	
	public Long getUserId() {
		return userId;
	}
	
	public UserRole getRole() {
		return role;
	}
	
	@Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.getKey()));
    }

    @Override public String getUsername() { return email; }
    @Override public String getPassword() { return null; }
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}


