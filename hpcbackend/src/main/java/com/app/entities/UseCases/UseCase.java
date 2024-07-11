package com.app.entities.UseCases;

import java.util.ArrayList;
import java.util.HashMap;

import com.app.dto.UseCaseInputDto;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class UseCase {

	public int usecaseId;
	public String useCaseName;
	public String useCaseDescription;
	
	
	public ArrayList<Stage> stages = new ArrayList<Stage>();
	public HashMap<String,String> environment;
	
	
	public UseCase()
	{
		super();
	}
	public UseCase(int usecaseId, String useCaseName, String useCaseDesciption, ArrayList<Stage> stages, HashMap<String, String> environment)
	{
		this.usecaseId = usecaseId;
		this.useCaseDescription = useCaseDesciption;
		this.useCaseName = useCaseName;
		this.stages = stages;
		this.environment = environment;
	}
	
	
	@SuppressWarnings("unchecked")
	public void setReactUsecase(
			UseCaseInputDto inputDto)
	{
		ArrayList<Step> steps = new ArrayList<Step>();
		HashMap<String, String> environment = new HashMap<String,String>();
	
		
		
		// git Checkout
		Step step1 = new Step();
		step1.setGitBranch(inputDto.gitUrl);
		
		steps.add(step1);
		Stage stageCheckout = new Stage("Checkout", (ArrayList<Step>)steps.clone());
		this.stages.add(stageCheckout);
		steps.clear();
		//----------------------------------------------------------------//
		
		
		
		//docker Image build
		Step step2 = new Step("sh 'docker image prune -af'");
		Step step3 = new Step("docker image build -t " + inputDto.imagename+ ":" + inputDto.imagetag + " " + inputDto.dockerfilePath);
		
		steps.add(step2);
		steps.add(step3);
	
		Stage dockerImageBuild = new Stage("Docker Image Build", (ArrayList<Step>)steps.clone());
		this.stages.add(dockerImageBuild);
		steps.clear();
		//----------------------------------------------------------------//
		
		//docker Image push

		Step step4 = new Step("echo " + inputDto.dockerpassword + " | docker login " + inputDto.dockerregistry + " -u " + inputDto.dockeruser + " --password-stdin");
		Step step5 = new Step("docker image push "+ inputDto.imagename + ":" + inputDto.imagetag);
				
		steps.add(step4);
		steps.add(step5);
		Stage dockerImagePush = new Stage("Docker Image Push", (ArrayList<Step>)steps.clone());	
		this.stages.add(dockerImagePush);
		steps.clear();
		//----------------------------------------------------------------//
		
		
		
		
		
		
		
		// adding environment variables
		environment.put("GITHUB_CREDENTIALS", "github_id");
		environment.put("REVISION", "1");
		
		this.usecaseId = 1;
		this.useCaseDescription = "This is used for react";
		this.useCaseName = "React";
		this.environment = environment;
		
		
	}
}
