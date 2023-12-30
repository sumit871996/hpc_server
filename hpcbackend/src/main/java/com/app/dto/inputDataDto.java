package com.app.dto;

import java.time.LocalDateTime;
import java.util.List;

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

}
