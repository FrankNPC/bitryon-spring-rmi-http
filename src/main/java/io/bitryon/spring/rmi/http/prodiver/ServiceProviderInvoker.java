package io.bitryon.spring.rmi.http.prodiver;

import java.lang.reflect.Parameter;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.bitryon.spring.rmi.http.HttpHeaderTransporter;
import io.bitryon.spring.rmi.http.helper.MethodHandlerHelper;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class ServiceProviderInvoker {

	private static final ObjectMapper objectMapper = new ObjectMapper();
	private static final String BAD_REQUEST = "{\"code\": -1, \"message\": \"bad request: {}\", \"success\": false, \"resolve\": \"\"}";
	private static final String NOT_FOUND   = "{\"code\": -1, \"message\": \"not found: {}\", \"success\": false, \"resolve\": \"\"}";
	public static final String INTERNAL_SERVICE_ERROR = "{\"code\": -1, \"message\": \"Internal Servie Error\", \"success\": false, \"resolve\": \"\"}";

	private static final ErrorHandler DEFAULT_ERROR_HANDLER = new ErrorHandler() {
		public String handle(Throwable thrownException) {
			thrownException.printStackTrace();
			return  INTERNAL_SERVICE_ERROR.replace("\"resolve\": \"\"", "\"resolve\": \""+thrownException.getMessage()+"\"");
		}
	};
	
	@Resource
	@Lazy
	AutoConfigurationServiceProvider serviceProviderConfiguration;

    @Autowired(required = false)
    ErrorHandler errorHandler = DEFAULT_ERROR_HANDLER;

    @Autowired(required = false)
	HttpHeaderTransporter httpHeaderTransporter;
	
	public HttpHeaderTransporter getHttpHeaderTransporter() {
		return this.httpHeaderTransporter;
	}
	
	static String defaultCharSet = Charset.defaultCharset().name();
	
	public ErrorHandler getErrorHandler() {
		return errorHandler;
	}
	
	public ResponseEntity<String> get(HttpServletRequest request, HttpServletResponse response) throws Throwable {
		URI url = URI.create(request.getRequestURL().toString());
		String path = url.getPath();
		if (path==null || !path.matches("^/[a-zA-Z0-9_\\-]+/[a-zA-Z0-9_\\-]+.*$")) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.headers(getHttpHeaders()).body(BAD_REQUEST.replace("{}", path));
		}
		InvokeTarget invokeTarget = serviceProviderConfiguration.getServiceInvokeTarget(path);
		if (invokeTarget==null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.headers(getHttpHeaders()).body(NOT_FOUND.replace("{}", path));
		}
		
		Map<String, String[]> requestMapper = request.getParameterMap();
		Object[] requestParams = parseParameters(requestMapper, invokeTarget, null);
		Object ret = MethodHandlerHelper.invoke(invokeTarget.getMethod(), invokeTarget.getBean(), requestParams);
		return ResponseEntity.status(HttpStatus.OK).headers(getHttpHeaders()).body(objectMapper.writeValueAsString(ret));
	}
	
	public ResponseEntity<String> post(HttpServletRequest request, HttpServletResponse response,
								Map<String, Object> formBody) throws Throwable {
		URI url = URI.create(request.getRequestURL().toString());
		String path = url.getPath();
		if (path==null || !path.matches("^/[a-zA-Z0-9_\\-]+/[a-zA-Z0-9_\\-]+.*$")) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.headers(getHttpHeaders()).body(BAD_REQUEST.replace("{}", path));
		}
		InvokeTarget invokeTarget = serviceProviderConfiguration.getServiceInvokeTarget(path);
		if (invokeTarget==null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.headers(getHttpHeaders()).body(NOT_FOUND.replace("{}", path));
		}

		Map<String, String[]> requestMapper = request.getParameterMap();
		Object[] requestParams = parseParameters(requestMapper, invokeTarget, formBody);
		
		Object ret = MethodHandlerHelper.invoke(invokeTarget.getMethod(), invokeTarget.getBean(), requestParams);
		return ResponseEntity.status(HttpStatus.OK).headers(getHttpHeaders()).body(objectMapper.writeValueAsString(ret));
	}

	HttpHeaders getHttpHeaders() {
		HttpHeaders headers = new HttpHeaders();
		HttpHeaderTransporter httpHeaderTransporter = getHttpHeaderTransporter();
		if (httpHeaderTransporter!=null) {
			Map<String, List<String>> headerValues = httpHeaderTransporter.getHttpHeaders();
			if (headerValues!=null) {
				for(Map.Entry<String, List<String>> entry : headerValues.entrySet()) {
					headers.addAll(entry.getKey(), entry.getValue());
				}
			}
		}
		return headers;
	}
	
	static Object[] parseParameters(Map<String, String[]> requestMapper, InvokeTarget invokeTarget, Map<String, Object> formBody) throws Exception {
		Parameter[] params = invokeTarget.getMethod().getParameters();
		Object[] requestParams = new Object[params.length];
		for(int i=0; i<requestParams.length; i++) {
			String[] values = requestMapper.get(params[i].getName());
			if (values!=null) {
				Class<?> clazz = params[i].getType();
				if (values.length==1) {
					requestParams[i] = parseValue(clazz, values[0]);
				}else {
					Object[] objects = new Object[values.length];
					for(int k=0; k<values.length; k++) {
						objects[k] = parseValue(clazz, values[k]);
					}
					requestParams[i] = objects;
				}
			}else if (formBody!=null){
				Class<?> clazz = params[i].getType();
				Object obj = formBody.get(params[i].getName());
				requestParams[i] = parseValue(clazz, obj);
			}
		}
		return requestParams;
	}
	
	private static interface Converter {
		Object convert(String strValue, Class<?> toClass) throws Exception;
	}
	private static Map<Class<?>, Converter> converters = initiateConverters();
	static Map<Class<?>, Converter> initiateConverters(){
		Map<Class<?>, Converter> theConverters = new HashMap<>();
		theConverters.put(String.class, (strValue, toClass) -> strValue.isEmpty() ? "" : String.valueOf(strValue));
		theConverters.put(Void.class, (strValue, toClass) -> null);
		theConverters.put(void.class, (strValue, toClass) -> null);
		theConverters.put(Boolean.class, (strValue, toClass) -> strValue.isEmpty() ? false : Boolean.parseBoolean(strValue));
		theConverters.put(boolean.class, (strValue, toClass) -> strValue.isEmpty() ? false : Boolean.parseBoolean(strValue));
		theConverters.put(Byte.class, (strValue, toClass) -> strValue.isEmpty() ? (byte)0 : Byte.parseByte(strValue));
		theConverters.put(byte.class, (strValue, toClass) -> strValue.isEmpty() ? (byte)0 : Byte.parseByte(strValue));
		theConverters.put(Character.class, (strValue, toClass) -> strValue.isEmpty() ? '0' : strValue.charAt(0));
		theConverters.put(char.class, (strValue, toClass) -> strValue.isEmpty() ? '0' : strValue.charAt(0));
		theConverters.put(Short.class, (strValue, toClass) -> strValue.isEmpty() ? (short)0 : Short.parseShort(strValue));
		theConverters.put(short.class, (strValue, toClass) -> strValue.isEmpty() ? (short)0 : Short.parseShort(strValue));
		theConverters.put(Integer.class, (strValue, toClass) -> strValue.isEmpty() ? 0 : Integer.parseInt(strValue));
		theConverters.put(int.class, (strValue, toClass) -> strValue.isEmpty() ? 0 : Integer.parseInt(strValue));
		theConverters.put(Long.class, (strValue, toClass) -> strValue.isEmpty() ? 0L : Long.parseLong(strValue));
		theConverters.put(long.class, (strValue, toClass) -> strValue.isEmpty() ? 0L : Long.parseLong(strValue));
		theConverters.put(Float.class, (strValue, toClass) -> strValue.isEmpty() ? 0F : Float.parseFloat(strValue));
		theConverters.put(float.class, (strValue, toClass) -> strValue.isEmpty() ? 0F : Float.parseFloat(strValue));
		theConverters.put(Double.class, (strValue, toClass) -> strValue.isEmpty() ? 0D : Double.parseDouble(strValue));
		theConverters.put(double.class, (strValue, toClass) -> strValue.isEmpty() ? 0D : Double.parseDouble(strValue));
		return theConverters;
	}

	static Object parseValue(Class<?> clazz, Object value) throws Exception {
		if (value==null) {
			return null;
		}
		Converter converter = converters.get(clazz);
		if (converter==null) {
			return objectMapper.convertValue(value, clazz);
		}else {
			return converter.convert(String.valueOf(value), clazz);
		}
	}
	
}
