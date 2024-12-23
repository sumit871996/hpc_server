package com.app.controller;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.app.dao.BuildsRepository;
import com.app.dto.buildResponse;
import com.app.dto.inputDataDto;
import com.app.dto.responseDto;
import com.app.dto.new_dto.cInputDto;
import com.app.dto.new_dto.cppInputDto;
import com.app.dto.new_dto.intelmpiInputDto;
import com.app.dto.new_dto.mpichInputDto;
import com.app.dto.new_dto.openmpiInputDto;
import com.app.dto.new_dto.reactInputDto;
import com.app.entities.Builds;
import com.app.entities.Users;
import com.app.entities.buildStatusEnum;
import com.app.entities.UseCases.UseCasesEnum;
import com.app.entities.UseCases.UseCasesEnumBean;
import com.app.utils.CommonUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;

@RestController
@CrossOrigin("*")
@RequestMapping("/form")
public class UseCaseController {
	
//@Autowired
//public getUseCaseServiceImpl getusecaseService;
//private static final String UPLOAD_DIR = "C:"+File.separator+"ProgramData"+File.separator+"zipfiles";
//
//@Autowired
//private ModelMapper mapper;
	@Autowired
	private BuildsRepository buildsRepo;
@Value("${file.upload.folder}") // Injecting the value of app.greeting from application.properties
private String file;

private static final String UPLOAD_DIR = "C:" + File.separator + "ProgramData" + File.separator + "zipfiles";

@Autowired
private ModelMapper mapper;


@Autowired
private UseCasesEnumBean useCasesEnumBean;

@GetMapping("/getusecases")  // get all usecases available
public ResponseEntity<?> getusecaseInfo()
{
	
	HashMap<Integer, String> hm = new HashMap<Integer,String>();
	for(UseCasesEnum env : UseCasesEnum.values())
	hm.put(env.getUseCaseId(), env.toString());
	return new ResponseEntity<>(hm,HttpStatus.OK);
}


@GetMapping("/getusecases/{useCaseId}")  // get rjsf schema
public ResponseEntity<?> getusecaseDetails(@PathVariable("useCaseId") Integer useCaseId)
{
	String jsonfilename = getFileName(useCaseId, "json");
	if(jsonfilename==null)
	return new ResponseEntity<>("Invalid ID", HttpStatus.NOT_ACCEPTABLE);
	
	try {
		Path filepath = Paths.get(file,"json",jsonfilename);
		Resource resource = new UrlResource(filepath.toUri());
		if(resource.exists() || resource.isReadable()) 
		{
         return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
		}
        else
        {
          return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
	}
	catch(IOException e) {
		return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
	}
}

@PostMapping("/buildandpush")

public ResponseEntity<?> getSecurity(
		@RequestPart("inputData") String inputData,
		@RequestPart("file") MultipartFile file
		) {
	
	System.out.println(file);
	
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
	
	
	System.out.println(inputDataDetails);
	
	String jenkinsUrl = "http://localhost:8080"; 
    String jobName = "dockerbuildimage"; 
    String jenkinsuser= "admin";
    String jenkinsapitoken = "112e4c7995f6b151471d2049e88430d707";

    String apiUrl = jenkinsUrl + "/job/" + jobName + "/buildWithParameters";

    SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
    requestFactory.setOutputStreaming(false);
    RestTemplate restTemplate = new RestTemplate(requestFactory);
    HttpHeaders headers = new HttpHeaders();
    headers.setBasicAuth(jenkinsuser, jenkinsapitoken);
    if (file.isEmpty()) {
        return new ResponseEntity<>("File is empty", HttpStatus.BAD_REQUEST);
    }
	
	try {
        byte[] bytes = file.getBytes();
        File uploadDir = new File(UPLOAD_DIR);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }
        
        System.out.println(UPLOAD_DIR);
        String fn = inputDataDetails.getImagename()+ inputDataDetails.getImagetag()+".zip";
        Path path = Paths.get(UPLOAD_DIR + File.separator + fn);
        System.out.println(java.nio.file.Files.write(path, bytes));
        
        
        String fileName = inputDataDetails.getDockerfilename();

        // Create the Dockerfile content by joining array elements with new lines
        String dockerfileContentString = String.join(System.lineSeparator(), inputDataDetails.getDockerfile());

        // Create and save the Dockerfile
        createAndSaveDockerfile(UPLOAD_DIR, fileName, dockerfileContentString);
        
//        String fileNamebase = inputDataDetails.getBasedockerfilename();

        // Create the Dockerfile content by joining array elements with new lines
//        String dockerfileContentStringbase = String.join(System.lineSeparator(), inputDataDetails.getBasedockerfile());

        // Create and save the Dockerfile
//        createAndSaveDockerfile(UPLOAD_DIR, fileNamebase, dockerfileContentStringbase);
        
  
//        System.out.println(inputDataDetails.getImagetag());
//        System.out.println(inputDataDetails.getBaseimagetag());
        System.out.println(inputDataDetails.getBuildcommand());
//        System.out.println(inputDataDetails.getBasebuildcommand());
        MultiValueMap<String, String> map= new LinkedMultiValueMap<>();
        map.add("dockeruser", inputDataDetails.getDockeruser());
        map.add("dockerpassword", inputDataDetails.getDockerpassword());
        map.add("imagename", inputDataDetails.getImagename());
        map.add("imagetag", inputDataDetails.getImagetag());	
        map.add("dockerfile", String.join(",", inputDataDetails.getDockerfile()));
        map.add("DOCKERFILE_NAME", inputDataDetails.getDockerfilename());
        map.add("DOCKER_BUILD_COMMAND", inputDataDetails.getBuildcommand());
//        map.add("baseimagename", inputDataDetails.getBaseimagename());
//        map.add("baseimagetag", inputDataDetails.getBaseimagetag());	
//        map.add("basedockerfile", String.join(",", inputDataDetails.getBasedockerfile()));
//        map.add("BASE_DOCKERFILE_NAME", inputDataDetails.getBasedockerfilename());
//        map.add("BASE_DOCKER_BUILD_COMMAND", inputDataDetails.getBasebuildcommand());
        map.add("BASE_PATH", UPLOAD_DIR);
        map.add("ZIP_FILE_NAME", fn);

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, headers);

        // Trigger the Jenkins pipeline
        
        try {
        ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, entity, String.class);
        System.out.println("HTTP Status Code: " + response);
        String location = response.getHeaders().getLocation().toString();

        String[] parts = location.split("/");
        String itemNumber = parts[parts.length - 1];
        for(String part : parts)
        System.out.println(part);
        
        String queueItemUrl = "http://localhost:8080/queue/item/" + itemNumber + "/api/json";
//        ResponseEntity<String> queueItemResponse = restTemplate.getForEntity(queueItemUrl, String.class);
//        String queueItemBody = queueItemResponse.getBody().toString();
        
//        System.out.println(queueItemBody + "hii");
//        System.out.println(queueItemUrl);
        
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
}


@PostMapping("/getdockerfile/{useCaseId}")
public ResponseEntity<?> getUsecaseDockerfile(
		@PathVariable("useCaseId") Integer useCaseId,
		@RequestPart("inputData") String inputData,
		@RequestPart(value = "file", required = false) MultipartFile file)
{
	System.out.println(inputData);
	System.out.println(file);
	
	
	String dockerfileName = getFileName(useCaseId, "dockerfile");
	if(dockerfileName == null)
	return new ResponseEntity<>("Invalid ID", HttpStatus.NOT_ACCEPTABLE);
	ArrayList<String> dockerfile = null;
	
	ObjectMapper objectMapper = new ObjectMapper();
	try {
		
	// process react input DTO
	if(useCaseId == 1)
	{
		reactInputDto inputDataDetails = objectMapper.readValue(inputData, reactInputDto.class);
		ArrayList<String> app_docker_commands = new ArrayList<String>(Arrays.asList(inputDataDetails.getApp_docker_commands().split("\n")));
		if(file == null && inputDataDetails.getGitUrl()==null)
			return new ResponseEntity<>("Please provide code source",HttpStatus.BAD_REQUEST);
		if(file != null && inputDataDetails.getGitUrl()!=null)
			return new ResponseEntity<>("Please provide only one code source",HttpStatus.BAD_REQUEST);
		dockerfile = readDockerfile(dockerfileName,useCaseId,inputDataDetails);
	}
	
	//process C input DTO
	if(useCaseId == 2)
	{ 
		cInputDto inputDataDetails = objectMapper.readValue(inputData, cInputDto.class);
		ArrayList<String> app_docker_commands = new ArrayList<String>(Arrays.asList(inputDataDetails.getApp_docker_commands().split("\n")));
		if(file == null && inputDataDetails.getGitUrl()==null)
			return new ResponseEntity<>("Please provide code source",HttpStatus.BAD_REQUEST);
		if(file != null && inputDataDetails.getGitUrl()!=null)
			return new ResponseEntity<>("Please provide only one code source",HttpStatus.BAD_REQUEST);
		dockerfile = readDockerfile(dockerfileName,useCaseId,inputDataDetails);
	} 
    
	//process CPP input DTO
	if(useCaseId == 3)
	{
		cppInputDto inputDataDetails = objectMapper.readValue(inputData, cppInputDto.class);
		ArrayList<String> app_docker_commands = new ArrayList<String>(Arrays.asList(inputDataDetails.getApp_docker_commands().split("\n")));
		if(file == null && inputDataDetails.getGitUrl()==null)
			return new ResponseEntity<>("Please provide code source",HttpStatus.BAD_REQUEST);
		if(file != null && inputDataDetails.getGitUrl()!=null)
			return new ResponseEntity<>("Please provide only one code source",HttpStatus.BAD_REQUEST);
		dockerfile = readDockerfile(dockerfileName,useCaseId,inputDataDetails);
	}
	
	// process intel MPI input DTO
	if(useCaseId == 4)
	{
		intelmpiInputDto inputDataDetails = objectMapper.readValue(inputData, intelmpiInputDto.class);
		ArrayList<String> app_docker_commands = new ArrayList<String>(Arrays.asList(inputDataDetails.getApp_docker_commands().split("\n")));
		if(file == null && inputDataDetails.getGitUrl()==null)
			return new ResponseEntity<>("Please provide code source",HttpStatus.BAD_REQUEST);
		if(file != null && inputDataDetails.getGitUrl()!=null)
			return new ResponseEntity<>("Please provide only one code source",HttpStatus.BAD_REQUEST);
		dockerfile = readDockerfile(dockerfileName,useCaseId,inputDataDetails);
	} 
	
	// process MPICH input DTO
	if(useCaseId == 5)
	{
		mpichInputDto inputDataDetails = objectMapper.readValue(inputData, mpichInputDto.class);
		ArrayList<String> app_docker_commands = new ArrayList<String>(Arrays.asList(inputDataDetails.getApp_docker_commands().split("\n")));	
		if(file == null && inputDataDetails.getGitUrl()==null)
			return new ResponseEntity<>("Please provide code source",HttpStatus.BAD_REQUEST);
		if(file != null && inputDataDetails.getGitUrl()!=null)
			return new ResponseEntity<>("Please provide only one code source",HttpStatus.BAD_REQUEST);
		dockerfile = readDockerfile(dockerfileName,useCaseId,inputDataDetails);
	}
	
	// process Openmpi input DTO
	if(useCaseId == 6)
	{
		openmpiInputDto inputDataDetails = objectMapper.readValue(inputData, openmpiInputDto.class);
		ArrayList<String> app_docker_commands = new ArrayList<String>(Arrays.asList(inputDataDetails.getApp_docker_commands().split("\n")));
		if(file == null && inputDataDetails.getGitUrl()==null)
			return new ResponseEntity<>("Please provide code source",HttpStatus.BAD_REQUEST);
		if(file != null && inputDataDetails.getGitUrl()!=null)
			return new ResponseEntity<>("Please provide only one code source",HttpStatus.BAD_REQUEST);
		dockerfile = readDockerfile(dockerfileName,useCaseId,inputDataDetails);
	}
			
	}
	catch(NullPointerException n)
	{
		n.printStackTrace();
		return new ResponseEntity<>("Missing fields",HttpStatus.BAD_REQUEST);
	}
	catch (UnrecognizedPropertyException up)
	{
		up.printStackTrace();
		return new ResponseEntity<>("Please provide input in proper formatting",HttpStatus.BAD_REQUEST);
	
	}
	catch (MismatchedInputException e2)
	{
		e2.printStackTrace();
		return new ResponseEntity<>("Please provide input in proper formatting",HttpStatus.BAD_REQUEST);
	}
	catch (JsonMappingException e1) {
		e1.printStackTrace();
		return new ResponseEntity<>("Please provide input in proper formatting",HttpStatus.BAD_REQUEST);
	}
	catch (JsonProcessingException e1) {
		e1.printStackTrace();
		return new ResponseEntity<>("Please provide input in proper formatting",HttpStatus.BAD_REQUEST);
	} 
	
	if(dockerfile == null || dockerfile.size()==0)
		return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
	return new ResponseEntity<>(dockerfile,HttpStatus.OK);
}

//@PostMapping("/buildandpush/{useCaseId}")
//public ResponseEntity<?> buildandpush(
//		@PathVariable("useCaseId") Integer useCaseId,
//		@RequestPart("inputData") String inputData,
//		@RequestPart("file") MultipartFile file)
//{
//	System.out.println(inputData);
//	System.out.println(useCaseId);
//	ObjectMapper objectMapper = new ObjectMapper();
//	
//	
//	
//	// process MPICH input DTO
//	if(useCaseId == 5)
//	{
//	mpichInputDto inputDataDetails = null;
//    try {
//		inputDataDetails = objectMapper.readValue(inputData, mpichInputDto.class);
//		if(file == null && inputDataDetails.getGitUrl()==null)
//		return new ResponseEntity<>("Please provide code source",HttpStatus.BAD_REQUEST);
//		if(file != null && inputDataDetails.getGitUrl()!=null)
//		return new ResponseEntity<>("Please provide only one code source",HttpStatus.BAD_REQUEST);
//		System.out.println(inputDataDetails);
//	} catch (JsonMappingException e1) {
//		// TODO Auto-generated catch block
//		e1.printStackTrace();
//	} catch (JsonProcessingException e1) {
//		// TODO Auto-generated catch block
//		e1.printStackTrace();
//	}
//	}
//	
//	
//	
//	
//	
//	return new ResponseEntity<>(HttpStatus.OK);
//	
//}


public ArrayList<String> readDockerfile(String dockerfileName, Integer useCaseId, Object inputDataDetails) {
    ArrayList<String> lines = new ArrayList<>();
	try {
		Path filepath = Paths.get(file,"dockerfiles",dockerfileName);
		Resource resource = new UrlResource(filepath.toUri());
		 	
		if (resource.exists() || resource.isReadable()) {
			  lines.addAll(Files.readAllLines(filepath));
		}
		
		// replacing vars for react
		if(useCaseId == 1)
		{
			reactInputDto inputData = (reactInputDto)inputDataDetails;
			for( int i=0; i< lines.size(); i++)
			{
				if(lines.get(i).contains("${NODE_VERSION}"))
					lines.set(i, lines.get(i).replace("${NODE_VERSION}", inputData.getNode_version()));
			}
		}
		
		//replacing vars for c
		if(useCaseId == 2)
		{
			cInputDto inputData = (cInputDto)inputDataDetails;
			for( int i=0; i< lines.size(); i++)
			{
				if(lines.get(i).contains("${GCC_VERSION}"))
					lines.set(i, lines.get(i).replace("${GCC_VERSION}", inputData.getGcc_version()));
				if(lines.get(i).contains("${FILENAME}"))
					lines.set(i, lines.get(i).replace("${FILENAME}", "main.c"));
			}
		}
		
		//replacing vars for cpp
		if(useCaseId == 3)
		{
			cppInputDto inputData = (cppInputDto)inputDataDetails;
			for( int i=0; i< lines.size(); i++)
			{
				if(lines.get(i).contains("${GCC_VERSION}"))
					lines.set(i, lines.get(i).replace("${GCC_VERSION}", inputData.getGcc_version()));
				if(lines.get(i).contains("${FILENAME}"))
					lines.set(i, lines.get(i).replace("${FILENAME}", "main.cpp"));
			}
		}
		//replacing vars for intelmpi
		if(useCaseId == 4)
		{
			intelmpiInputDto inputData = (intelmpiInputDto)inputDataDetails;
			for( int i=0; i< lines.size(); i++)
			{
				if(lines.get(i).contains("${MPI_VERSION}"))
					lines.set(i, lines.get(i).replace("${MPI_VERSION}", inputData.getMpi_developement_version()));
				if(lines.get(i).contains("${ICC_VERSION}"))
					lines.set(i, lines.get(i).replace("${ICC_VERSION}", inputData.getIntel_icc_version()));
				if(lines.get(i).contains("${MKL_VERSION}"))
					lines.set(i, lines.get(i).replace("${MKL_VERSION}", inputData.getIntel_mkl_version()));
				if(lines.get(i).contains("${TBB_VERSION}"))
					lines.set(i, lines.get(i).replace("${TBB_VERSION}", inputData.getIntel_tbb_version()));
			}
		}
		// replacing vars for mpich
		if(useCaseId == 5)
		{
			mpichInputDto inputData = (mpichInputDto)inputDataDetails;
			for( int i=0; i< lines.size(); i++)
			{
			if(lines.get(i).contains("${MPI_VERSION}"))
				lines.set(i, lines.get(i).replace("${MPI_VERSION}", inputData.getMpich_version()));
			if(lines.get(i).contains("${MPI_CONFIGURE_OPTIONS}"))
				lines.set(i, lines.get(i).replace("${MPI_CONFIGURE_OPTIONS}", inputData.getMpi_configure_options()));
			if(lines.get(i).contains("${MPI_MAKE_OPTIONS}"))
				lines.set(i, lines.get(i).replace("${MPI_MAKE_OPTIONS}", inputData.getMpi_make_options()));
			if(lines.get(i).contains("${WORKDIR}"))
				lines.set(i, lines.get(i).replace("${WORKDIR}", inputData.getWork_dir()));
			if(lines.get(i).contains("${USER}"))
				lines.set(i, lines.get(i).replace("${USER}", inputData.getUser()));
			}
		}
		
		// replacing vars for openmpi
		if(useCaseId == 6)
		{
			openmpiInputDto inputData = (openmpiInputDto)inputDataDetails;
			for( int i=0; i< lines.size(); i++)
			{
				if(lines.get(i).contains("${MPI_VERSION}"))
					lines.set(i, lines.get(i).replace("${MPI_VERSION}", inputData.getOpenmpi_version()));
				if(lines.get(i).contains("${MPI_MAJOR_VERSION}"))
					lines.set(i, lines.get(i).replace("${MPI_MAJOR_VERSION}", inputData.getOpenmpi_major_version()));
				if(lines.get(i).contains("${MPI_CONFIGURE_OPTIONS}"))
					lines.set(i, lines.get(i).replace("${MPI_CONFIGURE_OPTIONS}", inputData.getMpi_configure_options()));
				if(lines.get(i).contains("${MPI_MAKE_OPTIONS}"))
					lines.set(i, lines.get(i).replace("${MPI_MAKE_OPTIONS}", inputData.getMpi_make_options()));
				if(lines.get(i).contains("${WORKDIR}"))
					lines.set(i, lines.get(i).replace("${WORKDIR}", inputData.getWork_dir()));
				if(lines.get(i).contains("${USER}"))
					lines.set(i, lines.get(i).replace("${USER}", inputData.getUser()));
			}
		}
		
		
//			for( int i=0; i< lines.size(); i++)
//				{
//				//replacing final app
//				if(dockerfileName.contains("finalMpi_"))
//				{
//				if(lines.get(i).contains("${WORKDIR}"))
//					lines.set(i, lines.get(i).replace("${WORKDIR}", "/project"));
//				if(lines.get(i).contains("${USER}"))
//					lines.set(i, lines.get(i).replace("${USER}", "user"));
//				if(lines.get(i).contains("${MPI_IMAGETAG}"))
//					lines.set(i, lines.get(i).replace("${MPI_IMAGETAG}", "4.1"));
//				if(lines.get(i).contains("${MPI_IMAGENAME}"))
//					lines.set(i, lines.get(i).replace("${MPI_IMAGENAME}", "4.1"));
//				
//				}
//				}
		
		return lines;
		}
	catch (IOException e) {
		return null;
	}
}


//	@PostMapping("/buildandpush/{useCaseId}")
//	public ResponseEntity<?> buildandpush(
//			@PathVariable("useCaseId") Integer useCaseId,
//			@RequestPart("inputData") String inputData,
//			@RequestPart("file") MultipartFile file) {
//		System.out.println(inputData);
//		System.out.println(useCaseId);
//		ObjectMapper objectMapper = new ObjectMapper();
//
//		// process MPICH input DTO
//		if (useCaseId == 5) {
//			mpichInputDto inputDataDetails = null;
//			try {
//				inputDataDetails = objectMapper.readValue(inputData, mpichInputDto.class);
//				if (file == null && inputDataDetails.getGitUrl() == null)
//					return new ResponseEntity<>("Please provide code source", HttpStatus.BAD_REQUEST);
//				if (file != null && inputDataDetails.getGitUrl() != null)
//					return new ResponseEntity<>("Please provide only one code source", HttpStatus.BAD_REQUEST);
//				System.out.println(inputDataDetails);
//			} catch (JsonMappingException e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			} catch (JsonProcessingException e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			}
//		}
//
//		return new ResponseEntity<>(HttpStatus.OK);
//
//	}

	private int parseBuildIdFromJson(String jsonResponse) {

		responseDto jsonObject = null;
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			jsonObject = objectMapper.readValue(jsonResponse, responseDto.class);
			// System.out.println(jsonObject.toString());

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

//	public ArrayList<String> readDockerfile(String dockerfileName, Integer useCaseId, Object inputDataDetails) {
//		ArrayList<String> lines = new ArrayList<>();
//		try {
//			Path filepath = Paths.get(file, "dockerfiles", dockerfileName);
//			Resource resource = new UrlResource(filepath.toUri());
//
//			if (resource.exists() || resource.isReadable()) {
//				lines.addAll(Files.readAllLines(filepath));
//			}
//
//			// replacing vars for react
//			if (useCaseId == 1) {
//				reactInputDto inputData = (reactInputDto) inputDataDetails;
//				for (int i = 0; i < lines.size(); i++) {
//					if (lines.get(i).contains("${NODE_VERSION}"))
//						lines.set(i, lines.get(i).replace("${NODE_VERSION}", inputData.getNode_version()));
//				}
//				System.out.println(inputData);
//			}
//
//			// replacing vars for c
//			if (useCaseId == 2) {
//				cInputDto inputData = (cInputDto) inputDataDetails;
//				for (int i = 0; i < lines.size(); i++) {
//					if (lines.get(i).contains("${GCC_VERSION}"))
//						lines.set(i, lines.get(i).replace("${GCC_VERSION}", inputData.getGcc_version()));
//					if (lines.get(i).contains("${FILENAME}"))
//						lines.set(i, lines.get(i).replace("${FILENAME}", "main.c"));
//				}
//				System.out.println(inputData);
//			}
//
//			// replacing vars for cpp
//			if (useCaseId == 3) {
//				cppInputDto inputData = (cppInputDto) inputDataDetails;
//				for (int i = 0; i < lines.size(); i++) {
//					if (lines.get(i).contains("${GCC_VERSION}"))
//						lines.set(i, lines.get(i).replace("${GCC_VERSION}", inputData.getGcc_version()));
//					if (lines.get(i).contains("${FILENAME}"))
//						lines.set(i, lines.get(i).replace("${FILENAME}", "main.cpp"));
//				}
//				System.out.println(inputData);
//			}
//			// replacing vars for intelmpi
//			if (useCaseId == 4) {
//				intelmpiInputDto inputData = (intelmpiInputDto) inputDataDetails;
//				for (int i = 0; i < lines.size(); i++) {
//					if (lines.get(i).contains("${MPI_VERSION}"))
//						lines.set(i, lines.get(i).replace("${MPI_VERSION}", inputData.getMpi_developement_version()));
//					if (lines.get(i).contains("${ICC_VERSION}"))
//						lines.set(i, lines.get(i).replace("${ICC_VERSION}", inputData.getIntel_icc_version()));
//					if (lines.get(i).contains("${MKL_VERSION}"))
//						lines.set(i, lines.get(i).replace("${MKL_VERSION}", inputData.getIntel_mkl_version()));
//					if (lines.get(i).contains("${TBB_VERSION}"))
//						lines.set(i, lines.get(i).replace("${TBB_VERSION}", inputData.getIntel_tbb_version()));
//				}
//				System.out.println(inputData);
//			}
//			// replacing vars for mpich
//			if (useCaseId == 5) {
//				mpichInputDto inputData = (mpichInputDto) inputDataDetails;
//				for (int i = 0; i < lines.size(); i++) {
//					if (lines.get(i).contains("${MPI_VERSION}"))
//						lines.set(i, lines.get(i).replace("${MPI_VERSION}", inputData.getMpich_version()));
//					if (lines.get(i).contains("${MPI_CONFIGURE_OPTIONS}"))
//						lines.set(i,
//								lines.get(i).replace("${MPI_CONFIGURE_OPTIONS}", inputData.getMpi_configure_options()));
//					if (lines.get(i).contains("${MPI_MAKE_OPTIONS}"))
//						lines.set(i, lines.get(i).replace("${MPI_MAKE_OPTIONS}", inputData.getMpi_make_options()));
//					if (lines.get(i).contains("${WORKDIR}"))
//						lines.set(i, lines.get(i).replace("${WORKDIR}", inputData.getWork_dir()));
//					if (lines.get(i).contains("${USER}"))
//						lines.set(i, lines.get(i).replace("${USER}", inputData.getUser()));
//				}
//				System.out.println(inputData);
//			}
//
//			// replacing vars for openmpi
//			if (useCaseId == 6) {
//				openmpiInputDto inputData = (openmpiInputDto) inputDataDetails;
//				for (int i = 0; i < lines.size(); i++) {
//					if (lines.get(i).contains("${MPI_VERSION}"))
//						lines.set(i, lines.get(i).replace("${MPI_VERSION}", inputData.getOpenmpi_version()));
//					if (lines.get(i).contains("${MPI_MAJOR_VERSION}"))
//						lines.set(i,
//								lines.get(i).replace("${MPI_MAJOR_VERSION}", inputData.getOpenmpi_major_version()));
//					if (lines.get(i).contains("${MPI_CONFIGURE_OPTIONS}"))
//						lines.set(i,
//								lines.get(i).replace("${MPI_CONFIGURE_OPTIONS}", inputData.getMpi_configure_options()));
//					if (lines.get(i).contains("${MPI_MAKE_OPTIONS}"))
//						lines.set(i, lines.get(i).replace("${MPI_MAKE_OPTIONS}", inputData.getMpi_make_options()));
//					if (lines.get(i).contains("${WORKDIR}"))
//						lines.set(i, lines.get(i).replace("${WORKDIR}", inputData.getWork_dir()));
//					if (lines.get(i).contains("${USER}"))
//						lines.set(i, lines.get(i).replace("${USER}", inputData.getUser()));
//				}
//				System.out.println(inputData);
//			}
//
//			// for( int i=0; i< lines.size(); i++)
//			// {
//			// //replacing final app
//			// if(dockerfileName.contains("finalMpi_"))
//			// {
//			// if(lines.get(i).contains("${WORKDIR}"))
//			// lines.set(i, lines.get(i).replace("${WORKDIR}", "/project"));
//			// if(lines.get(i).contains("${USER}"))
//			// lines.set(i, lines.get(i).replace("${USER}", "user"));
//			// if(lines.get(i).contains("${MPI_IMAGETAG}"))
//			// lines.set(i, lines.get(i).replace("${MPI_IMAGETAG}", "4.1"));
//			// if(lines.get(i).contains("${MPI_IMAGENAME}"))
//			// lines.set(i, lines.get(i).replace("${MPI_IMAGENAME}", "4.1"));
//			//
//			// }
//			// }
//
//			return lines;
//		} catch (IOException e) {
//			return null;
//		}
//	}

	private String getFileName(Integer useCaseId, String type) {
		UseCasesEnum useCase = useCasesEnumBean.getByUseCaseId(useCaseId);
		if (useCase != null) {
			if (type.toLowerCase() == "json") {
				return useCasesEnumBean.getByUseCaseId(useCaseId).getJsonSchemaName();
			}
			if (type.toLowerCase() == "dockerfile") {
				return useCasesEnumBean.getByUseCaseId(useCaseId).getDockerFileName();
			}
		}
		return null;
		
	}
		
		@PostMapping("/dockerfile/{useCaseId}")
		public ResponseEntity<?> generateDockerFileFromUseCase(@PathVariable("useCaseId") Integer useCaseId,
				@RequestBody JsonNode inputData) {

			String dockerfileName = getFileName(useCaseId, "dockerfile");

			if (dockerfileName == null) {
				return new ResponseEntity<>("Invalid ID", HttpStatus.NOT_ACCEPTABLE);
			}
			try {
				String fileName = getFileName(useCaseId, "dockerfile");

				if (fileName != null) {
					Path filepath = Paths.get(file, "dockerfiles", fileName);
					System.out.println(filepath.toAbsolutePath().toString());

					Resource resource = new UrlResource(filepath.toUri());
					if (resource.exists() && resource.isReadable()) {
						String dockerFileString = resource.getContentAsString(Charset.defaultCharset());
						ObjectMapper objectMapper = new ObjectMapper();
						Map<String, Object> mapMapped = objectMapper.readValue(inputData.toString(),
								new TypeReference<Map<String, Object>>() {
								});

				        Map<String, Object> map = new HashMap<>();

				        // Iterate over the original map and convert keys to uppercase
				        for (Map.Entry<String, Object> entry : mapMapped.entrySet()) {
				        	map.put(entry.getKey().toUpperCase(), entry.getValue());
				        }

						String updatedDockerfile = CommonUtils.replaceDockerArgs(dockerFileString, map);
						byte[] fileContent = updatedDockerfile.getBytes();

						// Set up response headers
						HttpHeaders headers = new HttpHeaders();
						headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Dockerfile");
						headers.add(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN_VALUE);
						headers.add(HttpHeaders.CONTENT_LENGTH, String.valueOf(fileContent.length));

						// Return the file as a byte array
						return new ResponseEntity<>(fileContent, headers, HttpStatus.OK);
					} else {
						return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
					}
				}

				return new ResponseEntity<>(HttpStatus.OK);
			} catch (Exception e) {
				System.out.println(e.toString());
				return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
			}

		}

	// @PostMapping("/setpipeline/{useCaseId}")
	// public ResponseEntity<?> setpipeline(@RequestBody UseCaseInputDto inputDto,
	// @PathVariable("useCaseId") Integer useCaseId)
	// {
	// System.out.println(useCaseId);
	// UseCase usecase = new UseCase();
	// if(useCaseId == 1)
	// usecase.setReactUsecase(inputDto);
	// else
	// return new ResponseEntity<>("Invalid Parameter", HttpStatus.NOT_ACCEPTABLE);
	// return new ResponseEntity<>(usecase, HttpStatus.OK);
	// }
	// @PostMapping("/getusecaseparams/{useCaseId}")
	// public ResponseEntity<?> getusecases(@PathVariable("useCaseId") Integer
	// useCaseId)
	// {
	// return new ResponseEntity<>("", HttpStatus.OK);
	// }

}
