package com.all.core.common.model;

import static com.all.model.ModelType.model;

import com.all.model.ModelType;

public interface ApplicationModel {

	String HAS_INTERNET_CONNECTION_ID = "application.hasInternetConnection";
	ModelType<Boolean> HAS_INTERNET_CONNECTION = model(HAS_INTERNET_CONNECTION_ID);

}
