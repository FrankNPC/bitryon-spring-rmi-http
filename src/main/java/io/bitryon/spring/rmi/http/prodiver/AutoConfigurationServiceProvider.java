package io.bitryon.spring.rmi.http.prodiver;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.util.ClassUtils;

import io.bitryon.spring.rmi.http.helper.BeanHelper;

@Configuration
@Import({ServiceProviderController.class})
@ConditionalOnClass(AutoConfigurationServiceProvider.class)
public class AutoConfigurationServiceProvider implements BeanPostProcessor {

	private Map<String, InvokeTarget> beans = new HashMap<>();
	
	InvokeTarget getServiceInvokeTarget(String url) {
		return beans.get(url);
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) {
		initializeBeanToInvokeTarget(bean, beanName);
		return bean;
	}

	private synchronized void initializeBeanToInvokeTarget(Object bean, String beanName) {
		Map<String, InvokeTarget> prepBeans = new HashMap<>(beans);
		
		Class<?> clazz = ClassUtils.getUserClass(bean);
		String serviceName = BeanHelper.parseServiceName(beanName);
		Optional<Provider> classProvider = Stream.of(ClassUtils.getUserClass(bean).getAnnotations()).filter(anno->anno instanceof Provider).map(item->(Provider)item).findAny();
		
		Method[] methods = clazz.getDeclaredMethods();
		for (Method method : methods) {
			if (!Modifier.isStatic(method.getModifiers()) && !Modifier.isPrivate(method.getModifiers())) {
				
				Optional<Provider> methodProvider = Stream.of(method.getAnnotations()).filter(anno->anno instanceof Provider).map(item->(Provider)item).findFirst();
				if (methodProvider.isPresent()) {
					String path = BeanHelper.parseMethodName(methodProvider.get(), serviceName, method.getName());
					prepBeans.put(path, new InvokeTarget(bean, method));
				}else if (classProvider.isPresent()) {
					String path = BeanHelper.parseMethodName(classProvider.get(), serviceName, method.getName());
					prepBeans.put(path, new InvokeTarget(bean, method));
				}
			}
		}
		
		beans = prepBeans;
	}

}
