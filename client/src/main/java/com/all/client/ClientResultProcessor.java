package com.all.client;

import com.all.app.Attribute;
import com.all.app.Attributes;
import com.all.app.ResultProcessor;

public class ClientResultProcessor implements ResultProcessor<ClientResult> {

	@Override
	public ClientResult result(Attributes attributes) {
		boolean loggedOut = getBoolean(attributes, ClientAttributes.LOGGED_OUT);
		boolean requiresRestart = getBoolean(attributes, ClientAttributes.REQUIRE_RESTART);
		if (loggedOut) {
			return ClientResult.logout;
		}
		if (requiresRestart) {
			return ClientResult.exit;
		}
		return ClientResult.exit;
	}

	private boolean getBoolean(Attributes attributes, Attribute attribute) {
		Object attr = attributes.getAttribute(attribute);
		if (attr == null) {
			return false;
		}
		if (attr instanceof Boolean) {
			Boolean bool = (Boolean) attr;
			return bool.booleanValue();
		}
		return false;
	}

}
