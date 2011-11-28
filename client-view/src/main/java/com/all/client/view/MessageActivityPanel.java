package com.all.client.view;

import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

import org.apache.commons.lang.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.all.action.ValueAction;
import com.all.appControl.control.ViewEngine;
import com.all.client.view.dialog.DialogFactory;
import com.all.core.actions.Actions;
import com.all.core.actions.ComposeView;
import com.all.core.actions.LoadContactProfileAction;
import com.all.core.common.view.SynthFonts;
import com.all.core.events.Events;
import com.all.core.events.ProfileLoadEvent;
import com.all.core.events.ProfileLoadEvent.ProfileLoadStatus;
import com.all.core.model.Model;
import com.all.core.model.SubViews;
import com.all.core.model.Views;
import com.all.event.EventMethod;
import com.all.event.ValueEvent;
import com.all.shared.model.ContactInfo;

@Component
public class MessageActivityPanel extends JPanel {

	private static final long serialVersionUID = -5763500383841931229L;
	@Autowired
	private DialogFactory dialogFactory;
	@Autowired
	private ViewEngine viewEngine;

	private JButton twitterMessageButton;
	private JLabel twitterMessageLabel;
	private Long twitterMessageValue = new Long(0);
	private AtomicBoolean requestViewChange = new AtomicBoolean(false);

	public MessageActivityPanel() {
		intialize();
	}

	private void intialize() {
		this.setLayout(new MigLayout("ins 0 0 0 0", "6[20!]4[l,24!]2[20!]4[l,24!]2[20!]4[l,24!]", "[center]"));
		this.setName("bottomProgressPanel");
		this.setPreferredSize(new Dimension(102, 26));

		this.add(BottomPanel.getSeparatorPanel(), "dock west, w 2!");

		// this.add(getAllMessageButton());
		// this.add(getAllMessageLabel());
		this.add(getTwitterMessageButton());
		this.add(getTwitterMessageLabel());
		// this.add(getFacebookMessageButton());
		// this.add(getFacebookMessageLabel());
	}

	private JButton getTwitterMessageButton() {
		if (twitterMessageButton == null) {
			twitterMessageButton = new JButton();
			twitterMessageButton.setPreferredSize(new Dimension(20, 20));
			twitterMessageButton.setName("twitterActivityButton");
			twitterMessageButton.setEnabled(false);
			twitterMessageButton.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {// we use mouseClicked to handle disabled state clicks as well
					if (dialogFactory.isTwitterLoggedIn()) {
						requestViewChange.set(true);
						ContactInfo contact = new ContactInfo(viewEngine.get(Model.CURRENT_USER));
						viewEngine.send(Actions.Social.LOAD_USER_PROFILE, new LoadContactProfileAction(contact,
								new ComposeView(Views.PROFILE, SubViews.TWITTER)));
					}
				}
			});
		}
		return twitterMessageButton;
	}

	@EventMethod(Events.Social.PROFILE_LOAD_ID)
	public void onProfileLoad(ProfileLoadEvent profileLoadEvent) {
		if (profileLoadEvent.getStatus() == ProfileLoadStatus.FINISHED && requestViewChange.compareAndSet(true, false)) {
			viewEngine.send(Actions.View.SET_CURRENT_COMPOSE_VIEW, new ValueAction<ComposeView>(new ComposeView(
					Views.PROFILE, SubViews.TWITTER)));
		}
	}

	private JLabel getTwitterMessageLabel() {
		if (twitterMessageLabel == null) {
			twitterMessageLabel = new JLabel();
			twitterMessageLabel.setText(twitterMessageValue.toString());
			twitterMessageLabel.setName(SynthFonts.BOLD_FONT12_GRAY64_64_64);
			twitterMessageLabel.setVisible(false);
		}
		return twitterMessageLabel;
	}

	@EventMethod(Events.Social.TWITTER_NUMBER_OF_NEW_MESSAGES_RECEIVED_ID)
	public void updateTwitterMessages(ValueEvent<Long> valueEvent) {
		if (viewEngine.get(Model.CURRENT_VIEW) != Views.PROFILE
				|| (viewEngine.get(Model.CURRENT_VIEW) == Views.PROFILE && viewEngine.get(Model.CURRENT_SUBVIEW) != SubViews.TWITTER)) {
			twitterMessageValue += valueEvent.getValue();
			updateNumberOfTwitterMessages();
		}
	}

	private void updateNumberOfTwitterMessages() {
		getTwitterMessageLabel().setText(twitterMessageValue.toString());
		getTwitterMessageLabel().setVisible(twitterMessageValue > 0);
	}

	@EventMethod(Events.Social.TWITTER_LOGGED_ID)
	public void updateTwitterIcon(ValueEvent<Boolean> valueEvent) {
		Boolean loggedIn = valueEvent.getValue();
		getTwitterMessageButton().setEnabled(BooleanUtils.isTrue(loggedIn));
	}

	@EventMethod(Model.CURRENT_SUBVIEW_ID)
	public void onCurrentSubViewchanged(ValueEvent<SubViews> valueEvent) {
		if (viewEngine.get(Model.CURRENT_VIEW) == Views.PROFILE && valueEvent.getValue().equals(SubViews.TWITTER)) {
			twitterMessageValue = 0L;
			updateNumberOfTwitterMessages();
		}
	}

	@EventMethod(Model.CURRENT_VIEW_ID)
	public void onCurrentViewchanged(ValueEvent<Views> valueEvent) {
		SubViews subView = viewEngine.get(Model.CURRENT_SUBVIEW);
		if (valueEvent.getValue() == Views.PROFILE && subView != null && subView.equals(SubViews.TWITTER)) {
			twitterMessageValue = 0L;
			updateNumberOfTwitterMessages();
		}
	}
}
