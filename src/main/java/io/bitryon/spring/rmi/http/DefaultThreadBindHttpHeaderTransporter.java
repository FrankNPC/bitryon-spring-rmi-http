package io.bitryon.spring.rmi.http;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultThreadBindHttpHeaderTransporter implements HttpHeaderTransporter {
	
	protected ThreadLocal<Map<String, List<String>>> httpHeaderThreadLocal = new ThreadLocal<>();
	
	@Override
	public void reset() {
		httpHeaderThreadLocal.remove();
	}

	@Override
	public Map<String, List<String>> getHttpHeaders() {
		Map<String, List<String>> headers = httpHeaderThreadLocal.get();
		return headers;
	}
	
	@Override
	public void setHttpHeaders(Map<String, List<String>> httpHeaders) {
		Map<String, List<String>> headers = httpHeaderThreadLocal.get();
		if (headers==null) {
			httpHeaderThreadLocal.set(headers = new HashMap<>());
		}
		headers.putAll(httpHeaders);
	}

	@Override
	public void addHttpHeader(String headerKey, String headerValue) {
		Map<String, List<String>> httpHeaders = httpHeaderThreadLocal.get();
		if (httpHeaders==null) {
			httpHeaderThreadLocal.set(httpHeaders = new HashMap<>());
		}
		List<String> exsitingHeaderValues = httpHeaders.get(headerKey);
		if (exsitingHeaderValues==null) {
			httpHeaders.put(headerKey, exsitingHeaderValues = new ArrayList<>());
		}
		exsitingHeaderValues.add(headerValue);
	}
}
