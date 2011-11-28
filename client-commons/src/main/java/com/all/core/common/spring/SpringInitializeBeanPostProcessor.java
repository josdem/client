package com.all.core.common.spring;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.annotation.PreDestroy;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;

import com.all.commons.IncrementalNamedThreadFactory;

public class SpringInitializeBeanPostProcessor implements BeanPostProcessor, ApplicationContextAware,
		ApplicationListener<ContextRefreshedEvent> {
	private final PausableExecutor executorService;
	private List<Future<?>> futures = new ArrayList<Future<?>>();

	public SpringInitializeBeanPostProcessor() {
		IncrementalNamedThreadFactory threadFactory = new IncrementalNamedThreadFactory("SpringInitializer");
		executorService = new PausableExecutor(threadFactory);
		executorService.pause();
	}

	@PreDestroy
	public void destroy() {
		executorService.shutdownNow();
	}

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		List<Method> methods = getInitializableMethods(bean);
		if (!methods.isEmpty()) {
			futures.add(executorService.submit(new SpringInitializeWorker(bean, methods)));
		}
		return bean;
	}

	private List<Method> getInitializableMethods(Object bean) {
		List<Method> methods = new ArrayList<Method>(0);
		Method[] declaredMethods = bean.getClass().getDeclaredMethods();
		for (Method method : declaredMethods) {
			if (isInitializable(method)) {
				methods.add(method);
			}
		}
		return methods;
	}

	public void awaitInitialization() throws ExecutionException, InterruptedException {
		for (Future<?> future : futures) {
			future.get();
		}
	}

	public static boolean isInitializable(Method method) {
		boolean annotationPresent = method.isAnnotationPresent(InitializeService.class);
		boolean noArguments = method.getParameterTypes().length == 0;
		boolean isVoid = method.getReturnType().equals(void.class);
		boolean isPublic = (method.getModifiers() & Modifier.PUBLIC) == Modifier.PUBLIC;
		boolean isNotStatic = (method.getModifiers() & Modifier.STATIC) != Modifier.STATIC;
		return annotationPresent && noArguments && isPublic && isNotStatic && isVoid;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		if (applicationContext instanceof ConfigurableApplicationContext) {
			ConfigurableApplicationContext appContext = (ConfigurableApplicationContext) applicationContext;
			appContext.addApplicationListener(this);
		} else {
			executorService.resume(60000);
		}
	}

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		executorService.resume(1000);
	}

}
