package com.app.dto;

import lombok.Getter;

@Getter
public class UseCaseInputDto {

	public String gitUrl;
	public String imagename;
	public String imagetag;
	public String dockerfileName;
	public String dockerfilePath;
	public String dockeruser;
	public String dockerpassword;
	public String dockerregistry;
	
}
