package com.app.dto.new_dto;

import java.util.ArrayList;

import lombok.Data;
@Data
public class mpichInputDto {
	public String mpich_version;
	public String mpi_configure_options;
	public String mpi_make_options;
	public String user;
	public String work_dir;
	public String base_image_name;
	public String base_image_tag;
	public String gitUrl;

    
	
	public String app_image_name;
	public String app_image_tag;
	public ArrayList<String> app_docker_commands;
    public String dockeruser;
    public String dockerpassword;
    public String app_sin_image_name;
}
