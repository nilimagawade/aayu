package com.ebixcash.aayu.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ebixcash.aayu.model.User;




public interface UserRepository extends JpaRepository<User, String>{

	
	public Optional<User>findByEmail(String Email);
}
