package com.all.core.common.services;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.all.appControl.ActionMethod;
import com.all.appControl.RequestMethod;
import com.all.core.common.model.ApplicationActions;
import com.all.core.common.model.ApplicationLanguage;
import com.all.i18n.ConfigurableMessages;
import com.all.shared.model.City;

@Service
public class ApplicationServices {
	@Autowired
	private ApplicationDao appDao;
	@Autowired
	private ConfigurableMessages messages;

	@PostConstruct
	public void initialize() {
		ApplicationLanguage language = appDao.getLanguage();
		changeLanguage(language);
	}

	@ActionMethod(ApplicationActions.CHANGE_LANGUAGE_ID)
	public void changeLanguage(ApplicationLanguage lang) {
		appDao.setLanguage(lang);
		messages.setLocale(lang.locale());
	}

	@RequestMethod(ApplicationActions.GET_ALL_CITIES_ID)
	public List<City> getAllCities() {
		return appDao.findAllCities();
	}

}
