package com.all.client.sync;

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
import com.all.client.services.UserPreferenceService;
import com.all.core.common.bean.AllDataBase;
import com.all.core.common.bean.ModalUserDatabaseInitializer;
import com.all.core.common.services.ApplicationConfig;
import com.all.schemaupdater.SchemaUpdater;
import com.all.shared.model.ContactInfo;
import com.all.shared.model.SyncValueObject;

public class ContactLibraryContext {

	private final Log log = LogFactory.getLog(this.getClass());
	private boolean loaded;
	private final ContactInfo contact;
	private SyncHelper syncHelper;
	private SyncValueObject syncObject;
	private AllDataBase db;
	private GenericApplicationContext appContext;
	private UserPreferenceService contactPreferenceService;
	private LocalModelDao dao;

	public ContactLibraryContext(ContactInfo contact) {
		this.contact = contact;
	}

	public void load() {
		try {
			ApplicationConfig applicationConfig = new ApplicationConfig();
			SchemaUpdater.updateUserDatabaseSchema(applicationConfig.getDatabasePrefix() + contact.getEmail());
			log.info("Loading context for " + contact.getEmail() + " 's library.");
			appContext = new GenericApplicationContext(new ClassPathXmlApplicationContext(
					new String[] { "/spring/offLineLibraryContext.xml" }));

			ModalUserDatabaseInitializer configuration = appContext.getBean("configuration",
					ModalUserDatabaseInitializer.class);
			configuration.setMail(contact.getEmail());
			XmlBeanDefinitionReader beanReader = new XmlBeanDefinitionReader(appContext);
			beanReader.loadBeanDefinitions(new String[] { "/spring/userDataLayer.xml" });
			beanReader.loadBeanDefinitions(new String[] { "/spring/offLineLibraryLayer.xml" });
			appContext.refresh();

			db = new AllDataBase(appContext.getBean("userJdbcTemplate", SimpleJdbcTemplate.class), " USER:"
					+ contact.getEmail());
			dao = appContext.getBean("localModelDao", LocalModelDao.class);

			contactPreferenceService = new UserPreferenceService(dao);
			contactPreferenceService.initialize();
			syncHelper = appContext.getBean("syncHelper", SyncHelper.class);
			syncHelper.setLocalModelDao(dao);
			syncHelper.setUserPreferenceService(contactPreferenceService);
			syncObject = new SyncValueObject(contact.getEmail(), contactPreferenceService.getCurrentSnapshot(),
					contactPreferenceService.getCurrentDelta(), 0L);
			loaded = true;
		} catch (Exception e) {
			log.error(e, e);
		}
	}

	public void close() {
		db.close();
		appContext.close();
		syncObject = null;
		db = null;
		appContext = null;
		loaded = false;
	}

	public ContactRoot loadRoot() {
		if (loaded) {
			return new RemoteModelFactory().createRemoteLibrary(new LocalRoot(dao), contact);
		}
		throw new IllegalStateException("Trying to get contact root  before loading context.");
	}

	public SyncHelper getSyncHelper() {
		if (loaded) {
			return syncHelper;
		}
		throw new IllegalStateException("Trying to get sync Helper before loading context.");
	}

	public SyncValueObject getMergeRequest() {
		if (loaded) {
			return syncObject;
		}
		throw new IllegalStateException("Trying to get the SyncValueObject before loading context.");
	}

}
