package com.app.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.app.entities.Users;
public interface UsersRepository extends JpaRepository<Users, Long>{

	@Query("select e from Users e where username = ?1")
	Optional<Users> findByUsername(String user);
}