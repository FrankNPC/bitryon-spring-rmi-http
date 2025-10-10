package io.bitryon.spring.rmi.http.subscriber;

import org.springframework.aop.Advisor;
import org.springframework.web.client.RestClient;

public interface ServiceClientTemplate<T> {
	
	String getBaseUrl();
	
	/**
	 * Return a RestClient for Invoker to call.
	 * 
	 * @return
	 */
	default RestClient getRestClient() {return null;}

//	default HttpHeaderTransporter getResponseHttpHeaderTransporter() {return null;}
	
//	default ErrorHandler getErrorHandler() {return null;}

	default T handleErrorResponse(Integer httpCode, Exception exception) { return null;}
	
	/**
	 * can apply a pointcut to run logistics
	 * @return
	 */
	default Advisor[] getAdvisors() {return null;}
	
}
