package io.bitryon.spring.rmi.http.prodiver;

import java.lang.reflect.Method;

class InvokeTarget {
	private Object bean;
	private Method method;

	public InvokeTarget(Object b, Method m) {
		bean = b;
		method = m;
	}

	public Object getBean() {
		return bean;
	}

	public void setBean(Object bean) {
		this.bean = bean;
	}

	public Method getMethod() {
		return method;
	}

	public void setMethod(Method method) {
		this.method = method;
	}
}