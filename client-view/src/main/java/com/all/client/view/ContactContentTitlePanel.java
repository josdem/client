package com.all.client.view;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;

import com.all.action.ResponseCallback;
import com.all.appControl.control.ViewEngine;
import com.all.commons.SoundPlayer.Sound;
import com.all.core.actions.Actions;
import com.all.core.actions.LoadContactLibraryAction;
import com.all.core.common.view.SynthFonts;
import com.all.i18n.Messages;
import com.all.shared.model.Root;

public class ContactContentTitlePanel extends ContentTitlePanel {

	private static final long serialVersionUID = 1L;

	private static final Rectangle REFRESH_BUTTON_BOUNDS = new Rectangle(176, 2, 22, 22);

	private static final String NAME = "myMusicPanel";

	private static final String REFRESH_BUTTON_NAME = "refreshLibraryButton";

	private static final String TOOLTIP_REFRESH_REMOTE = "tooltip.refreshRemote";

	private JButton refreshButton;

	private final Root root;

	private final ViewEngine viewEngine;

	public ContactContentTitlePanel(Root root, ViewEngine viewEngine) {
		this.root = root;
		this.viewEngine = viewEngine;
		initGui();
	}

	@Override
	public void initGui() {
		super.initialize();
		this.setName(NAME);
		this.add(getRefreshButton());
	}
	
	private JButton getRefreshButton() {
		if (refreshButton == null) {
			refreshButton = new JButton();
			refreshButton.setName(REFRESH_BUTTON_NAME);
			refreshButton.setBounds(REFRESH_BUTTON_BOUNDS);
			refreshButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					Sound.LIBRARY_REMOTE_REFRESH.play();
					refreshButton.setEnabled(false);
					LoadContactLibraryAction param = LoadContactLibraryAction.reload(root.getOwnerMail());
					viewEngine.request(Actions.Library.LOAD_CONTACT_LIBRARY_REQUEST, param, new ResponseCallback<Void>() {
						@Override
						public void onResponse(Void t) {
							refreshButton.setEnabled(true);
						}
					});
				}
			});
		}
		return refreshButton;
	}

	public JLabel getTitleLabel() {
		super.getTitleLabel();
		titleLabel.setName(SynthFonts.BOLD_FONT12_GRAY77_77_77);
		return super.getTitleLabel();
	}

	@Override
	public void internationalize(Messages messages) {
		getTitleLabel().setText(root.getName());
		getRefreshButton().setToolTipText(messages.getMessage(TOOLTIP_REFRESH_REMOTE));
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
