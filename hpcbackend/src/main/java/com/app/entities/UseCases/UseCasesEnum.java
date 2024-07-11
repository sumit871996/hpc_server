package com.app.entities.UseCases;

public enum UseCasesEnum {
	REACT(1), C(2), CPP(3), INTELMPI(4), MPICH(5), OPENMPI(6);
	
	int useCaseId;
	
	private UseCasesEnum(int useCaseId) {
        this.useCaseId = useCaseId;
    }
	
	public int getUseCaseId()
	{
		return useCaseId;
	}
	

}
