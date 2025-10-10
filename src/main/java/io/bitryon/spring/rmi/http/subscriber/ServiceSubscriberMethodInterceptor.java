package io.bitryon.spring.rmi.http.subscriber;


import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.cglib.proxy.Callback;
import org.springframework.core.ParameterizedTypeReference;

import io.bitryon.spring.rmi.http.subscriber.BeanParser.FormKeys;

public class ServiceSubscriberMethodInterceptor<T> extends AbstractInvokerClient<T> implements MethodInterceptor, Callback {
	
	private BeanParser beanParser = new BeanParser();
	
	public ServiceSubscriberMethodInterceptor(ServiceClientTemplate<T> serviceClientTemplate) {
		super(serviceClientTemplate);
	}
	
	@Override
	public T invoke(MethodInvocation invocation) throws Throwable {
		Method method = invocation.getMethod();
		FormKeys form = beanParser.parseForm(method);

		Map<String, Object> queryVariables = new HashMap<>();
		Map<String, Object> formData = new HashMap<>();

		Object[] objects = invocation.getArguments();
		for(int i=0; i<form.primativeKeys.length; i++) {
			if (objects[i]==null) { continue; }
			if (form.primativeKeys[i]!=null) {
				queryVariables.put(form.primativeKeys[i], objects[i]);
			}else {
				formData.put(form.formKeys[i], objects[i]);
			}
		}
		
		if (!formData.isEmpty()) {
			return super.post(form.path, queryVariables, formData, ParameterizedTypeReference.forType(method.getGenericReturnType()));
		}else {
			return super.get(form.path, queryVariables, ParameterizedTypeReference.forType(method.getGenericReturnType()));
		}
	}
	

}
