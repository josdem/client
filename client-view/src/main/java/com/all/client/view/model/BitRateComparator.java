package com.all.client.view.model;

import java.io.Serializable;
import java.util.Comparator;

public class BitRateComparator  implements Comparator<String>, Serializable  {
	private static final long serialVersionUID = 1L;
	@Override
	public int compare(String bitRateOne, String bitRateTwo) {
		String[] bitRateOneSplitted = bitRateOne.trim().replace("~","").split(" ");
		String[] bitRateTwoSplitted = bitRateTwo.trim().replace("~","").split(" ");
		Double bROne = new Double(bitRateOneSplitted[0]);
		Double bRTwo = new Double(bitRateTwoSplitted[0]);
		return bROne.compareTo(bRTwo);
	}

}
