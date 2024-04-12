package com.app.service;

import java.util.List;

import com.app.entities.Builds;

public interface IBuildService {
	List<Builds> getBuildList(Long build_id); 
}
