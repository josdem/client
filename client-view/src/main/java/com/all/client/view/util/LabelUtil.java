package com.all.client.view.util;

import java.awt.Color;
import java.awt.FontMetrics;
import java.util.StringTokenizer;

import javax.swing.JLabel;

public final class LabelUtil {

	private LabelUtil() {

	}

	public static String splitTextInTwoRows(JLabel label, String quote, Color color, int maxQuoteWidth) {
		FontMetrics fontMetrics = label.getFontMetrics(label.getFont());
		String hexColor = Integer.toHexString(color.getRGB()).substring(2);
		String htmlBeginString = "<HTML><FONT style='color: #%s; font-size: %dpt'>";
		htmlBeginString = String.format(htmlBeginString, hexColor, label.getFont().getSize());
		String htmlEndString = "</FONT></HTML>";
		String textLine1 = "";
		String textLine2 = "";

		if (fontMetrics.stringWidth(quote) < maxQuoteWidth) {
			return htmlBeginString + quote + htmlEndString;
		}

		StringTokenizer tokenizer = new StringTokenizer(quote);
		tokenizer.countTokens();
		if (tokenizer.countTokens() == 1) {
			for (int i = 0; i < quote.length(); i++) {
				textLine1 = textLine1 + quote.charAt(i);
				if (fontMetrics.stringWidth(textLine1) > maxQuoteWidth) {
					break;
				}
			}
		} else {
			String nextToken = "";
			while (tokenizer.hasMoreTokens() && fontMetrics.stringWidth(textLine1) < maxQuoteWidth) {
				nextToken = tokenizer.nextToken();
				if (fontMetrics.stringWidth(textLine1 + nextToken) > maxQuoteWidth) {
					break;
				}
				textLine1 = textLine1 + nextToken + " ";
			}
		}
		textLine1 = textLine1.concat("<BR/>");
		textLine2 = quote.substring(textLine1.length() - "<BR/>".length(), quote.length());
		
		if (fontMetrics.stringWidth(textLine2) > maxQuoteWidth) {
			int dotsWidth = fontMetrics.stringWidth("...");
			String textLine2b = "";
			for (int i = 0; i < textLine2.length(); i++) {
				textLine2b = textLine2b + textLine2.charAt(i);
				if (fontMetrics.stringWidth(textLine2b) >= (maxQuoteWidth - dotsWidth)) {
					break;
				}
			}
			textLine2 = textLine2b.concat("...");
		}
		return htmlBeginString + textLine1 + textLine2 + htmlEndString;
	}

}
