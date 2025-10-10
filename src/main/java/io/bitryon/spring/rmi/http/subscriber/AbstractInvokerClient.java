package io.bitryon.spring.rmi.http.subscriber;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClient.RequestHeadersSpec.ConvertibleClientHttpResponse;
import org.springframework.web.util.UriBuilder;

/**
 * major class to exchange request payload to response object
 * 
 * @param <T> generic type
 */
public abstract class AbstractInvokerClient<T> {

//	private static final ObjectMapper objectMapper = JsonMapper.builder()
//		.serializationInclusion(JsonInclude.Include.NON_NULL)
//		.build();
//	
//	public static final Consumer<List<HttpMessageConverter<?>>> HttpMessageConverter = (converters) -> {
//        MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter(objectMapper);
//        converters.removeIf(c -> c instanceof MappingJackson2HttpMessageConverter);
//        converters.add(jsonConverter);
//    };
	
	/**
	 * The template to get URL and advisor etc.
	 */
	protected final ServiceClientTemplate<T> serviceClientTemplate;
	
	/**
	 * constructor 
	 * 
	 * @param serviceClientTemplate to get URL and restclient etc.
	 */
	public AbstractInvokerClient(ServiceClientTemplate<T> serviceClientTemplate) {
		this.serviceClientTemplate = serviceClientTemplate;
	}

	/**
	 * For GET, URL query parameters
	 * 
	 * @param path				path
	 * @param queryVariables	Query variables on the URL
	 * @param typeRef			Type convert to
	 * @return T 				The object by typeRef
	 */
	protected T get(String path, Map<String, Object> queryVariables, ParameterizedTypeReference<T> typeRef) {
		try {
			return getRestClient(serviceClientTemplate)
					.get()
					.uri(builder ->  addUriBuilder(builder, path, queryVariables).build())
//					.headers(headers -> addHeaders(serviceClientTemplate.getRequestHttpHeaderTransporter(), headers))
					.accept(MediaType.APPLICATION_JSON)
					.exchange((request, response) -> exchange(serviceClientTemplate, request, response, typeRef));
		}catch(Exception e) {
			return serviceClientTemplate.handleErrorResponse(null, e);
		}
	}

	/**
	 * For POST, No URL query parameters
	 * 
	 * @param path				path
	 * @param queryVariables	Query variables on the URL
	 * @param formData			Form data
	 * @param typeRef			Type convert to
	 * @return T 				The object by typeRef
	 */
	protected T post(String path, Map<String, Object> queryVariables, Map<String, Object> formData, ParameterizedTypeReference<T> typeRef) {
		try {
			return getRestClient(serviceClientTemplate)
					.post()
					.uri(builder ->  addUriBuilder(builder, path, queryVariables).build())
//					.headers(headers -> addHeaders(serviceClientTemplate.getRequestHttpHeaderTransporter(), headers))
					.accept(MediaType.APPLICATION_JSON)
					.contentType(MediaType.APPLICATION_JSON)
					.body(formData)
					.exchange((request, response) -> exchange(serviceClientTemplate, request, response, typeRef));
		}catch(Exception e) {
			return serviceClientTemplate.handleErrorResponse(null, e);
		}
	}

	
	static UriBuilder addUriBuilder(UriBuilder builder, String path, Map<String, Object> queryVariables) {
		builder.path(path);
		for (Map.Entry<String, Object> entry : queryVariables.entrySet()) {
			Object obj = entry.getValue();
			if (obj instanceof String) {
				String val = (String) obj;
				try {
					val = URLEncoder.encode(val, "UTF-8");
					builder.queryParam(entry.getKey(), val);
				} catch (UnsupportedEncodingException e) {
					throw new RuntimeException(e);
				}
			}else {
				builder.queryParam(entry.getKey(), obj);
			}
		}
		return builder;
	}
	
//	static HttpHeaders addHeaders(HttpHeaderTransporter requestHeaderTransporter, HttpHeaders headers) {
//		if (requestHeaderTransporter!=null) {
//			Map<String, List<String>> httpHeaders = requestHeaderTransporter.getHttpHeaders();
//			if (httpHeaders!=null) {
//				for(Map.Entry<String, List<String>> entry : httpHeaders.entrySet()) {
//					if (entry.getValue() == null) { continue; }
//					headers.addAll(entry.getKey(), entry.getValue());
//				}
//			}
//		}
//		return headers;
//	}
	
	static RestClient getRestClient(ServiceClientTemplate<?> serviceClientTemplate) {
		RestClient restClient = serviceClientTemplate.getRestClient();
		if (restClient!=null) {
			return restClient;
		}else {
			return RestClient
				.builder().requestFactory(new HttpComponentsClientHttpRequestFactory())
				.defaultStatusHandler(null)
				.baseUrl(serviceClientTemplate.getBaseUrl())
//	            .messageConverters(HttpMessageConverter)
				.build();
		}
	}
	
	static <T> T exchange(ServiceClientTemplate<T> serviceClientTemplate, 
			HttpRequest request, ConvertibleClientHttpResponse response, 
			ParameterizedTypeReference<T> typeRef) throws IOException {
//		HttpHeaderTransporter responseHeaderTransporter = serviceClientTemplate.getResponseHttpHeaderTransporter();
//		if (responseHeaderTransporter!=null) {
//			responseHeaderTransporter.setHttpHeaders(response.getHeaders());
//		}
		if (response.getStatusCode().isError()) {
//			if (serviceClientTemplate.getErrorHandler()!=null) {
//				serviceClientTemplate.getErrorHandler().handle(request, response);
//			}
			return serviceClientTemplate.handleErrorResponse(response.getStatusCode().value(), null);
		} else {
			try {
				return response.bodyTo(typeRef);
			}catch(Exception e) {
				return serviceClientTemplate.handleErrorResponse(response.getStatusCode().value(), e);
			}
		}
	}
}
