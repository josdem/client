package com.all.core.common.services;

import java.util.HashSet;

import net.sf.json.JSONObject;

import com.all.core.common.model.ApplicationDatabase;
import com.all.shared.json.JsonConverter;
import com.all.shared.json.readers.JsonReader;
import com.all.shared.model.UltrapeerNode;

public class ApplicationDatabaseReader implements JsonReader<ApplicationDatabase> {

	@SuppressWarnings("unchecked")
	@Override
	public ApplicationDatabase read(String json) {
		JSONObject jsonModel = JSONObject.fromObject(json);
		ApplicationDatabase db = new ApplicationDatabase();
		db.setUltrapeers(JsonConverter.toTypedCollection(jsonModel.getJSONArray("ultrapeers").toString(), HashSet.class,
				UltrapeerNode.class));
		Object language = jsonModel.get("language");
		db.setLanguage(language == null ? null : language.toString());
		return db;
	}

}
