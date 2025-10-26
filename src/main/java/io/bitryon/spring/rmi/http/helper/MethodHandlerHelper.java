package io.bitryon.spring.rmi.http.helper;


import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class MethodHandlerHelper {

	private static MethodHandle toMethodHandle(Method method) {
		method.setAccessible(true);
		MethodHandles.Lookup lookup = MethodHandles.lookup();
		try {
			return lookup.unreflect(method);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	private static Map<Method, MethodHandle> methods = Collections.emptyMap();
	public static MethodHandle getMethodHandler(Method method) {
		MethodHandle methodHandle = methods.get(method);
		if (methodHandle==null) {
			methodHandle = loadMethodHandle(method);
		}
		return methodHandle;
	}
	
	public static Object invoke(Method method, Object instance, Object...args) {
		if (!java.lang.reflect.Modifier.isStatic(method.getModifiers())) {
			Object[] fullArgs = new Object[args.length + 1];
			fullArgs[0] = instance;
			System.arraycopy(args, 0, fullArgs, 1, args.length);

			try {
				return getMethodHandler(method).invokeWithArguments(fullArgs);
			} catch (Throwable e) {
				try {
					return method.invoke(instance, args);
				} catch (IllegalAccessException | InvocationTargetException e1) {
					new RuntimeException(e);
				}
			}
		} else {
			try {
				return getMethodHandler(method).invokeWithArguments(args);
			} catch (Throwable e) {
				try {
					return method.invoke(instance, args);
				} catch (IllegalAccessException | InvocationTargetException e1) {
					new RuntimeException(e);
				}
			}
		}
		return null;
	}

	private static synchronized MethodHandle loadMethodHandle(Method method){
		MethodHandle methodHandle = methods.get(method);
		if (methodHandle==null) {
			methodHandle = toMethodHandle(method);
			
			Map<Method, MethodHandle> copyMethods = new HashMap<>(methods);
			copyMethods.put(method, methodHandle);
			methods = Collections.unmodifiableMap(copyMethods);
		}
		return methodHandle;
	}
	
}