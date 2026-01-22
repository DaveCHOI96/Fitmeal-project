package com.fitmeal.project.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.fitmeal.project.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
	@Query("""
		    select count(u) > 0
		    from User u
		    where u.email = :email
		""")
	boolean existsByEmail(@Param("email") String email);
	
	@Query("""
		    select case when count(u) > 0 then true else false end
		    from User u
		    where u.nickName = :nickName
		""")
    boolean existsByNickName(@Param("nickName") String nickName);
	
	Optional<User> findByEmail(String email);
	
	Optional<User> findByRefreshToken(String refreshToken);

}
