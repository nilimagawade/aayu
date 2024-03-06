package com.ebixcash.aayu.aspects;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import com.ebixcash.aayu.model.RequestResponseLog;
import com.ebixcash.aayu.repositories.APIServiceLogsRepository;
import com.ebixcash.aayu.security.JwtTokenHelper;
import com.ebixcash.aayu.util.LocalDate;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AayuReqRespLoggingAspects extends OncePerRequestFilter {

	@Autowired
	private APIServiceLogsRepository apiServiceLogsRepository;

	@Autowired(required = true)
	private JwtTokenHelper jwtTokenHelper;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
			jakarta.servlet.FilterChain filterChain) throws ServletException, java.io.IOException {
		ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
		ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);

		filterChain.doFilter(requestWrapper, responseWrapper);

		String req = null;
		String requestBody = new String(requestWrapper.getContentAsByteArray());
		String responseBody = new String(responseWrapper.getContentAsByteArray());

		String requestToken = request.getHeader("Authorization");
		if (requestToken != null) {
			requestToken = requestToken.substring(7);
			req = jwtTokenHelper.getUsernameFromToken(requestToken);
		}

		RequestResponseLog apiServiceLogs = new RequestResponseLog();

		apiServiceLogs.setRequestId(UUID.randomUUID());
		apiServiceLogs.setServiceUrl(request.getRequestURL().toString());
		apiServiceLogs.setResponse(responseBody);
		apiServiceLogs.setCreationTime(LocalDate.convertNowToTimeStamp());
		apiServiceLogs.setToken(responseBody);
		apiServiceLogs.setStatus(response.getStatus());
		apiServiceLogs.setHttpMethod(request.getMethod());
		apiServiceLogs.setDtUpdated(LocalDate.convertNowToTimeStamp());
		apiServiceLogs.setRequest(requestBody);
		String method = request.getRequestURL().toString();
		//apiServiceLogs.setApiServiceName(method.substring(30));

		apiServiceLogsRepository.save(apiServiceLogs);

		responseWrapper.copyBodyToResponse();

	}
}
