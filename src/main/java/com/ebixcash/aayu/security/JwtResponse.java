package com.ebixcash.aayu.security;

public class JwtResponse {

	private String jwtToken;
	private String username;
	
	
	
	public JwtResponse(String jwtToken, String username) {
		super();
		this.jwtToken = jwtToken;
		this.username = username;
	}
	@Override
	public String toString() {
		return "JwtResposne [jwtToken=" + jwtToken + ", username=" + username + "]";
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getJwtToken() {
		return jwtToken;
	}
	public void setJwtToken(String jwtToken) {
		this.jwtToken = jwtToken;
	}

	
	
}
