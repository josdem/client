package com.all.client;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ContextFactory {
	static Log log = LogFactory.getLog(ContextFactory.class);
	//TODO in an common manager interface register each manager's context to be loaded
	static final String[] CONTEXTS = { "/context/download-manager.xml", "/context/socketdownload.xml",
			"/context/phexcore.xml", "/context/turndownloader.xml" };

	public ApplicationContext create() {
		Collection<String> availableContexts = new ArrayList<String>();  
		
		for (String context : CONTEXTS) {
			InputStream resource = this.getClass().getResourceAsStream(context);
			if(resource != null) {
				log.debug("Found context to load: " + context);
				
				availableContexts.add(context);
			} else {
				log.warn("Unable to find context: " + context);
			}
		}

		if(availableContexts.isEmpty()) {
			throw new IllegalStateException("No contexts were found to");
		}
		
		String[] contexts = availableContexts.toArray(new String[availableContexts.size()]);
		
		ClassPathXmlApplicationContext classPathXmlApplicationContext = new ClassPathXmlApplicationContext(contexts);
		return classPathXmlApplicationContext;
	}
	
	public static void main(String[] args) {
		new ContextFactory().create();
	}
}
