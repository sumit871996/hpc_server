package com.app.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.dao.UsersRepository;
import com.app.dto.AuthRequest;
import com.app.dto.AuthResp;
import com.app.entities.Users;
import com.app.jwt_utils.JwtUtils;
import com.app.service.UserServiceImpl;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
@CrossOrigin("*")
@Validated
public class SignInController {
	
	@Autowired
	private JwtUtils utils;
	// dep : Auth mgr
	@Autowired
	private AuthenticationManager manager;
	
	@Autowired
	private UsersRepository userRepo;
	
	@Autowired
	private UserServiceImpl userService;
	
	@PostMapping("/signin")
	public ResponseEntity<?> validateUserCreateToken(@RequestBody @Valid AuthRequest request) {
		// store incoming user details(not yet validated) into Authentication object
		// Authentication i/f ---> implemented by UserNamePasswordAuthToken
		UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(request.getUsername(),
				request.getPassword());
//		log.info("auth token before {}", authToken);
		try {
			// authenticate the credentials
			Authentication authenticatedDetails = manager.authenticate(authToken);
//			log.info("auth token again {} ", authenticatedDetails);
			// => auth succcess
			
			System.out.println("inside");
			AuthResp authResp = new AuthResp();
			Optional<Users> users = userRepo.findByUsername(request.getUsername());
			// System.out.println(user);
			if (users.isPresent()) {
				Users user = users.get();
				authResp.setId(user.getUserId());
				authResp.setRole(user.getUserRole());
			}
			authResp.setMessage("Auth Successful!!!");
			authResp.setToken(utils.generateJwtToken(authenticatedDetails));

			return ResponseEntity.ok(authResp);
		} catch (BadCredentialsException e) { // lab work : replace this by a method in global exc handler
			// send back err resp code
			System.out.println("err " + e);
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
		}

	}
	
	@PostMapping("/signup")
	public ResponseEntity<Users> signUP(@RequestBody @Valid Users user) {

		Users p = userService.signUP(user);

		return new ResponseEntity<>(p, HttpStatus.CREATED);

	}

}
