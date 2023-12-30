package com.app.controller;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.app.dto.inputDataDto;



@RestController
@CrossOrigin("*")
@RequestMapping("/home")
public class HomeController {

	@PostMapping("/buildandpush")
	public ResponseEntity<?> getSecurity(@RequestBody inputDataDto inputData) {
	
		String jenkinsUrl = "http://localhost:8080"; 
        String jobName = "dockerbuild"; 
        String jenkinsuser= "admin";
        String jenkinsapitoken = "11336dc0a057016f575b0d7076bcbc5304";

        String apiUrl = jenkinsUrl + "/job/" + jobName + "/buildWithParameters";

        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setOutputStreaming(false);
        RestTemplate restTemplate = new RestTemplate(requestFactory);
//        RestTemplate restTemplate = new RestTemplate();

        // Prepare the request parameters
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(jenkinsuser, jenkinsapitoken);

        // Set the request body with environment variables
        MultiValueMap<String, String> map= new LinkedMultiValueMap<>();
        map.add("dockeruser", inputData.getDockeruser());
        map.add("dockerpassword", inputData.getDockerpassword());
        map.add("imagename", inputData.getImagename());
        map.add("imagetag", inputData.getImagetag());	
        map.add("dockerfile", String.join(",", inputData.getDockerfile()));

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, headers);

        // Trigger the Jenkins pipeline
        ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, entity, String.class);
        System.out.println("HTTP Status Code: " + response);

		return new ResponseEntity<>(inputData, response.getStatusCode());

	}
	

}
