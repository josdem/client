package com.all.core.common.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Date;
import java.util.Scanner;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.all.shared.json.JsonConverter;
import com.all.shared.model.City;
import com.all.shared.model.Gender;
import com.all.shared.model.User;
import com.all.shared.model.UserStatus;

public class UserUtils {

	private static final Log LOG = LogFactory.getLog(UserUtils.class);

	public static User defaultUser() {
		User user = new User();

		user.setId(16L);
		user.setEmail("marco@dev.com");
		user.setVersion(1L);

		// 12345678
		user.setPassword("25d55ad283aa400af464c76d713c07ad");

		user.setBirthday(new Date());
		user.setCity(new City());
		user.setDay(24);
		user.setFirstName("Marco Antonio");
		user.setGender(Gender.MALE);
		user.setIdLocation("397105");
		user.setLastName("Fernández");
		user.setMonth(7);
		user.setNickName("marco");
		user.setQuote("Sharing is Caring!!");
		user.setRegistrationDate(new Date());
		user.setStatus(UserStatus.Active);
		user.setYear(1984);
		user.setZipCode("123456");
		return user;
	}

	public static void saveUser(User user, String file) {
		save(new File(file), user);
	}

	public static User loadUser(String file) {
		return load(new File(file), User.class);
	}

	private static <T> T load(File file, Class<T> clazz) {
		if (file.exists()) {
			Scanner scanner = null;
			try {
				StringBuilder text = new StringBuilder();
				String NL = System.getProperty("line.separator");
				scanner = new Scanner(new FileInputStream(file));
				while (scanner.hasNextLine()) {
					text.append(scanner.nextLine() + NL);
				}
				return JsonConverter.toBean(text.toString(), clazz);
			} catch (Exception e) {
				LOG.error(e, e);
			} finally {
				try {
					scanner.close();
				} catch (Exception e) {
					LOG.error(e, e);
				}
			}
		}
		return null;
	}

	private static void save(File file, Object db) {
		Writer out = null;
		try {
			out = new OutputStreamWriter(new FileOutputStream(file));
			out.write(JsonConverter.toJson(db));
		} catch (Exception e) {
			LOG.error(e, e);
		} finally {
			try {
				out.close();
			} catch (Exception e) {
				LOG.error(e, e);
			}
		}
	}

}
