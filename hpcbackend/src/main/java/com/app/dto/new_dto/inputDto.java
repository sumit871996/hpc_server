package com.app.dto.new_dto;

import org.springframework.web.multipart.MultipartFile;

import lombok.Getter;
@Getter
public class inputDto {

	private String useCaseId;
	
//	public commonInputDto commonInput;
	
	private String useCaseInput;
	
	private MultipartFile file;
	
	private String gitUrl;
	
}
