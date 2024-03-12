package com.app.service;

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.dao.UsersRepository;

import com.app.entities.Users;

@Service
@Transactional
public class UserServiceImpl implements IUserService{
	
	@Autowired
	private PasswordEncoder pwencoder;

	@Autowired
	private ModelMapper mapper;
	
	@Autowired
	private UsersRepository userRepository;


	@Override
	public Users signUP(Users user) {
		
		Optional<Users> users = userRepository.findByUsername(user.getUsername());
		

		// System.out.println(user);
		

		String password = pwencoder.encode(user.getPassword());
		user.setPassword(password);
		return userRepository.save(user);
	}

}
