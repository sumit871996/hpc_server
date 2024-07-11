package com.app.controller;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.entities.UseCases.UseCasesEnum;
import com.app.service.getUseCases.getUseCaseServiceImpl;

@RestController
@CrossOrigin("*")
@RequestMapping("/form")
public class UseCaseController {
	
@Autowired
public getUseCaseServiceImpl getusecaseService;

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
	System.out.println(useCaseId);
	String jsonfilename = null;
	switch(useCaseId)
	{
	case 1:
		jsonfilename = "react_params.json";
		break;
	case 2:
		jsonfilename = "c_params.json";
		break;
	case 3:
		jsonfilename = "cpp_params.json";
		break;
	case 4:
		jsonfilename = "intelmpi_params.json";
		break;
	case 5:
		jsonfilename = "mpich_params.json";
		break;
	case 6:
		jsonfilename = "openmpi_params.json";
		break;
	default:
		return new ResponseEntity<>("Invalid ID", HttpStatus.NOT_ACCEPTABLE);
	}
	
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
