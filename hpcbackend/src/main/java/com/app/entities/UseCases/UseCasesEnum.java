package com.app.entities.UseCases;

import lombok.Getter;
import lombok.Setter;

public enum UseCasesEnum {
	REACT(1, "react_params.json", "react_Dockerfile"),
	C(2, "c_params.json", "c_Dockerfile"),
	CPP(3, "cpp_params.json", "cpp_Dockerfile"),
	INTELMPI(4, "intelmpi_params.json", "intelmpi_Dockerfile"),
	MPICH(5, "mpich_params.json", "mpich_Dockerfile"),
	OPENMPI(6, "openmpi_params.json", "openmpi_Dockerfile");

	int useCaseId;
	String json_schema_name;
	String docker_file_name;

	private UseCasesEnum(int useCaseId, String json_schema_name, String docker_file_name) {
		this.useCaseId = useCaseId;
		this.json_schema_name = json_schema_name;
		this.docker_file_name = docker_file_name;
	}

	public int getUseCaseId() {
		return useCaseId;
	}

	public String getJsonSchemaName() {
		return json_schema_name;
	}

	public String getDockerFileName() {
		return docker_file_name;
	}

}
