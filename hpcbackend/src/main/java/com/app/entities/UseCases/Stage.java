package com.app.entities.UseCases;

import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class Stage {
	public String stageName;
	public ArrayList<Step> steps;
	public Stage()
	{
		this.stageName = " ";
		this.steps = new ArrayList<Step>();
	}
	public Stage(String stageName,ArrayList<Step> steps)
	{
		super();
		this.stageName = stageName;
		this.steps = steps;
	}

}
