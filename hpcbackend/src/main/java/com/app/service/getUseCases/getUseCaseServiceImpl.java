package com.app.service.getUseCases;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.dto.UseCaseInputDto;
import com.app.entities.UseCases.UseCase;

@Service
@Transactional
public class getUseCaseServiceImpl {

	public UseCase getReactUseCase(UseCaseInputDto inputDto)
	{
		
		UseCase usecaseReact = new UseCase();
		usecaseReact.setReactUsecase(inputDto);
		
		return usecaseReact;
		
	}
	
//	public ArrayList<Stage> getReactStages()
//	{
//		Stage stageCheckout = new Stage();
//		Stage dockerImageBuild = new Stage();
//		Stage dockerImagePush = new Stage();
//		
//	}
}
