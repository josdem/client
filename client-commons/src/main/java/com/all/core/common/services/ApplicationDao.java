package com.all.core.common.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.all.core.common.model.ApplicationLanguage;
import com.all.shared.model.City;
import com.all.shared.model.UltrapeerNode;

@Repository
public class ApplicationDao {

	private static final Log LOG = LogFactory.getLog(ApplicationDao.class);

	private List<City> cities = null;

	@Autowired
	private ApplicationDatabaseAccess db;

	public List<UltrapeerNode> getKnownUltrapeers() {
		return new ArrayList<UltrapeerNode>(db.getDB().getUltrapeers());
	}

	public int getUltrapeerCount() {
		return db.getDB().getUltrapeers().size();
	}

	public void save(UltrapeerNode ultrapeer) {
		db.getDB().getUltrapeers().add(ultrapeer);
	}

	public void delete(UltrapeerNode ultrapeer) {
		db.getDB().getUltrapeers().remove(ultrapeer);
	}

	public List<City> findAllCities() {
		return getCities();
	}

	private List<City> getCities() {
		if (cities == null) {
			synchronized (this) {
				if (cities == null) {
					List<City> cities = new ArrayList<City>();
					Scanner scanner = null;
					try {
						scanner = new Scanner(getClass().getResourceAsStream("/scripts/cities.txt"));
						int line = 0;
						while (scanner.hasNextLine()) {
							String text = scanner.nextLine();
							line++;
							if (text.startsWith("('")) {
								try {
									City city = new City();
									String[] data = getSmartData(text);
									city.setCityId(data[0]);
									city.setCityName(data[1]);
									city.setCountryId(data[2]);
									city.setCountryName(data[3]);
									city.setStateId(data[4]);
									city.setStateName(data[5]);
									city.setPopIndex(data[6]);
									cities.add(city);
								} catch (Exception e) {
									LOG.warn("cities.txt[" + line + "]" + text + e);
								}
							}
						}
					} catch (Exception e) {
						LOG.error(e, e);
					} finally {
						try {
							scanner.close();
						} catch (Exception e) {
							LOG.error(e, e);
						}
					}

					this.cities = cities;
				}
			}
		}
		return cities;
	}

	private String[] getSmartData(String text) {
		text = text.substring(text.indexOf('(') + 1, text.lastIndexOf(')'));
		String[] split = text.split(",");
		for (int i = 0; i < split.length; i++) {
			String string = split[i];
			int indexOf = string.indexOf('\'');
			if (indexOf >= 0) {
				string = string.substring(indexOf + 1);
			}
			indexOf = string.indexOf('\'');
			if (indexOf >= 0) {
				string = string.substring(0, indexOf);
			}
			split[i] = string;
		}
		return split;
	}

	public City findCity(String cityId) {
		for (City city : getCities()) {
			if (cityId.equals(city.getCityId())) {
				return city;
			}
		}
		return null;
	}

	public void setLanguage(ApplicationLanguage language) {
		db.getDB().setLanguage(language.name());
	}

	public ApplicationLanguage getLanguage() {
		String langCode = db.getDB().getLanguage();
		try {
			return ApplicationLanguage.valueOf(langCode);
		} catch (Exception e) {
			return ApplicationLanguage.ENGLISH;
		}
	}
}
