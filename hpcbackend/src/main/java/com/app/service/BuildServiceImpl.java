package com.app.service;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.dao.BuildsRepository;
import com.app.entities.Builds;
import com.app.entities.buildStatusEnum;

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

	@Override
	public Builds updateStatus(Long build_id, String status) {
	    Builds build = buildsRepo.findById(build_id)
	                            .orElseThrow(() -> new NoSuchElementException("Build with ID " + build_id + " not found"));
	    if(status==null) {
	    	status="INPROGRESS";
	    }
	    status = status.toUpperCase(); // Convert status to upper case for case-insensitive comparison

	    if (status.equals("FAILURE")) {
	        build.setFinalBuildStatus(buildStatusEnum.FAILURE);
	    } else if (status.equals("SUCCESS")) {
	        build.setFinalBuildStatus(buildStatusEnum.SUCCESS);
	    } else if (status.equals("UNSTABLE")) {
	        build.setFinalBuildStatus(buildStatusEnum.UNSTABLE);
	    } else if (status.equals("INPROGRESS")) {
	        build.setFinalBuildStatus(buildStatusEnum.INPROGRESS);
	    } else {
	        // Handle invalid status here if needed
	    }

	    return buildsRepo.save(build);
	}
}
