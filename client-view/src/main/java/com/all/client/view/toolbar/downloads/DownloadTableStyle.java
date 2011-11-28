package com.all.client.view.toolbar.downloads;

import java.awt.Color;

import com.all.client.view.components.TableStyle;
import com.all.core.common.view.SynthColors;
import com.all.i18n.Messages;

public class DownloadTableStyle implements TableStyle {

	private final Messages messages;

	public DownloadTableStyle(Messages messages) {
		this.messages = messages;
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
		return SynthColors.GRAY150_150_150;
	}

	public Color getHeaderSelectedFontColor() {
		return SynthColors.WHITE255_255_255;
	}

	public Color getForeGroundColor() {
		return SynthColors.GRAY77_77_77;
	}


	public Color getHeaderFontColor() {
		return SynthColors.GRAY77_77_77;
	}

	public Messages getMessages() {
		return messages;
	}

}