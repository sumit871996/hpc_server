package com.app.controller;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.app.dto.responseDto;
import com.app.dto.new_dto.cInputDto;
import com.app.dto.new_dto.cppInputDto;
import com.app.dto.new_dto.intelmpiInputDto;
import com.app.dto.new_dto.mpichInputDto;
import com.app.dto.new_dto.openmpiInputDto;
import com.app.dto.new_dto.reactInputDto;
import com.app.entities.UseCases.UseCasesEnum;
import com.app.entities.UseCases.UseCasesEnumBean;
import com.app.utils.CommonUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;

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

@PostMapping("/getdockerfile/{useCaseId}")
public ResponseEntity<?> getUsecaseDockerfile(
		@PathVariable("useCaseId") Integer useCaseId,
		@RequestPart("inputData") String inputData,
		@RequestPart(value = "file", required = false) MultipartFile file)
{
	
	
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
		String[] linesAppDockerCommands = inputDataDetails.getApp_docker_commands().split("\n");
		ArrayList<String> app_docker_commands = new ArrayList<String>(Arrays.asList(linesAppDockerCommands));
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
		String[] linesAppDockerCommands = inputDataDetails.getApp_docker_commands().split("\n");
		ArrayList<String> app_docker_commands = new ArrayList<String>(Arrays.asList(linesAppDockerCommands));
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
		String[] linesAppDockerCommands = inputDataDetails.getApp_docker_commands().split("\n");
		ArrayList<String> app_docker_commands = new ArrayList<String>(Arrays.asList(linesAppDockerCommands));
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
		String[] linesAppDockerCommands = inputDataDetails.getApp_docker_commands().split("\n");
		ArrayList<String> app_docker_commands = new ArrayList<String>(Arrays.asList(linesAppDockerCommands));
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
		String[] linesAppDockerCommands = inputDataDetails.getApp_docker_commands().split("\n");
		ArrayList<String> app_docker_commands = new ArrayList<String>(Arrays.asList(linesAppDockerCommands));
		System.out.println(app_docker_commands.get(0));
		System.out.println(app_docker_commands.get(1));		
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
		String[] linesAppDockerCommands = inputDataDetails.getApp_docker_commands().split("\n");
		ArrayList<String> app_docker_commands = new ArrayList<String>(Arrays.asList(linesAppDockerCommands));
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
						Map<String, Object> map = objectMapper.readValue(inputData.toString(),
								new TypeReference<Map<String, Object>>() {
								});

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
