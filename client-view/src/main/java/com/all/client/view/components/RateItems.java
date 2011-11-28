/**
 * 
 */
package com.all.client.view.components;

public enum RateItems {
	NOT_RATED("ratingNotRated", "Click to rate", 0),
	RATED_POSITIVE("ratingPositive", "Like it", 3),
	RATED_REGULAR("ratingRegular", "I don't care", 2),
	RATED_NEGATIVE("ratingNegative", "Hate it", 1);

	private final String synthName;
	private String toolTip;
	public final int ratingValue;

	private RateItems(String synthName, String toolTip, int ratingValue) {
		this.synthName = synthName;
		this.toolTip = toolTip;
		this.ratingValue = ratingValue;
	}

	public String getSynthName() {
		return synthName;
	}

	public String getToolTip() {
		return toolTip;
	}

	public RateItems next() {
		if ((this.ordinal() + 1) == RateItems.values().length) {
			return RateItems.values()[1];
		}
		return RateItems.values()[this.ordinal() + 1];
	}

	public static RateItems getItem(int rating) {
		for (RateItems item : values()) {
			if (item.ratingValue == rating) {
				return item;
			}
		}
		return NOT_RATED;
	}
}