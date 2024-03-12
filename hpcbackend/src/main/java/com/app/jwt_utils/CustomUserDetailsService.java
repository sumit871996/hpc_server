package com.app.jwt_utils;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.app.dao.UsersRepository;
import com.app.entities.Users;

@Service // or @Component also works!
@Transactional

public class CustomUserDetailsService implements UserDetailsService {
	// dep : user repository : based upon spring data JPA
	@Autowired
	private UsersRepository userRepo;
	 
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		System.out.println("in load by user nm " + username);
		// invoke dao's method to load user details from db by username(ie. actaully an
		// email)
		Optional<Users> users= userRepo.findByUsername(username);
		//System.out.println(user);
		if(users.isPresent())
		{
			Users user = users.get();
			System.out.println(user.getUserRole());
			return new CustomUserDetails(user);
		}
		else
			return null;
		

	}

}
