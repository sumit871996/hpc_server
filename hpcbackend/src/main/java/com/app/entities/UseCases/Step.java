package com.app.entities.UseCases;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Step {
	
	public String step;
	public Step()
	{
		this.step = " ";
	}
	public Step(String step)
	{
		super();
		this.step = step;
	}
	
	public Step Clone()
	{
		return this.Clone();
	}
	
	public void setGitBranch(String gitUrl)
	{
		this.step="git branch: 'main', url: '"+ gitUrl.toString() +"'";
	}


}
