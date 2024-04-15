package com.app.jwt_utils;

import java.util.Arrays;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.app.entities.Users;

import lombok.ToString;
@ToString
public class CustomUserDetails implements UserDetails {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Users user;
	
	public CustomUserDetails(Users user) {
		super();
//		if(user instanceof Employee)
		this.user = user;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		//Map   role   -----> Collection : GrantedAuthority <----SimpleGrantedAuthority(String authority)		
		return Arrays.asList(new SimpleGrantedAuthority(user.getUserRole().name()));
	}


	@Override
	public String getPassword() {
		// TODO Auto-generated method stub
		return user.getPassword();
	}

	@Override
	public String getUsername() {
		// TODO Auto-generated method stub
		return user.getUsername();
	}

	@Override
	public boolean isAccountNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return true;
	}

}
