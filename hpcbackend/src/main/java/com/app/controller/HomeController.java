package com.app.controller;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.app.dto.inputDataDto;



@RestController
@CrossOrigin("*")
@RequestMapping("/home")
public class HomeController {
	
	private static final String UPLOAD_DIR = "C:"+File.separator+"ProgramData"+File.separator+"zipfiles";
	@PostMapping("/buildandpush")
	public ResponseEntity<?> getSecurity(
			@RequestPart("inputData") inputDataDto inputData,
			@RequestPart("file") MultipartFile file) {
		
		System.out.println(inputData);
		
		String jenkinsUrl = "http://localhost:8080"; 
        String jobName = "dockerbuild"; 
        String jenkinsuser= "admin";
        String jenkinsapitoken = "115e0432d1d42d261d33f6efcf1e8d51c6";

        String apiUrl = jenkinsUrl + "/job/" + jobName + "/buildWithParameters";

        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setOutputStreaming(false);
        RestTemplate restTemplate = new RestTemplate(requestFactory);
//        RestTemplate restTemplate = new RestTemplate();

        // Prepare the request parameters
        HttpHeaders headers = new HttpHeaders();

        headers.setBasicAuth(jenkinsuser, jenkinsapitoken);
        
        
        
        
        
        
        
        if (file.isEmpty()) {
            return new ResponseEntity<>("File is empty", HttpStatus.BAD_REQUEST);
        }
		
		try {
            // Get the file bytes
            byte[] bytes = file.getBytes();

            // Create the directory if it doesn't exist
            File uploadDir = new File(UPLOAD_DIR);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }
            
            System.out.println(UPLOAD_DIR);

            // Save the file
            Path path = Paths.get(UPLOAD_DIR + File.separator + file.getOriginalFilename());
            System.out.println(java.nio.file.Files.write(path, bytes));
            
            
            String fileName = inputData.getDockerfilename();

            // Create the Dockerfile content by joining array elements with new lines
            String dockerfileContentString = String.join(System.lineSeparator(), inputData.getDockerfile());

            // Create and save the Dockerfile
            createAndSaveDockerfile(UPLOAD_DIR, fileName, dockerfileContentString);
            
            
            
      

            MultiValueMap<String, String> map= new LinkedMultiValueMap<>();
            map.add("dockeruser", inputData.getDockeruser());
            map.add("dockerpassword", inputData.getDockerpassword());
            map.add("imagename", inputData.getImagename());
            map.add("imagetag", inputData.getImagetag());	
            map.add("dockerfile", String.join(",", inputData.getDockerfile()));
            map.add("DOCKERFILE_NAME", inputData.getDockerfilename());
            map.add("DOCKER_BUILD_COMMAND", inputData.getBuildcommand());
            map.add("BASE_PATH", UPLOAD_DIR);
            map.add("ZIP_FILE_NAME", file.getOriginalFilename());

            HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, headers);

            // Trigger the Jenkins pipeline
            
            try {
            ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, entity, String.class);
            System.out.println("HTTP Status Code: " + response);
            
            
            

    		return new ResponseEntity<>(inputData, response.getStatusCode());
            }
            catch(RestClientException e) {
            	e.printStackTrace();
                return new ResponseEntity<>("Failed to run pipeline", HttpStatus.INTERNAL_SERVER_ERROR);
            	
            }
            
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>("Failed to upload the file", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // Set the request body with environment variables
        

	}
	
	private static void createAndSaveDockerfile(String directoryPath, String fileName, String content) {
        File directory = new File(directoryPath);

        // Create the directory if it doesn't exist
        if (!directory.exists()) {
            directory.mkdirs();
        }

        File dockerfile = new File(directory, fileName);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(dockerfile))) {
            // Write the content to the file
            writer.write(content);
            System.out.println("Dockerfile created and saved successfully.");
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to create and save the Dockerfile.");
        }
    }

}
