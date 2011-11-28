package com.all.core.common.model;

import static com.all.action.ActionType.cm;

import java.util.List;

import com.all.action.ActionType;
import com.all.action.RequestAction;
import com.all.action.ValueAction;
import com.all.shared.model.City;
import com.all.shared.stats.AllStat;

public interface ApplicationActions {

	String REPORT_USER_ACTION_ID = "application.reportUserAction";
	ActionType<ValueAction<Integer>> REPORT_USER_ACTION = cm(REPORT_USER_ACTION_ID);

	String REPORT_USER_STAT_ID = "application.reportUserStat";
	ActionType<ValueAction<AllStat>> REPORT_USER_STAT = cm(REPORT_USER_STAT_ID);

	String CHANGE_LANGUAGE_ID = "application.changeLang";
	ActionType<ValueAction<ApplicationLanguage>> CHANGE_LANGUAGE = cm(CHANGE_LANGUAGE_ID);

	String GET_ALL_CITIES_ID = "application.getAllCities";
	ActionType<RequestAction<Void, List<City>>> GET_ALL_CITIES = cm(GET_ALL_CITIES_ID);

}
