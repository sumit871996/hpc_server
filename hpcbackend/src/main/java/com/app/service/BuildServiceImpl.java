package com.app.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.dao.BuildsRepository;
import com.app.entities.Builds;

@Service
@Transactional
public class BuildServiceImpl implements IBuildService {

	@Autowired
	private BuildsRepository buildsRepo;
	
	@Override
	public List<Builds> getBuildList(Long build_id) {
		
		 List<Builds> buildList= buildsRepo.findBuildByUser(build_id);
		
		return buildList;
	}

}
