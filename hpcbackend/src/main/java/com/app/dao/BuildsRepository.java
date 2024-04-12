package com.app.dao;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.app.entities.Builds;


public interface BuildsRepository extends JpaRepository<Builds, Long>{
	
	@Query("select e from Builds e join fetch e.user where e.user.userId = ?1")
	List<Builds> findBuildByUser(Long user_id);
}
