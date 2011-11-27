package com.all.client.integration;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

import com.all.appControl.ActionMethod;
import com.all.appControl.AppAnnotationUtils;
import com.all.appControl.RequestField;
import com.all.appControl.RequestMethod;
import com.all.event.EventMethod;
import com.all.messengine.MessageMethod;

public class TestSpringEngineDiagnosticProcessor {
	private final static Log log = LogFactory.getLog(TestSpringEngineDiagnosticProcessor.class);

	@Test
	public void shouldTestEngineSanity() throws Exception {
		for (Class<?> clazz : getClasses("com.all")) {
			checkEngines(clazz);
		}
	}

	private void checkEngines(Class<?> clazz) {
		int actions = AppAnnotationUtils.getMethods(clazz, ActionMethod.class).size();
		int requests = AppAnnotationUtils.getMethods(clazz, RequestMethod.class).size();
		int requestFields = AppAnnotationUtils.getFields(clazz, RequestField.class).size();
		int events = AppAnnotationUtils.getMethods(clazz, EventMethod.class).size();
		int messages = AppAnnotationUtils.getMethods(clazz, MessageMethod.class).size();
		if ((actions + requests + requestFields + messages) > 0 && events > 0) {
			fail("Class " + clazz.getName() + " HAS INCORRECT BINDINGS Mixes events with messages and actions.");
		}
		if ((actions + requests + requestFields + messages + events) > 0) {
			log.info("Class: " + clazz.getName() + " actions:" + actions + " requests:" + requests + " requestFields:"
					+ requestFields + " messages:" + messages + " events:" + events);
		}
	}

	/**
	 * Scans all classes accessible from the context class loader which belong to
	 * the given package and subpackages.
	 * 
	 * @param packageName
	 *          The base package
	 * @return The classes
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	private static List<Class<?>> getClasses(String packageName) throws ClassNotFoundException, IOException {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		assertNotNull(classLoader);
		String path = packageName.replace('.', '/');
		Enumeration<URL> resources = classLoader.getResources(path);
		List<File> dirs = new ArrayList<File>();
		while (resources.hasMoreElements()) {
			URL resource = resources.nextElement();
			dirs.add(new File(resource.getFile()));
		}
		ArrayList<Class<?>> classes = new ArrayList<Class<?>>();
		for (File directory : dirs) {
			classes.addAll(findClasses(directory, packageName));
		}
		return classes;
	}

	/**
	 * Recursive method used to find all classes in a given directory and subdirs.
	 * 
	 * @param directory
	 *          The base directory
	 * @param packageName
	 *          The package name for classes found inside the base directory
	 * @return The classes
	 * @throws ClassNotFoundException
	 */
	private static List<Class<?>> findClasses(File directory, String packageName) throws ClassNotFoundException {
		List<Class<?>> classes = new ArrayList<Class<?>>();
		if (!directory.exists()) {
			return classes;
		}
		File[] files = directory.listFiles();
		for (File file : files) {
			if (file.isDirectory()) {
				assert !file.getName().contains(".");
				classes.addAll(findClasses(file, packageName + "." + file.getName()));
			} else if (file.getName().endsWith(".class")) {
				classes.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
			}
		}
		return classes;
	}
}
