package com.all.client.view.components;

import java.awt.Color;

import com.all.core.common.view.SynthColors;

public class TrashTableStyle implements TableStyle {
	public TrashTableStyle() {
	}

	@Override
	public Color getEvenRowColor() {
		return SynthColors.WHITE255_255_255;
	}

	@Override
	public Color getOddRowColor() {
		return SynthColors.CLEAR_GRAY245_245_245;
	}

	@Override
	public Color getSelectedRowColor() {
		return SynthColors.GRAY210_210_210;
	}

	@Override
	public Color getSelectedSeparatorColor() {
		return SynthColors.WHITE255_255_255;
	}

	@Override
	public Color getGridColor() {
		return SynthColors.GRAY77_77_77;
	}

	public Color getHeaderSelectedFontColor() {
		return SynthColors.WHITE255_255_255;
	}

}
