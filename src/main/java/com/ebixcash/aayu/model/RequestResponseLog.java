package com.ebixcash.aayu.model;

import java.sql.Timestamp;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "t_api_service_logs", schema = "aayu")
public class RequestResponseLog {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private int id;

	@Column(name = "request_ID", columnDefinition = "text")
	private UUID requestId;

	@Column(name = "sz_token", columnDefinition = "text")
	private String token;

	@Column(name = "sz_api_service_name", columnDefinition = "text")
	private String apiServiceName;

	@Column(name = "sz_request", columnDefinition = "text")
	private String request;

	@Column(name = "sz_response", columnDefinition = "text")
	private String response;

	@Column(name = "sz_error", columnDefinition = "text")
	private String error;

	@Column(name = "sz_service_url", columnDefinition = "text")
	private String serviceUrl;

	@Column(name = "sz_status", columnDefinition = "text")
	private int status;

	@Column(name = "dt_created")
	private Timestamp CreationTime;

	@Column(name = "dt_updated", columnDefinition = "text")
	private Timestamp dtUpdated;

	@Column(name = "http_method", columnDefinition = "text")
	private String httpMethod;

	public String getHttpMethod() {
		return httpMethod;
	}

	public void setHttpMethod(String httpMethod) {
		this.httpMethod = httpMethod;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the requestId
	 */
	public UUID getRequestId() {
		return requestId;
	}

	/**
	 * @param uuid the requestId to set
	 */
	public void setRequestId(UUID uuid) {
		this.requestId = uuid;
	}

	/**
	 * @return the token
	 */
	public String getToken() {
		return token;
	}

	/**
	 * @param token the token to set
	 */
	public void setToken(String token) {
		this.token = token;
	}

	/**
	 * @return the apiServiceName
	 */
	public String getApiServiceName() {
		return apiServiceName;
	}

	/**
	 * @param apiServiceName the apiServiceName to set
	 */
	public void setApiServiceName(String apiServiceName) {
		this.apiServiceName = apiServiceName;
	}

	/**
	 * @return the request
	 */
	public String getRequest() {
		return request;
	}

	/**
	 * @param request the request to set
	 */
	public void setRequest(String request) {
		this.request = request;
	}

	/**
	 * @return the response
	 */
	public String getResponse() {
		return response;
	}

	/**
	 * @param response the response to set
	 */
	public void setResponse(String response) {
		this.response = response;
	}

	/**
	 * @return the error
	 */
	public String getError() {
		return error;
	}

	/**
	 * @param error the error to set
	 */
	public void setError(String error) {
		this.error = error;
	}

	/**
	 * @return the serviceUrl
	 */
	public String getServiceUrl() {
		return serviceUrl;
	}

	/**
	 * @param serviceUrl the serviceUrl to set
	 */
	public void setServiceUrl(String serviceUrl) {
		this.serviceUrl = serviceUrl;
	}

	/**
	 * @return the status
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(int status) {
		this.status = status;
	}

	public Timestamp getCreationTime() {
		return CreationTime;
	}

	public void setCreationTime(Timestamp creationTime) {
		CreationTime = creationTime;
	}

	public Timestamp getDtUpdated() {
		return dtUpdated;
	}

	public void setDtUpdated(Timestamp dtUpdated) {
		this.dtUpdated = dtUpdated;
	}

}