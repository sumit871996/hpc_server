package com.app.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.app.dto.inputDataDto;
import com.app.dto.new_dto.commonInputDto;
import com.app.dto.new_dto.inputDto;
import com.app.entities.UseCases.UseCasesEnum;
import com.app.service.getUseCases.getUseCaseServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@CrossOrigin("*")
@RequestMapping("/form")
public class UseCaseController {
	
//@Autowired
//public getUseCaseServiceImpl getusecaseService;

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

@GetMapping("/getdockerfile/{useCaseId}")
public ResponseEntity<?> getUsecaseDockerfile(@PathVariable("useCaseId") Integer useCaseId)
{
	String dockerfileName = getFileName(useCaseId, "dockerfile");
	if(dockerfileName == null)
	return new ResponseEntity<>("Invalid ID", HttpStatus.NOT_ACCEPTABLE);
	ArrayList<String> dockerfile = readDockerfile(dockerfileName);
	if(dockerfile == null || dockerfile.size()==0)
		return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
	return new ResponseEntity<>(dockerfile,HttpStatus.OK);
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

@PostMapping("/buildandpush")
public ResponseEntity<?> buildandpush(@ModelAttribute("inputData") inputDto inputData)
{
	System.out.println(inputData.getFile());
	System.out.println(inputData.getUseCaseInput());
	System.out.println(inputData.getGitUrl());
	
	commonInputDto commonInput = new commonInputDto();
	inputDto inputDataDetails = null;
//	ObjectMapper objectMapper = new ObjectMapper();
//    try {
//		inputDataDetails = objectMapper.readValue(inputData, inputDto.class);
//	} catch (JsonMappingException e1) {
//		// TODO Auto-generated catch block
//		e1.printStackTrace();
//	} catch (JsonProcessingException e1) {
//		// TODO Auto-generated catch block
//		e1.printStackTrace();
//	}
    
//    System.out.println(inputDataDetails);
//    
//	if(gitUrl == null && file == null)
//		return new ResponseEntity<>("Please provide source code input", HttpStatus.BAD_REQUEST);
//	else
//	if(gitUrl != null && file != null)
//		return new ResponseEntity<>("Please provide only once type of source code input", HttpStatus.BAD_REQUEST);
//	if(gitUrl != null)
//	{
//		
//	}
//	else if(file != null)
//	{
//		
//	}
//	
	return new ResponseEntity<>(HttpStatus.OK);
	
}

public ArrayList<String> readDockerfile(String dockerfileName) {
    ArrayList<String> lines = new ArrayList<>();
	try {
		Path filepath = Paths.get(file,"dockerfiles",dockerfileName);
		Resource resource = new UrlResource(filepath.toUri());
		 	
		if (resource.exists() || resource.isReadable()) {
			  lines.addAll(Files.readAllLines(filepath));
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
