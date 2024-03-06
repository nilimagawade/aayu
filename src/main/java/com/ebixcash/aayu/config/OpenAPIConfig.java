package com.ebixcash.aayu.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {

	/*
	 * @Value("${ebxicash.openapi.dev-url}") private String devUrl;
	 * 
	 * @Value("${ebxicash.openapi.prod-url}") private String prodUrl;
	 * 
	 * @Bean public OpenAPI myOpenAPI() { Server devServer = new Server();
	 * devServer.setUrl(devUrl);
	 * devServer.setDescription("Server URL in Development environment");
	 * 
	 * Server prodServer = new Server(); prodServer.setUrl(prodUrl);
	 * prodServer.setDescription("Server URL in Production environment");
	 * 
	 * Contact contact = new Contact(); contact.setEmail("ebxicash@gmail.com");
	 * contact.setName("ebxicash"); contact.setUrl("https://www.ebxicash.com");
	 * 
	 * License mitLicense = new
	 * License().name("MIT License").url("https://ebxicash.com/licenses/mit/");
	 * 
	 * Info info = new Info() .title("Aayu  API") .version("1.0") .contact(contact)
	 * .description("This API exposes endpoints for aayu.").termsOfService(
	 * "https://www.ebxicash.com/terms"); // .license(mitLicense);
	 * 
	 * return new OpenAPI().info(info).servers(List.of(devServer, prodServer)); }
	 */
}
