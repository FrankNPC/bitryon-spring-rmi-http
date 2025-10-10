package io.bitryon.spring.rmi.http;

import java.util.List;
import java.util.Map;

public interface HttpHeaderTransporter {
	
	void reset();
	
	Map<String, List<String>> getHttpHeaders();
	
	void setHttpHeaders(Map<String, List<String>> headers);

	void addHttpHeader(String headerKey, String headerValue);

}
