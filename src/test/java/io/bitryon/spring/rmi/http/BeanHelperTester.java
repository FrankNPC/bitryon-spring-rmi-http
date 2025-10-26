package io.bitryon.spring.rmi.http;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.bitryon.spring.rmi.http.helper.BeanHelper;
import io.bitryon.spring.rmi.http.prodiver.Provider;

@Provider("/api/v1")
public class BeanHelperTester {

	@Test
	public void test_ServiceProviderConfiguration() {
		String name = BeanHelper.parseServiceName("AccountServiceImpl");
		Assertions.assertEquals("account", name);

		Provider classProvider = (Provider) Stream.of(BeanHelperTester.class.getAnnotations()).findFirst().orElse(null);

		String method = BeanHelper.parseMethodName(classProvider, name, "loginByUserName");
		Assertions.assertEquals("/api/v1/account/login_by_user_name", method);
	}

	@Provider
	public void test_ServiceProviderConfiguration_detectAnnotationByName() {
		Set<String> annotationNames = new HashSet<>(Arrays.asList("Provider"));
		boolean isPresent = BeanHelper.detectAnnotationByName(
				BeanHelperTester.class.getAnnotations(), annotationNames);
		Assertions.assertTrue(isPresent);

		boolean isPresent1 = Arrays.asList(BeanHelperTester.class.getMethods())
			.stream().anyMatch(method->BeanHelper.detectAnnotationByName(
					method.getAnnotations(), annotationNames));
		Assertions.assertTrue(isPresent1);

		Set<String> annotationNames1 = new HashSet<>(Arrays.asList("id"));
		boolean isPresent2 = Arrays.asList(BeanHelperTester.class.getMethods())
			.stream().anyMatch(method->BeanHelper.detectAnnotationByName(
					method.getAnnotations(), annotationNames1));
		Assertions.assertFalse(isPresent2);
	}
}
