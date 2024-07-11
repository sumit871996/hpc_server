package com.app.service;

import java.util.List;

import com.app.entities.Builds;
import com.app.entities.buildStatusEnum;

public interface IBuildService {
	List<Builds> getBuildList(Long build_id); 
	Builds updateStatus(Long build_id,String status);
}
