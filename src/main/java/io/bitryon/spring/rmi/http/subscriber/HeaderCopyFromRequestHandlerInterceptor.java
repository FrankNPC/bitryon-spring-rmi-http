package io.bitryon.spring.rmi.http.subscriber;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.lang.Nullable;
import org.springframework.web.servlet.HandlerInterceptor;

import io.bitryon.spring.rmi.http.HttpHeaderTransporter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Copy the headers from http request header to the next http request with the transporter, so we can pass the headers across services
 */
public class HeaderCopyFromRequestHandlerInterceptor implements HandlerInterceptor {

	private final List<String> headersForCopy;

	private final HttpHeaderTransporter requestHttpHeaderTransporter;

	public HeaderCopyFromRequestHandlerInterceptor(
			HttpHeaderTransporter requestHttpHeaderTransporter, 
			List<String> headersForCopy) {
		this.requestHttpHeaderTransporter = requestHttpHeaderTransporter;
		this.headersForCopy = headersForCopy;
	}
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		Map<String, List<String>> headValues = new HashMap<>();
		for(String headerKey : headersForCopy) {
			Enumeration<String> headers = request.getHeaders(headerKey);
			if (headers!=null) {
				List<String> values = new ArrayList<>();
				while(headers.hasMoreElements()) {
					values.add(headers.nextElement());
				}
			}
		}
		requestHttpHeaderTransporter.setHttpHeaders(headValues);
		return true;
	}

//	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
//			@Nullable ModelAndView modelAndView) throws Exception {
//		Map<String, List<String>> headValues = new HashMap<>();
//		for(String headerKey : headersForCopy) {
//			Enumeration<String> headers = request.getHeaders(headerKey);
//			if (headers!=null) {
//				List<String> values = new ArrayList<>();
//				while(headers.hasMoreElements()) {
//					values.add(headers.nextElement());
//				}
//			}
//		}
//		responseHttpHeaderTransporter.addHttpHeaders(headValues);
//	}
	
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
			@Nullable Exception ex) throws Exception {
		requestHttpHeaderTransporter.reset();
	}
	
}
