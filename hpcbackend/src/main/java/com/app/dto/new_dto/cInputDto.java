package com.app.dto.new_dto;

import java.util.ArrayList;

import lombok.Data;

@Data
public class cInputDto {
	public String gcc_version;

	public String gitUrl;    
	public String app_image_name;
	public String app_image_tag ; 
	public String app_docker_commands  ;      
	public String dockeruser ;    
	public String dockerpassword;
	public String app_sin_image_name;


}
