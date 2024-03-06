package com.ebixcash.aayu.defaultsetting;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.w3c.dom.Document;

import com.ebixcash.aayu.customvalidator.Validations;
import com.ebixcash.aayu.exception.AmortException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class DefaultSettingValues {
	 @Value("${AAYU_DEFAULTVALUES_XML_PATH}")
	 private static String pathLoc;
	static Document document = null;

	private static JsonNode jsonNode;

	private static JsonNode getDefaultValueJsonNode() {
		try {
			// pathLoc = aayu_defaultvalues_json_path;
			Resource resource = null;

			ObjectMapper objectMapper = new ObjectMapper();
			resource = new ClassPathResource("AayuDefaultValues.json");
			InputStream inputStream = resource.getInputStream();
			byte[] bytes = StreamUtils.copyToByteArray(inputStream);
			String aayuJson = new String(bytes, StandardCharsets.UTF_8);
			jsonNode =  objectMapper.readTree(aayuJson);
		} catch (IOException e) {
			e.printStackTrace();
			try {
				pathLoc = "D:/IndusJDE/jboss-5.1.0.GA/server/default/deploy/csf.ear/csfweb.war/WEB-INF/aayuConfig/AayuDefaultValues.json";
				ObjectMapper objectMapper = new ObjectMapper();
				jsonNode = objectMapper.readTree(pathLoc);
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return jsonNode;
	}

	
	public static Map<String, Object> getDefaultValues(Map<String, Object> amortMap) {
		Map<String, Object> resultValues = new HashMap<>();

		try {

			if (jsonNode == null)
				getDefaultValueJsonNode();

			if (jsonNode != null) {
				JsonNode emiAdjNode = jsonNode.get("EMI_ADJ");
				/*
				 * for (JsonNode childRootNode : jsonNode) { if
				 * ("EMI_ADJ".equals(childRootNode.fieldNames().next())) { JsonNode childNode1 =
				 * childRootNode.get("EMI_ADJ"); for (JsonNode childRootNode1 : childNode1) {
				 * String nodeName = childRootNode1.fieldNames().next(); if
				 * (!amortMap.containsKey(nodeName)) { amortMap.put(nodeName,
				 * childRootNode1.asText()); } } } else { String nodeName =
				 * childRootNode.fieldNames().next(); if (!amortMap.containsKey(nodeName) ||
				 * String.valueOf(amortMap.get(nodeName)).equals("null")) {
				 * amortMap.put(nodeName, childRootNode.asText()); } } }
				 */
				if (emiAdjNode != null && emiAdjNode.isObject()) {
				    Iterator<Map.Entry<String, JsonNode>> fieldsIterator = emiAdjNode.fields();
				    while (fieldsIterator.hasNext()) {
				        Map.Entry<String, JsonNode> field = fieldsIterator.next();
				        String nodeName = field.getKey();
				        JsonNode nodeValue = field.getValue();
				        if (!amortMap.containsKey(nodeName)) {
				            amortMap.put(nodeName, nodeValue.asText());
				        }
				    }
				}

				// Now handle other fields
				Iterator<Map.Entry<String, JsonNode>> fieldsIterator = jsonNode.fields();
				while (fieldsIterator.hasNext()) {
				    Map.Entry<String, JsonNode> field = fieldsIterator.next();
				    String nodeName = field.getKey();
				    JsonNode nodeValue = field.getValue();
				    if (!"EMI_ADJ".equals(nodeName) && (!amortMap.containsKey(nodeName) || String.valueOf(amortMap.get(nodeName)).equals("null"))) {
				        amortMap.put(nodeName, nodeValue.asText());
				    }
				}
			}
		} catch (Exception exp) {
			exp.printStackTrace();
			jsonNode = null;
			amortMap.clear();
		}

		try {
			if (amortMap != null && amortMap.size() > 0)
				resultValues = Validations.getValidate(amortMap);
		} catch (AmortException exp1) {
			exp1.printStackTrace();
		} catch (Exception exp1) {
			exp1.printStackTrace();
		}

		return resultValues;
	}

}
