package com.all.core.common.model;

import java.util.Locale;

import com.all.core.common.services.ApplicationConfig;
import com.all.core.common.services.ApplicationDatabaseAccess;

public enum ApplicationLanguage {
	ENGLISH(Locales.ENGLISH), SPANISH(Locales.SPANISH);

	private final Locale locale;

	private ApplicationLanguage(Locale locale) {
		this.locale = locale;
	}

	public Locale locale() {
		return locale;
	}

	interface Locales {
		Locale ENGLISH = Locale.US;
		Locale SPANISH = new Locale("es", "MX");
	}

	public static final ApplicationLanguage getLanguage() {
		ApplicationLanguage language = null;
		try {
			language = ApplicationLanguage.valueOf(new ApplicationDatabaseAccess(new ApplicationConfig()).getDB()
					.getLanguage());
		} catch (Exception e) {
		}
		if (language == null) {
			language = ApplicationLanguage.ENGLISH;
		}
		return language;
	}
}
