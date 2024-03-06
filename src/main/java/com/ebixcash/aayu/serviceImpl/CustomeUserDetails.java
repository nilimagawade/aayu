package com.ebixcash.aayu.serviceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.ebixcash.aayu.model.User;
import com.ebixcash.aayu.repositories.UserRepository;



@Service
public class CustomeUserDetails implements UserDetailsService {
	
	
	
	@Autowired
	private UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// load user from database
		
		User user = userRepository.findByEmail(username).orElseThrow(() -> new RuntimeException("USer not found"));
		return user;
	}

}
