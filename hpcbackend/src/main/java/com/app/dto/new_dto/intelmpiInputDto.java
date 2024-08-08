package com.app.dto.new_dto;

import java.util.ArrayList;

import lombok.Data;

@Data
public class intelmpiInputDto {

	    public String mpi_developement_version;
	    public String intel_mkl_version;
	    public String intel_icc_version;
	    public String intel_tbb_version;
	    public String base_image_name;
	    public String base_image_tag;


	    public String gitUrl;
	    public String app_image_name;
	    public String app_image_tag;
	    public String app_docker_commands;
	    public String dockeruser;
	    public String dockerpassword;
	    public String app_sin_image_name;
}
