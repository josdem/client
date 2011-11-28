package com.all.core.common.model;

import java.util.HashSet;
import java.util.Set;

import com.all.shared.model.UltrapeerNode;

public class ApplicationDatabase {
	private Set<UltrapeerNode> ultrapeers = new HashSet<UltrapeerNode>();

	private String language;

	public Set<UltrapeerNode> getUltrapeers() {
		return ultrapeers;
	}

	public void setUltrapeers(Set<UltrapeerNode> ultrapeers) {
		this.ultrapeers = ultrapeers;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public void addAll(ApplicationDatabase db2) {
		this.ultrapeers.addAll(db2.ultrapeers);
	}

	@Override
	public String toString() {
		return "LoginDatabase [ultrapeers=" + ultrapeers + ", language=" + language + "]";
	}
}
