package com.ebixcash.aayu.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.StreamUtils;

import com.ebixcash.aayu.constant.AmortConstant;
import com.ebixcash.aayu.model.AmortInputBean;
import com.ebixcash.aayu.serviceImpl.TotalInterestServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;

@AutoConfigureMockMvc
@SpringBootTest
public class TotalInterestControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	TotalInterestServiceImpl interestServiceImpl;

	private final ObjectMapper objectMapper = new ObjectMapper();

	@Test
	void getTotalIntrest() throws Exception {

		// Load JSON request from file
		String inputJson = loadJSONRequestFromFile("TotalInterestRequest.json");
		String outputJson = loadJSONRequestFromFile("TotalInterestResponse.json");

		AmortInputBean amortInputBean = objectMapper.readValue(inputJson, AmortInputBean.class);

		// Perform the request and expect status OK
		MvcResult mvcResult = mockMvc
				.perform(MockMvcRequestBuilders.post("/aayu/v1/totalIntrest").contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(amortInputBean)))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

		String responseJson = mvcResult.getResponse().getContentAsString();
		ArrayList<Object> actualResponse = objectMapper.readValue(responseJson, new ArrayList<Object>().getClass());
		ArrayList<Object> expectedResponse = objectMapper.readValue(outputJson, new ArrayList<Object>().getClass());

		// Compare the expected and actual responses
		assertEquals(expectedResponse, actualResponse);

	}

	private String loadJSONRequestFromFile(String filename) throws IOException {
		Resource resource = null;
		if (filename.contains(AmortConstant.request)) {
			resource = new ClassPathResource(AmortConstant.request_folder + filename);
		} else {
			resource = new ClassPathResource(AmortConstant.response_folder + filename);
		}
		InputStream inputStream = resource.getInputStream();
		byte[] bytes = StreamUtils.copyToByteArray(inputStream);
		return new String(bytes, StandardCharsets.UTF_8);
	}

}
