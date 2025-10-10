package io.bitryon.spring.rmi.http;

import java.lang.annotation.Annotation;
import java.util.Set;
import java.util.stream.Stream;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;

import io.bitryon.spring.rmi.http.prodiver.Provider;

public class BeanHelper {
	
	public static boolean detectAnnotationByName(Annotation[] annotations, Set<String> annotationNames) {
		return Stream.of(annotations)
			.anyMatch(anno->annotationNames.contains(anno.annotationType().getSimpleName()));
	}
	
	public static String parseServiceName(String serviceName) {
		serviceName = PropertyNamingStrategies.SnakeCaseStrategy.INSTANCE.translate(serviceName);
		serviceName = serviceName.replaceAll("_service_impl$", "");
		serviceName = serviceName.replaceAll("_impl$", "");
		serviceName = serviceName.replaceAll("_service$", "");
		return serviceName;
	}

	public static String parseMethodName(String serviceName, String methodName) {
		methodName = PropertyNamingStrategies.SnakeCaseStrategy.INSTANCE
				.translate(methodName);
		return serviceName + "/" + methodName;
	}
	
	public static String parseMethodName(Provider methodProvider, String serviceName, String methodName) {
		return methodProvider.value() + parseMethodName(serviceName, methodName);
	}
}
