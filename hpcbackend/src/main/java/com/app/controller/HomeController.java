package com.app.controller;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.tomcat.util.json.JSONParser;
import org.apache.tomcat.util.json.ParseException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.GsonJsonParser;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

import com.app.dto.buildResponse;
import com.app.dto.inputDataDto;
import com.app.dto.responseDto;
import com.app.dto.statusDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.JSONPObject;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import jakarta.websocket.server.PathParam;



@RestController
@CrossOrigin("*")
@RequestMapping("/home")
public class HomeController {
	
	@Autowired
	private ModelMapper mapper;
	
	private static final String UPLOAD_DIR = "C:"+File.separator+"ProgramData"+File.separator+"zipfiles";
	
	@GetMapping("/test")
	public String test() {
		return "Test";
	}
	
	@PostMapping("/buildandpush")
	public ResponseEntity<?> getSecurity(
			@RequestPart("inputData") String inputData,
			@RequestPart("file") MultipartFile file) {
		
		
		
		inputDataDto inputDataDetails = null;
		ObjectMapper objectMapper = new ObjectMapper();
        try {
			inputDataDetails = objectMapper.readValue(inputData, inputDataDto.class);
		} catch (JsonMappingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (JsonProcessingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		
//		inputDataDto inputData = new inputDataDto();
		
//		System.out.println(inputDataString);
		
//		Gson gson = new Gson();    
//		inputDataDto jsonObject = null;
//		mapper.map(jsonObject, inputData);
		
		System.out.println(inputDataDetails);
		
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
            
            
            String fileName = inputDataDetails.getDockerfilename();

            // Create the Dockerfile content by joining array elements with new lines
            String dockerfileContentString = String.join(System.lineSeparator(), inputDataDetails.getDockerfile());

            // Create and save the Dockerfile
            createAndSaveDockerfile(UPLOAD_DIR, fileName, dockerfileContentString);
            
            String fileNamebase = inputDataDetails.getBasedockerfilename();

            // Create the Dockerfile content by joining array elements with new lines
            String dockerfileContentStringbase = String.join(System.lineSeparator(), inputDataDetails.getBasedockerfile());

            // Create and save the Dockerfile
            createAndSaveDockerfile(UPLOAD_DIR, fileNamebase, dockerfileContentStringbase);
            
      

            MultiValueMap<String, String> map= new LinkedMultiValueMap<>();
            map.add("dockeruser", inputDataDetails.getDockeruser());
            map.add("dockerpassword", inputDataDetails.getDockerpassword());
            map.add("imagename", inputDataDetails.getImagename());
            map.add("imagetag", inputDataDetails.getImagetag());	
            map.add("dockerfile", String.join(",", inputDataDetails.getDockerfile()));
            map.add("DOCKERFILE_NAME", inputDataDetails.getDockerfilename());
            map.add("DOCKER_BUILD_COMMAND", inputDataDetails.getBuildcommand());
            map.add("baseimagename", inputDataDetails.getBaseimagename());
            map.add("baseimagetag", inputDataDetails.getBaseimagetag());	
            map.add("basedockerfile", String.join(",", inputDataDetails.getBasedockerfile()));
            map.add("BASE_DOCKERFILE_NAME", inputDataDetails.getBasedockerfilename());
            map.add("BASE_DOCKER_BUILD_COMMAND", inputDataDetails.getBasebuildcommand());
            map.add("BASE_PATH", UPLOAD_DIR);
            map.add("ZIP_FILE_NAME", file.getOriginalFilename());

            HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, headers);

            // Trigger the Jenkins pipeline
            
            try {
            ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, entity, String.class);
            System.out.println("HTTP Status Code: " + response);
            String location = response.getHeaders().getLocation().toString();

            String[] parts = location.split("/");
            String itemNumber = parts[parts.length - 1];
            
            String queueItemUrl = "http://localhost:8080/queue/item/" + itemNumber + "/api/json";
//            ResponseEntity<String> queueItemResponse = restTemplate.getForEntity(queueItemUrl, String.class);
//            String queueItemBody = queueItemResponse.getBody().toString();
            
//            System.out.println(queueItemBody + "hii");
//            System.out.println(queueItemUrl);
            
            int maxRetries = 10;  // Maximum number of retries
            int retryInterval = 5000;  // Interval between retries in milliseconds

            int retries = 0;
 
            String queueItemBody = null;
            while (retries < maxRetries) {
                // Poll the /queue/item/71/api/json endpoint to get build details
                ResponseEntity<String> queueItemResponse = restTemplate.getForEntity(queueItemUrl, String.class);
                queueItemBody = queueItemResponse.getBody();
                
                System.out.println(queueItemBody);

                // Check if "executable" field is present in the JSON response
                if (queueItemBody.contains("executable")) {
                	System.out.println(queueItemBody);
                    break;  // Exit the loop if the build has started running
                }

                // Wait before the next retry
                try {
                    Thread.sleep(retryInterval);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                retries++;
            }
            
            int buildId = 0;

            if (retries == maxRetries) {
                // Handle the case where the build didn't start within the specified retries
                System.out.println("Build did not start within the specified retries.");
            } else {
                // Build has started running, you can now extract the build ID
                buildId = parseBuildIdFromJson(queueItemBody);
                System.out.println("Build ID: " + buildId);
            }
         
            buildResponse buildResp = new buildResponse();
            buildResp.setBuildId(buildId);
    		return new ResponseEntity<>(buildResp, response.getStatusCode());
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
	
	private int parseBuildIdFromJson(String jsonResponse) {
  
	    
	    responseDto jsonObject = null;
		ObjectMapper objectMapper = new ObjectMapper();
        try {
        	jsonObject = objectMapper.readValue(jsonResponse, responseDto.class);
        	System.out.println(jsonObject.toString());
        	
		} catch (JsonMappingException e1) {
			// TODO Auto-generated catch block
			System.out.println("deserialize error");
		} catch (JsonProcessingException e1) {
			// TODO Auto-generated catch block
			System.out.println("JsonProcessingException");
		}
	    return jsonObject.getExecutable().getNumber();
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
	
	@GetMapping("/getStatus/{buildId}")
	public ResponseEntity<?> getStatus(@PathVariable("buildId") Integer buildId) {
		
		String url = "http://localhost:8080/job/dockerbuild/" + buildId +"/api/json";
		
		
		SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setOutputStreaming(false);
        RestTemplate restTemplate = new RestTemplate(requestFactory);
		
	    ResponseEntity<String> statusResponse = restTemplate.getForEntity(url, String.class);
		System.out.println(statusResponse);
		statusDto statusObject = null;
		ObjectMapper objectMapper = new ObjectMapper();
        try {
        	statusObject = objectMapper.readValue(statusResponse.getBody(), statusDto.class);
        	System.out.println(statusObject.toString());
        	
		} catch (JsonMappingException e1) {
			// TODO Auto-generated catch block
			System.out.println("deserialize error");
		} catch (JsonProcessingException e1) {
			// TODO Auto-generated catch block
			System.out.println("JsonProcessingException");
		}
        String result = statusObject.getResult();
		if ("SUCCESS".equals(result)) {
		    return new ResponseEntity<>("SUCCESS", HttpStatus.OK);
		} else if ("FAILURE".equals(result)) {
		    return new ResponseEntity<>("FAILURE", HttpStatus.OK);
		} else if ("UNSTABLE".equals(result)) {
		    return new ResponseEntity<>("UNSTABLE", HttpStatus.OK);
		} else if (result == null) {
		    return new ResponseEntity<>("INPROGRESS", HttpStatus.OK);
		} else {
		    return new ResponseEntity<>("Unknown result: " + result, HttpStatus.OK);
		}
		
	}

}
