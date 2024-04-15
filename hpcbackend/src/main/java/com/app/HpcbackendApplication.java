package com.app;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.app.config.WebSecurityConfig;

@SpringBootApplication
public class HpcbackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(HpcbackendApplication.class, args);
	}
	
	@Bean
	public ModelMapper mapper()
	{
	 ModelMapper modelMapper = new ModelMapper();
	 modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
	 return modelMapper;
	}

}
