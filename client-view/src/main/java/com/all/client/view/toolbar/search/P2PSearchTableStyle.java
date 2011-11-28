package com.all.client.view.toolbar.search;

import java.awt.Color;

import com.all.client.model.DecoratedSearchData;
import com.all.client.view.components.TableStyle;
import com.all.core.common.view.SynthColors;

public class P2PSearchTableStyle implements TableStyle {
	public P2PSearchTableStyle() {
	}

	@Override
	public Color getEvenRowColor() {
		return SynthColors.CLEAR_GRAY245_245_245;
	}

	@Override
	public Color getOddRowColor() {
		return SynthColors.WHITE255_255_255;
	}

	@Override
	public Color getSelectedRowColor() {
		return SynthColors.BLUE175_205_225;
	}

	@Override
	public Color getSelectedSeparatorColor() {
		return SynthColors.WHITE255_255_255;
	}

	@Override
	public Color getGridColor() {
		return SynthColors.GRAY150_150_150;
	}

	public boolean isDownloading(DecoratedSearchData decoratedSearchData) {
		return false;
	}

	public Color getGray() {
		return SynthColors.GRAY170_170_170;
	}

	public Color getForeGroundColor() {
		return SynthColors.GRAY77_77_77;
	}

	public Color getHeaderSelectedFontColor() {
		return SynthColors.WHITE255_255_255;
	}

	public Color getHeaderFontColor() {
		return SynthColors.GRAY77_77_77;
	}
}
