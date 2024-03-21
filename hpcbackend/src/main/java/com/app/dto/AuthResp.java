package com.app.dto;

import com.app.entities.Role;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class AuthResp {
	private String message;
	private String token;
	private Long id;
	private Role role;
	
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public void setRole(Role role) {
		this.role = role;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
	
	public void setToken(String token) {
		this.token = token;
	}
	
	
	public AuthResp(String message, String token, Long id, Role role )
	{
	this.token = token;
	this.message = message;
	this.id = id;
	this.role = role;
	
	}
	
}
