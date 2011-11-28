package com.all.client.view;

import com.all.appControl.control.ViewEngine;

public interface View {
	void initialize(ViewEngine viewEngine);

	void destroy(ViewEngine viewEngine);
}
