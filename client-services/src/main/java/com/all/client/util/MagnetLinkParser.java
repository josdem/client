package com.all.client.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Scanner;
import java.util.regex.MatchResult;

public final class MagnetLinkParser {
	static final String CHARSET_ENCODING = "UTF-8";
	static final String DISPLAY_NAME = "&dn=";
	static final String TRACKER_DELIMITER = "&tr=";
	static final String INFO_HASH_PREFIX = "xt=urn:btih:";
	static final String INFO_LENGTH_MATCHER = "\\w{32}";
	
	final String magnetUri;
	String infoHash;
	String displayName;

	public MagnetLinkParser(String magnetUri) {
		this.magnetUri = magnetUri;
		parse();
	}
	
	private void parse() {
		try {
			infoHash = parseInfoHash();
			displayName = parseFileName();
		} catch(IllegalStateException isa) {
			throw new IllegalArgumentException("Invalid magnet uri", isa);
		}
	}

	String parseInfoHash() {
		Scanner scanner = new Scanner(magnetUri);
		scanner.findInLine(INFO_HASH_PREFIX + INFO_LENGTH_MATCHER);
		MatchResult match = scanner.match();
		String matchResult = match.group();
		return matchResult.replace(INFO_HASH_PREFIX, "");
	}
	
	String parseFileName() {
		String fileName = magnetUri.substring(magnetUri.indexOf(DISPLAY_NAME) + DISPLAY_NAME.length());
		if (fileName.contains(TRACKER_DELIMITER)) {
			fileName = fileName.substring(0, fileName.indexOf(TRACKER_DELIMITER));
		}
	  try {
			return URLDecoder.decode(fileName, CHARSET_ENCODING);
		} catch (UnsupportedEncodingException e) {
			return null;
		}
	}

	public String getInfoHash() {
		return infoHash;
	}
	
	public String getDisplayName() {
		return displayName;
	}
}
