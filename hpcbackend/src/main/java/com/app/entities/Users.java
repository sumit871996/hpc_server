package com.app.entities;

import java.time.LocalDateTime;

import jakarta.persistence.*;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@Entity
@Table(name = "users")
public class Users {
	
	@Id
	@TableGenerator(initialValue = 1000, name = "userGen", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "userGen")
	@Column(name = "user_id")
	public long userId;
	
	public String getUserRole()
	{
	return "USER";
	}
	
	@Column(name = "username", length = 10, nullable = false)
	public String username;
	
	@Column(name = "password", nullable = false)
	public String password;


}
