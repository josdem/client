package com.all.core.common.util;

import java.util.ArrayList;
import java.util.Collections;

public final class SequenceUtil {

	private SequenceUtil() {
	}

	public static String[] createSequence(String what, int start, int end) {
		return createSequence(what, start, end, false);
	}

	public static String[] createSequence(String what, int start, int end, boolean reversed) {
		ArrayList<String> list = new ArrayList<String>();
		for (int i = start; i <= end; i++) {
			list.add(i + "");
		}
		if (reversed) {
			Collections.reverse(list);
		}
		if (what != null) {
			list.add(0, what);
		}
		return list.toArray(new String[list.size()]);
	}

}
