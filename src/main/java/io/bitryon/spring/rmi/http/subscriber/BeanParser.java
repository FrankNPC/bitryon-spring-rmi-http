package io.bitryon.spring.rmi.http.subscriber;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;

import io.bitryon.spring.rmi.http.helper.BeanHelper;

class BeanParser {

	protected class FormKeys {
		protected String path;
		protected String[] primativeKeys;
		protected String[] formKeys;
		public FormKeys(String p, String[] pks, String[] fks){
			path = p;
			primativeKeys = pks;
			formKeys = fks;
		}
	}
	
	protected Map<Method, FormKeys> pathMap = new HashMap<>();
	
	protected FormKeys parseForm(Method method) {
		FormKeys form = pathMap.get(method);
		if (form==null) {
			form = initFormKeys(method);
		}
		return form;
	}
	private synchronized FormKeys initFormKeys(Method method){
		FormKeys form = pathMap.get(method);
		if (form==null) {
			String serviceName = method.getDeclaringClass().getSimpleName();
			serviceName = serviceName.substring(serviceName.lastIndexOf(".")+1);
			serviceName = BeanHelper.parseServiceName(serviceName);

			String path = BeanHelper.parseMethodName(serviceName, method.getName());
			
			if (!path.matches("^[a-zA-Z0-9_\\\\-]+/[a-zA-Z0-9_\\\\-]+.*$")) {
				throw new RuntimeException("Invalid service name. It should be [/_0-9a-z\\-].");
			}

			Parameter[] params = method.getParameters();
			String[] primativeKeys = new String[params.length];
			String[] formKeys = new String[params.length];
			
			for(int i=0; i<params.length; i++) {
				String key = params[i].getName();
//						key = PropertyNamingStrategies.SnakeCaseStrategy.INSTANCE.translate(key);
				if (params[i].getType().isPrimitive()) {
					primativeKeys[i] = key;
				}else {
					formKeys[i] = key;
				}
			}
			form = new FormKeys(path, primativeKeys, formKeys);

			Map<Method, FormKeys> prePathMap = new HashMap<>(pathMap);
			prePathMap.put(method, form);
			pathMap = prePathMap;
		}
		return form;
	}

//	private static final boolean isWrapperOrStringType(Class<?> clazz) {
//		return clazz.isPrimitive()
//				|| clazz.equals(String.class);
//	}
	
}
