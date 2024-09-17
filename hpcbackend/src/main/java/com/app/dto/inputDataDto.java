package com.app.dto;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class inputDataDto {
	
	public String dockeruser;
	public String dockerpassword;
	public String imagename;
	public String imagetag;
	public List<String> dockerfile;
	public String buildcommand;
	public String dockerfilename;
	
//	public String baseimagename;
//	public String baseimagetag;
//	public List<String> basedockerfile;
//	public String basebuildcommand;
//	public String basedockerfilename;

}
