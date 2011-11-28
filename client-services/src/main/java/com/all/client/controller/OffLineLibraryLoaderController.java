package com.all.client.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import com.all.client.model.ContactRoot;
import com.all.client.model.LocalModelDao;
import com.all.client.model.LocalRoot;
import com.all.client.model.RemoteModelFactory;
import com.all.client.services.ContactCacheService;
import com.all.client.services.UserPreferenceService;
import com.all.core.common.bean.AllDataBase;
import com.all.core.common.bean.ModalUserDatabaseInitializer;
import com.all.shared.model.ContactInfo;

public class OffLineLibraryLoaderController {

	private Log log = LogFactory.getLog(OffLineLibraryLoaderController.class);
	private GenericApplicationContext appContext;
	private LocalModelDao dao;
	private final String email;
	private final ContactCacheService contactCacheService;
	private ContactInfo contactInfo;
	private AllDataBase db;

	public OffLineLibraryLoaderController(ContactCacheService contactCacheService, String email) {
		this.contactCacheService = contactCacheService;
		this.email = email;
	}

	public UserPreferenceService prepareContextForOfflineLibrary() {
		try {

			log.info("loading context for offline library : " + email);

			appContext = new GenericApplicationContext(new ClassPathXmlApplicationContext(
					new String[] { "/spring/offLineLibraryContext.xml" }));

			ModalUserDatabaseInitializer configuration = appContext.getBean("configuration",
					ModalUserDatabaseInitializer.class);
			configuration.setMail(email);

			XmlBeanDefinitionReader beanReader = new XmlBeanDefinitionReader(appContext);
			beanReader.loadBeanDefinitions(new String[] { "/spring/userDataLayer.xml" });
			beanReader.loadBeanDefinitions(new String[] { "/spring/offLineLibraryLayer.xml" });
			appContext.refresh();

			db = new AllDataBase(appContext.getBean("userJdbcTemplate", SimpleJdbcTemplate.class), " USER:" + email);
			dao = appContext.getBean("localModelDao", LocalModelDao.class);

			UserPreferenceService userPreferenceService = new UserPreferenceService(dao);
			userPreferenceService.initialize();
			return userPreferenceService;
		} catch (Exception e) {
			log.error(e, e);
			return null;
		}
	}

	public ContactRoot loadOfflineLibrary() {
		ContactInfo offlineContactInfo = getOfflineContactInfo(email);
		return new RemoteModelFactory().createRemoteLibrary(new LocalRoot(dao), offlineContactInfo);
	}

	private ContactInfo getOfflineContactInfo(String email) {
		contactInfo = contactCacheService.findContactByEmail(email);
		return contactInfo;
	}

	public GenericApplicationContext getAppContext() {
		return appContext;
	}

	public ContactInfo getContactInfo() {
		return contactInfo;
	}

	public void close() {
		db.close();
		appContext.close();
	}
}
