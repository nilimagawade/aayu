package com.ebixcash.aayu.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ebixcash.aayu.model.User;
import com.ebixcash.aayu.security.JwtRequest;
import com.ebixcash.aayu.security.JwtResponse;
import com.ebixcash.aayu.security.JwtTokenHelper;
import com.ebixcash.aayu.serviceImpl.UserService;

@RestController
public class GenerateTokenController {

	private Logger logger = LoggerFactory.getLogger(GenerateTokenController.class);

	@Autowired
	private UserDetailsService userDetailsService;

	@Autowired
	private AuthenticationManager manager;

	@Autowired
	private JwtTokenHelper helper;

	@Autowired
	private UserService userService;

	// open end point for testing
	@GetMapping(value = "/msg")
	public String getMsg() {
		return "SUCCESS";
	}

	@GetMapping(value = "/testToken")
	public String getToken() {
		return "SUCCESS";
	}

	@PostMapping("/createUser")
	public User createUser(@RequestBody User user) {

		return userService.createUser(user);

	}

	@PostMapping("/generateToken")
	public ResponseEntity<JwtResponse> login(@RequestBody JwtRequest request) {

		logger.debug("Here are we ");
		this.doAuthenticate(request.getEmail(), request.getPassword());

		UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
		String token = this.helper.generateToken(userDetails);

//        JwtResponse response = ((Object) JwtResponse.builder())
//                .jwtToken(token)
//                .username(userDetails.getUsername()).build();
//        
		JwtResponse response = new JwtResponse(token, userDetails.getUsername());

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	private void doAuthenticate(String email, String password) {

		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(email, password);
		try {
			manager.authenticate(authentication);

		} catch (BadCredentialsException e) {
			throw new BadCredentialsException(" Invalid Username or Password  !!");
		}

	}

	@ExceptionHandler(BadCredentialsException.class)
	public String exceptionHandler() {
		return "Credentials Invalid !!";
	}

}
