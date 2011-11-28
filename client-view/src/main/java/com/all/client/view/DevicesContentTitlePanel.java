package com.all.client.view;

import java.awt.Dimension;
import java.awt.Rectangle;

import javax.swing.JLabel;

import com.all.appControl.control.ViewEngine;
import com.all.core.common.view.SynthFonts;
import com.all.i18n.Messages;
import com.all.shared.model.Root;

public class DevicesContentTitlePanel extends ContentTitlePanel {

	private static final Rectangle MY_MUSIC_LABEL_BOUNDS = new Rectangle(20, 2, 180, 18);

	private static final Dimension PREFERRED_SIZE = new Dimension(198, 24);

	private static final long serialVersionUID = 1L;

	protected JLabel myMusicLabel;

	private final Root root;

	public DevicesContentTitlePanel(Root root) {
		this.root = root;
	}

	@Override
	public void initGui() {
		this.setLayout(null);
		this.setName("myMusicPanel");
		this.setPreferredSize(PREFERRED_SIZE);
		this.setMaximumSize(PREFERRED_SIZE);
		this.setMinimumSize(PREFERRED_SIZE);
		this.setSize(PREFERRED_SIZE);
		this.add(getMyMusicLabel());
	}
	
	public JLabel getMyMusicLabel() {
		if (myMusicLabel == null) {
			myMusicLabel = new JLabel();
			myMusicLabel.setVerticalAlignment(JLabel.CENTER);
			myMusicLabel.setHorizontalAlignment(JLabel.CENTER);
			myMusicLabel.setBounds(MY_MUSIC_LABEL_BOUNDS);
			myMusicLabel.setName(SynthFonts.BOLD_FONT12_GRAY77_77_77);
		}
		return myMusicLabel;
	}

	@Override
	public void internationalize(Messages messages) {
		getMyMusicLabel().setText(root.getName());
	}

	@Override
	public void removeMessages(Messages messages) {
		messages.remove(this);
	}

	@Override
	public void setMessages(Messages messages) {
		messages.add(this);
	}

	@Override
	public void initialize(ViewEngine viewEngine) {
	}

	@Override
	public void destroy(ViewEngine viewEngine) {
	}

}
