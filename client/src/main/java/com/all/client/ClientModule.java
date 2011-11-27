package com.all.client;

import java.awt.Window;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JFrame;

import org.apache.commons.lang.time.StopWatch;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import com.all.app.ApplicationContext;
import com.all.app.ApplicationUtils;
import com.all.app.Attributes;
import com.all.app.BeanStatistics;
import com.all.app.BeanStatisticsWriter;
import com.all.app.Module;
import com.all.app.Spring;
import com.all.client.services.UserPreferenceService;
import com.all.client.services.reporting.ClientReporter;
import com.all.client.services.reporting.ReporterSessionFilterManager;
import com.all.client.view.MainFrame;
import com.all.core.common.bean.AllDataBase;
import com.all.core.common.bean.ModalUserDatabaseInitializer;
import com.all.core.common.model.ApplicationLanguage;
import com.all.core.common.services.ApplicationConfig;
import com.all.core.common.services.reporting.ReportSender;
import com.all.i18n.ConfigurableMessages;
import com.all.i18n.DefaultMessages;
import com.all.schemaupdater.SchemaUpdater;
import com.all.shared.model.User;

public class ClientModule implements Module {

	private final static Log log = LogFactory.getLog(ClientModule.class);

	private final StopWatch timer = new StopWatch();

	private ApplicationContext appContext;
	private ConfigurableMessages messages;
	private final ApplicationConfig appConfig = new ApplicationConfig();
	private final User user;
	private Window loaderWindow;
	private final Spring spring;
	private Set<AllDataBase> dbs = new HashSet<AllDataBase>();
	private boolean debug;

	public ClientModule(User user, Window loaderWindow) {
		this.user = user;
		this.loaderWindow = loaderWindow;

		spring = Spring.newSpring();
		spring.addConfig("/spring/userDataLayer.xml");
		spring.addConfig("/spring/clientApplicationContext.xml");
		spring.addConfig("/spring/task.xml");
		spring.addConfig("/context/download-manager.xml");
		spring.addConfig("/context/twitter4all.xml");
		spring.addConfig("/spring/browser.xml");
		spring.addConfig("/context/chat-manager.xml");
		spring.addConfig("/core/common/CommonAppContext.xml");
		spring.addConfig("/context/facebook.xml");
	}

	@Override
	public void activate() {
		timer.start();
		loaderWindow.setVisible(true);

		ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
		messageSource.setBasename("i18n.messages");

		messages = new DefaultMessages(messageSource);
		ApplicationLanguage language = ApplicationLanguage.getLanguage();
		messages.setLocale(language.locale());

		SchemaUpdater.updateUserDatabaseSchema(appConfig.getDatabasePrefix() + user.getEmail());

		ModalUserDatabaseInitializer conf = new ModalUserDatabaseInitializer();
		conf.setMail(user.getEmail());

		spring.addBean(messages);
		spring.addBean(conf, "configuration");

	}

	@Override
	public void execute(Attributes attributes) {
		spring.addBean(attributes);

		log.info("starting application");

		if (debug) {
			appContext = spring.debug();

			BeanStatistics stats = appContext.getBean(BeanStatistics.class);
			new BeanStatisticsWriter(stats).deleteFile("stats.csv").saveToCSVFile("stats.csv");
			log.info(stats.getDescription());
		} else {
			appContext = spring.load();
		}

		appContext.getBean(ClientReporter.class).login(user);
		appContext.getBean(ReporterSessionFilterManager.class).login(user);

		attributes.setAttribute(ClientAttributes.DATABASES, dbs);

		MainFrame frame = appContext.getBean(MainFrame.class);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setRestoreBounds(appContext.getBean(UserPreferenceService.class).getScreenBounds());

		frame.setVisible(true);
		loaderWindow.setVisible(false);

		timer.split();
		log.info(" >>>> Main frame displayed in: " + timer.toSplitString());

		SimpleJdbcTemplate jdbcTemplate = appContext.getBean("userJdbcTemplate", SimpleJdbcTemplate.class);
		AllDataBase currentDb = new AllDataBase(jdbcTemplate, "CurrentUser: " + user.getEmail());
		dbs.add(currentDb);

		log.info("Core Client Application Started");

		Initializer initializer = appContext.getBean(Initializer.class);
		initializer.init(user);
		timer.split();
		timer.stop();
		log.info(" >>>> App Fully loaded in: " + timer.toSplitString());

		ApplicationUtils.showFrameAndWaitForClose(frame);

		appContext.getBean(ClientReporter.class).logout();
		appContext.getBean(ReporterSessionFilterManager.class).logout();
		appContext.getBean(ReportSender.class).waitForTermination();

		try {
			log.info("Shutting down initializer...");
			initializer.shutdown();
		} catch (Exception e) {
			log.error("Unexpected exception shutting down initializer.", e);
		}

	}

	@Override
	public void destroy() {
		try {
			log.info("Setting loader Window invisible...");
			loaderWindow.setVisible(false);
		} catch (Exception e) {
			log.error("No que no tronaba.", e);
		}

		log.info("Cleaning messages...");
		messages.clean();

		for (AllDataBase allDataBase : dbs) {
			try {
				log.info("Closing " + allDataBase);
				allDataBase.close();
			} catch (Exception e) {
				log.error("Could not close " + allDataBase, e);
			}
		}

		try {
			log.info("Closing app context");
			appContext.close();
		} catch (Exception e) {
			log.error("Unexpected exception closing application context.", e);
		}

		log.info("Destroy method completed.");
	}

	public void addBean(Object object) {
		spring.addBean(object);
	}

	public void enableDebug() {
		debug = true;
	}

}
