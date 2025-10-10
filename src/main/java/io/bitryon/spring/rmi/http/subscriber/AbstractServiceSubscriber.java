package io.bitryon.spring.rmi.http.subscriber;

import org.springframework.aop.framework.ProxyFactoryBean;

public abstract class AbstractServiceSubscriber {
	/**
	 * Create subscriber to the method from ServiceTemplate.
	 * 
	 * @param clazz					The class of return type
	 * @param serviceClientTemplate	Base host url
	 */
	@SuppressWarnings("unchecked")
	protected <T> T getProxyFactoryBean(Class<T> clazz, ServiceClientTemplate<?> serviceClientTemplate) {
		ProxyFactoryBean factory = new ProxyFactoryBean();
		factory.addInterface(clazz);
		if (serviceClientTemplate.getAdvisors()!=null) {
			factory.addAdvisors(serviceClientTemplate.getAdvisors());
		}
		factory.addAdvice(new ServiceSubscriberMethodInterceptor<>(serviceClientTemplate));
		return (T) factory.getObject();
	}
}
