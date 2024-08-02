package com.app.controller;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.app.dto.responseDto;
import com.app.dto.new_dto.mpichInputDto;
import com.app.dto.new_dto.reactInputDto;
import com.app.entities.UseCases.UseCasesEnum;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@CrossOrigin("*")
@RequestMapping("/form")
public class UseCaseController {
	
//@Autowired
//public getUseCaseServiceImpl getusecaseService;
private static final String UPLOAD_DIR = "C:"+File.separator+"ProgramData"+File.separator+"zipfiles";

@Autowired
private ModelMapper mapper;

@Value("${file.upload.folder}") // Injecting the value of app.greeting from application.properties
private String file;
	
@GetMapping("/getusecases")
public ResponseEntity<?> getusecaseInfo()
{
	
	HashMap<Integer, String> hm = new HashMap<Integer,String>();
	for(UseCasesEnum env : UseCasesEnum.values())
	hm.put(env.getUseCaseId(), env.toString());
	return new ResponseEntity<>(hm,HttpStatus.OK);
}



@GetMapping("/getusecases/{useCaseId}")
public ResponseEntity<?> getusecaseDetails(@PathVariable("useCaseId") Integer useCaseId)
{
	String jsonfilename = getFileName(useCaseId, "json");
	if(jsonfilename==null)
	return new ResponseEntity<>("Invalid ID", HttpStatus.NOT_ACCEPTABLE);
	
	try {
		Path filepath = Paths.get(file,"json",jsonfilename);
		Resource resource = new UrlResource(filepath.toUri());
		
		System.out.println(filepath);
		
		if (resource.exists() || resource.isReadable()) {
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
//	return new ResponseEntity<>(file.concat(File.separator).concat("json").concat(File.separator).concat("mpich_params.json"),HttpStatus.OK);
	

}

@PostMapping("/getdockerfile/{useCaseId}")
public ResponseEntity<?> getUsecaseDockerfile(
		@PathVariable("useCaseId") Integer useCaseId,
		@RequestPart("inputData") String inputData,
		@RequestPart("file") MultipartFile file)
{
	ObjectMapper objectMapper = new ObjectMapper();

	// process react input DTO
	if(useCaseId == 1)
	{
	reactInputDto inputDataDetails = null;
    try {
		inputDataDetails = objectMapper.readValue(inputData, reactInputDto.class);
		if(file == null && inputDataDetails.getGitUrl()==null)
		return new ResponseEntity<>("Please provide code source",HttpStatus.BAD_REQUEST);
		if(file != null && inputDataDetails.getGitUrl()!=null)
		return new ResponseEntity<>("Please provide only one code source",HttpStatus.BAD_REQUEST);
		System.out.println(inputDataDetails);
	} catch (JsonMappingException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	} catch (JsonProcessingException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}
	}
	
	// process MPICH input DTO
	if(useCaseId == 5)
	{
	mpichInputDto inputDataDetails = null;
    try {
		inputDataDetails = objectMapper.readValue(inputData, mpichInputDto.class);
		if(file == null && inputDataDetails.getGitUrl()==null)
		return new ResponseEntity<>("Please provide code source",HttpStatus.BAD_REQUEST);
		if(file != null && inputDataDetails.getGitUrl()!=null)
		return new ResponseEntity<>("Please provide only one code source",HttpStatus.BAD_REQUEST);
		System.out.println(inputDataDetails);
	} catch (JsonMappingException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	} catch (JsonProcessingException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}
	}
	
	
	
	
	
	
	String dockerfileName = getFileName(useCaseId, "dockerfile");
	if(dockerfileName == null)
	return new ResponseEntity<>("Invalid ID", HttpStatus.NOT_ACCEPTABLE);
	ArrayList<String> dockerfile = readDockerfile(dockerfileName);
	if(dockerfile == null || dockerfile.size()==0)
		return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
	return new ResponseEntity<>(dockerfile,HttpStatus.OK);
}

@PostMapping("/buildandpush/{useCaseId}")
public ResponseEntity<?> buildandpush(
		@PathVariable("useCaseId") Integer useCaseId,
		@RequestPart("inputData") String inputData,
		@RequestPart("file") MultipartFile file)
{
	System.out.println(inputData);
	System.out.println(useCaseId);
	ObjectMapper objectMapper = new ObjectMapper();
	
	
	
	// process MPICH input DTO
	if(useCaseId == 5)
	{
	mpichInputDto inputDataDetails = null;
    try {
		inputDataDetails = objectMapper.readValue(inputData, mpichInputDto.class);
		if(file == null && inputDataDetails.getGitUrl()==null)
		return new ResponseEntity<>("Please provide code source",HttpStatus.BAD_REQUEST);
		if(file != null && inputDataDetails.getGitUrl()!=null)
		return new ResponseEntity<>("Please provide only one code source",HttpStatus.BAD_REQUEST);
		System.out.println(inputDataDetails);
	} catch (JsonMappingException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	} catch (JsonProcessingException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}
	}
	
	
	
	
	
	return new ResponseEntity<>(HttpStatus.OK);
	
}

private int parseBuildIdFromJson(String jsonResponse) {
	  
    
    responseDto jsonObject = null;
	ObjectMapper objectMapper = new ObjectMapper();
    try {
    	jsonObject = objectMapper.readValue(jsonResponse, responseDto.class);
//    	System.out.println(jsonObject.toString());
    	
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

public ArrayList<String> readDockerfile(String dockerfileName) {
    ArrayList<String> lines = new ArrayList<>();
	try {
		Path filepath = Paths.get(file,"dockerfiles",dockerfileName);
		Resource resource = new UrlResource(filepath.toUri());
		 	
		if (resource.exists() || resource.isReadable()) {
			  lines.addAll(Files.readAllLines(filepath));
		}

			for( int i=0; i< lines.size(); i++)
				{
				
				//replacing c vars
				if(dockerfileName.contains("c_"))
				{
				if(lines.get(i).contains("${GCC_VERSION}"))
					lines.set(i, lines.get(i).replace("${GCC_VERSION}", "4.1"));
				if(lines.get(i).contains("${FILENAME}"))
					lines.set(i, lines.get(i).replace("${FILENAME}", "4.1"));
				}
				//replacing cpp vars
				else if(dockerfileName.contains("cpp_"))
				{
				if(lines.get(i).contains("${GCC_VERSION}"))
					lines.set(i, lines.get(i).replace("${GCC_VERSION}", "4.1"));
				if(lines.get(i).contains("${FILENAME}"))
					lines.set(i, lines.get(i).replace("${FILENAME}", "4.1"));
				}
				
				//replacing mpich params
				else if(dockerfileName.contains("mpich_"))
				{
				if(lines.get(i).contains("${MPI_VERSION}"))
					lines.set(i, lines.get(i).replace("${MPI_VERSION}", "4.1"));
				if(lines.get(i).contains("${MPI_CONFIGURE_OPTIONS}"))
					lines.set(i, lines.get(i).replace("${MPI_CONFIGURE_OPTIONS}", "--disable-fortran"));
				if(lines.get(i).contains("${MPI_MAKE_OPTIONS}"))
					lines.set(i, lines.get(i).replace("${MPI_MAKE_OPTIONS}", "-j4"));
				if(lines.get(i).contains("${WORKDIR}"))
					lines.set(i, lines.get(i).replace("${WORKDIR}", "/project"));
				if(lines.get(i).contains("${USER}"))
					lines.set(i, lines.get(i).replace("${USER}", "user"));
				}
				//replacing intelmpi params
				else if(dockerfileName.contains("intelmpi_"))
				{
				if(lines.get(i).contains("${MPI_VERSION}"))
					lines.set(i, lines.get(i).replace("${MPI_VERSION}", "4.1"));
				if(lines.get(i).contains("${ICC_VERSION}"))
					lines.set(i, lines.get(i).replace("${ICC_VERSION}", "4.1"));
				if(lines.get(i).contains("${MKL_VERSION}"))
					lines.set(i, lines.get(i).replace("${MKL_VERSION}", "4.1"));
				if(lines.get(i).contains("${TBB_VERSION}"))
					lines.set(i, lines.get(i).replace("${TBB_VERSION}", "4.1"));
				}
				//replacing openmpi params
				else if(dockerfileName.contains("openmpi_"))
				{
				if(lines.get(i).contains("${MPI_VERSION}"))
					lines.set(i, lines.get(i).replace("${MPI_VERSION}", "4.1"));
				if(lines.get(i).contains("${MPI_MAJOR_VERSION}"))
					lines.set(i, lines.get(i).replace("${MPI_MAJOR_VERSION}", "4.1"));
				if(lines.get(i).contains("${MPI_CONFIGURE_OPTIONS}"))
					lines.set(i, lines.get(i).replace("${MPI_CONFIGURE_OPTIONS}", "--disable-fortran"));
				if(lines.get(i).contains("${MPI_MAKE_OPTIONS}"))
					lines.set(i, lines.get(i).replace("${MPI_MAKE_OPTIONS}", "-j4"));
				if(lines.get(i).contains("${WORKDIR}"))
					lines.set(i, lines.get(i).replace("${WORKDIR}", "/project"));
				if(lines.get(i).contains("${USER}"))
					lines.set(i, lines.get(i).replace("${USER}", "user"));
				}
				
				//replacing react params
				else if(dockerfileName.contains("react_"))
				{
				if(lines.get(i).contains("${NODE_VERSION}"))
					lines.set(i, lines.get(i).replace("${NODE_VERSION}", "4.1"));
				}
				//replacing final app
				else if(dockerfileName.contains("finalMpi_"))
				{
				if(lines.get(i).contains("${WORKDIR}"))
					lines.set(i, lines.get(i).replace("${WORKDIR}", "/project"));
				if(lines.get(i).contains("${USER}"))
					lines.set(i, lines.get(i).replace("${USER}", "user"));
				if(lines.get(i).contains("${MPI_IMAGETAG}"))
					lines.set(i, lines.get(i).replace("${MPI_IMAGETAG}", "4.1"));
				if(lines.get(i).contains("${MPI_IMAGENAME}"))
					lines.set(i, lines.get(i).replace("${MPI_IMAGENAME}", "4.1"));
				
				}
				}
		
		return lines;
	}
	catch(IOException e) {
		return null;
	}
}
private String getFileName(Integer useCaseId, String type)
{
	switch(useCaseId)
	{
	case 1:
		if("json" == type.toLowerCase())
			return "react_params.json";
		if("dockerfile" == type.toLowerCase())
			return "react_Dockerfile";
	case 2:
		if("json" == type.toLowerCase())
			return "c_params.json";
		if("dockerfile" == type.toLowerCase())
			return "c_Dockerfile";
	case 3:
		if("json" == type.toLowerCase())
			return "cpp_params.json";
		if("dockerfile" == type.toLowerCase())
			return "cpp_Dockerfile";
	case 4:
		if("json" == type.toLowerCase())
			return "intelmpi_params.json";
		if("dockerfile" == type.toLowerCase())
			return "intelmpi_Dockerfile";
	case 5:
		if("dockerfile" == type.toLowerCase())
			return "mpich_Dockerfile";
		if("json" == type.toLowerCase())
			return "mpich_params.json";

	case 6:
		if("json" == type.toLowerCase())
			return "openmpi_params.json";
		if("dockerfile" == type.toLowerCase())
			return "openmpi_Dockerfile";
	default:
		return null;
	}
}

//@PostMapping("/setpipeline/{useCaseId}")
//public ResponseEntity<?> setpipeline(@RequestBody UseCaseInputDto inputDto, @PathVariable("useCaseId") Integer useCaseId)
//{
//	System.out.println(useCaseId);
//	UseCase usecase = new UseCase();
//	if(useCaseId == 1)
//	usecase.setReactUsecase(inputDto);
//	else
//	return new ResponseEntity<>("Invalid Parameter", HttpStatus.NOT_ACCEPTABLE);
//	return new ResponseEntity<>(usecase, HttpStatus.OK);
//}
//@PostMapping("/getusecaseparams/{useCaseId}")
//public ResponseEntity<?> getusecases(@PathVariable("useCaseId") Integer useCaseId)
//{
//	return new ResponseEntity<>("", HttpStatus.OK);
//}




}
