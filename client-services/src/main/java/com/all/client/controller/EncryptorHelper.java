package com.all.client.controller;

import java.io.UnsupportedEncodingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasypt.util.text.BasicTextEncryptor;

import com.all.client.data.Hashcoder;
import com.all.shared.model.User;

public class EncryptorHelper {

	private BasicTextEncryptor textEncryptor;

	private final Log log = LogFactory.getLog(this.getClass());

	public EncryptorHelper(User currentUser) {
		this.textEncryptor = new BasicTextEncryptor();
		try {
			String digest = Hashcoder.digest((currentUser.getId() + currentUser.getEmail()).getBytes("UTF8"));
			textEncryptor.setPassword(digest);
		} catch (UnsupportedEncodingException e) {
			log.error("Unable to set up text encryptor", e);
		}
	}

	public String encrypt(String input) {
		return textEncryptor.encrypt(input);
	}

	public String decrypt(String input) {
		return textEncryptor.decrypt(input);
	}
}
