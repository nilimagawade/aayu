package com.ebixcash.aayu.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Autowired
	private JwtAuthenticationEntryPoint point;
	@Autowired
	private JwtAuthenticationFilter filter;
	
	@Autowired
	private PasswordEncoder passwordEncoder;


	@Autowired
	private UserDetailsService userDetailService;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

//working code
//		http.csrf(csrf -> csrf.disable()).authorizeRequests().requestMatchers("/generateToken").permitAll() //
//				.requestMatchers("/msg").permitAll().anyRequest().authenticated().and() //
//				.exceptionHandling(ex -> ex.authenticationEntryPoint(point)) //
//				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)); //
//		http.addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class); //
//		return http.build();

		http.csrf(csrf -> csrf.disable())
				.authorizeHttpRequests(
						authorizeRequests -> authorizeRequests.requestMatchers("/generateToken", "/msg","/createUser").permitAll() 
								.anyRequest().authenticated() // Secure all other endpoints
				).exceptionHandling(ex -> ex.authenticationEntryPoint(point))
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

		http.addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);
		return http.build();

	}
	
	@Bean
	public DaoAuthenticationProvider daoAuthenticationProvider() {

		DaoAuthenticationProvider provider = new DaoAuthenticationProvider();

		provider.setUserDetailsService(userDetailService);
		provider.setPasswordEncoder(passwordEncoder);

		return provider;

	}
}
