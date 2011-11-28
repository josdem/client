package com.all.core.common.bean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

public class AllDataBase {
	private static final Log log = LogFactory.getLog(AllDataBase.class);

	private final SimpleJdbcTemplate template;
	private final String name;

	public AllDataBase(SimpleJdbcTemplate template, String name) {
		this.template = template;
		this.name = name;
	}

	public void close() {
		log.info("Sutting down database " + name);
		try {
			template.update("SHUTDOWN");
			while (true) {
				try {
					template.update("SHOW TABLES");
					log.info("waiting for connection: " + name + " to invalidate.");
					Thread.sleep(100);
				} catch (Exception e) {
					log.info("database: " + name + " Closed. >> " + e.getClass().getName() + ":" + e.getMessage());
					break;
				}
			}
		} catch (Exception e) {
			log.error("Couldn't close database: " + name + " ERROR:" + e.getClass().getName() + ":" + e.getMessage());
		}
	}
	
	@Override
	public String toString() {
		return "DB["+name+"]";
	}
}
