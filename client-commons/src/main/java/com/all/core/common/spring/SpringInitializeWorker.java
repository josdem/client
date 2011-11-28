package com.all.core.common.spring;

import java.lang.reflect.Method;
import java.util.List;

public class SpringInitializeWorker implements Runnable {

	private final Object bean;
	private final List<Method> methods;

	public SpringInitializeWorker(Object bean, List<Method> methods) {
		this.bean = bean;
		this.methods = methods;
	}

	@Override
	public void run() {
		for (Method method : methods) {
			runMethod(bean, method);
		}
	}

	private void runMethod(Object bean, Method method) {
		try {
			method.invoke(bean);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
