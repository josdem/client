package com.all.core.common.model;

import static com.all.event.EventType.ev;

import com.all.event.EventType;
import com.all.event.ValueEvent;

public interface ApplicationEvents {

	EventType<ValueEvent<Boolean>> INTERNET_CONNECTION = ev(ApplicationModel.HAS_INTERNET_CONNECTION_ID);

}
