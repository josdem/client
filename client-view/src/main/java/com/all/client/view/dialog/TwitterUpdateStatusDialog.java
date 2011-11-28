package com.all.client.view.dialog;

import java.awt.Component;
import java.awt.Frame;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.all.action.ResponseCallback;
import com.all.appControl.control.ViewEngine;
import com.all.client.util.TwitterUtil;
import com.all.client.view.components.ScrollableTextArea;
import com.all.core.actions.Actions;
import com.all.core.common.model.ApplicationActions;
import com.all.core.common.view.SynthFonts;
import com.all.core.model.Model;
import com.all.core.model.Tweet;
import com.all.i18n.Messages;
import com.all.shared.stats.PostedListeningTrackTwitterStat;
import com.all.shared.stats.RecommendedTrackTwitterStat;
import com.all.shared.stats.usage.UserActions;

public class TwitterUpdateStatusDialog extends AllDialog {

	private static final long serialVersionUID = 1L;
	private static final Rectangle CONTENT_PANEL_BOUNDS = new Rectangle(0, 0, 380, 250);
	private static final String DESCRIPTION_PANEL_NAME = "twitterUpdateStatusPanel";
	private static final Rectangle DESCRIPTION_PANEL_BOUNDS = new Rectangle(0, 0, 380, 210);
	private static final Rectangle DESCRIPTION_LABEL_BOUNDS = new Rectangle(24, 50, 340, 32);
	private static final Rectangle STATUS_AREA_BOUNDS = new Rectangle(24, 88, 340, 90);
	private static final Rectangle CHAR_COUNTER_BOUNDS = new Rectangle(300, 180, 65, 20);
	private static final String SEPARATOR_NAME = "bottomPanelSeparator";
	private static final Rectangle SEPARATOR_BOUNDS = new Rectangle(5, 210, 370, 2);
	private static final Rectangle CANCEL_BUTTON_BOUNDS = new Rectangle(105, 218, 80, 22);
	private static final Rectangle TWEET_BUTTON_BOUNDS = new Rectangle(195, 218, 80, 22);
	private static final String BUTTON_NAME = "buttonOk";

	private JPanel contentPanel;
	private JPanel descriptionPanel;
	private JLabel descriptionLabel;
	private ScrollableTextArea statusScrollArea;
	private JLabel charCounterLabel;
	private JButton cancelButton;
	private JButton tweetButton;
	private String defaultMessage;
	private JPanel separator;

	private final ViewEngine viewEngine;
	private final Integer actionType;
	private final String hashcode;

	public TwitterUpdateStatusDialog(Frame frame, Messages messages, String defaultMessage, Integer actionType,
			ViewEngine viewEngine, String hashcode) {
		super(frame, messages);
		this.defaultMessage = defaultMessage;
		this.actionType = actionType;
		this.viewEngine = viewEngine;
		this.hashcode = hashcode;
		initializeContentPane();
		internationalizeDialog(messages);
	}

	private String getStatus() {
		return statusScrollArea.getText();
	}

	@Override
	String dialogTitle(Messages messages) {
		return messages.getMessage("twitter.updateStatusDialog.title");
	}

	@Override
	JComponent getContentComponent() {
		return getContentPanel();
	}

	private JPanel getContentPanel() {
		if (contentPanel == null) {
			contentPanel = new JPanel();
			contentPanel.setLayout(null);
			contentPanel.setBounds(CONTENT_PANEL_BOUNDS);
			contentPanel.add(getDescriptionPanel());
			contentPanel.add(getSeparator());
			contentPanel.add(getCancelButton());
			contentPanel.add(getTweetButton());
		}
		return contentPanel;
	}

	private JPanel getDescriptionPanel() {
		if (descriptionPanel == null) {
			descriptionPanel = new JPanel();
			descriptionPanel.setLayout(null);
			descriptionPanel.setName(DESCRIPTION_PANEL_NAME);
			descriptionPanel.setBounds(DESCRIPTION_PANEL_BOUNDS);
			descriptionPanel.add(getDescriptionLabel());
			descriptionPanel.add(getStatusScrollArea());
			descriptionPanel.add(getCharCounterLabel());

		}
		return descriptionPanel;
	}

	private JLabel getDescriptionLabel() {
		if (descriptionLabel == null) {
			descriptionLabel = new JLabel();
			descriptionLabel.setBounds(DESCRIPTION_LABEL_BOUNDS);
		}
		return descriptionLabel;
	}

	private JButton getCancelButton() {
		if (cancelButton == null) {
			cancelButton = new JButton();
			cancelButton.setName(BUTTON_NAME);
			cancelButton.setBounds(CANCEL_BUTTON_BOUNDS);
			cancelButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					closeDialog();
				}
			});
		}
		return cancelButton;
	}

	private JPanel getSeparator() {
		if (separator == null) {
			separator = new JPanel();
			separator.setName(SEPARATOR_NAME);
			separator.setBounds(SEPARATOR_BOUNDS);
		}
		return separator;
	}

	private Component getTweetButton() {
		if (tweetButton == null) {
			tweetButton = new JButton();
			tweetButton.setName(BUTTON_NAME);
			tweetButton.setBounds(TWEET_BUTTON_BOUNDS);
			tweetButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					getTweetButton().setEnabled(false);
					viewEngine.request(Actions.Twitter.UPDATE_STATUS, new Tweet(getStatus(), actionType),
							new ResponseCallback<Boolean>() {
								@Override
								public void onResponse(Boolean t) {
									closeDialog();
								}
							});
					String email = viewEngine.get(Model.CURRENT_USER).getEmail();
                    if(actionType.equals(UserActions.SocialNetworks.TWITTER_TRACK)){
                    	viewEngine.sendValueAction(ApplicationActions.REPORT_USER_STAT, new PostedListeningTrackTwitterStat(email,
                    					hashcode));
                    }
                    else if(actionType.equals(UserActions.SocialNetworks.TWITTER_RECOMMENDATION)){
                    	viewEngine.sendValueAction(ApplicationActions.REPORT_USER_STAT, new RecommendedTrackTwitterStat(email,
                    					hashcode));
                    }
				}
			});
		}
		return tweetButton;
	}

	private JLabel getCharCounterLabel() {
		if (charCounterLabel == null) {
			charCounterLabel = new JLabel();
			charCounterLabel.setHorizontalAlignment(JLabel.RIGHT);
			charCounterLabel.setText(Integer.toString(TwitterUtil.getRemainingChars(statusScrollArea.getText())));
			charCounterLabel.setBounds(CHAR_COUNTER_BOUNDS);
			charCounterLabel.setName(SynthFonts.BOLD_FONT20_GRAY51_51_51);
		}
		return charCounterLabel;
	}

	private ScrollableTextArea getStatusScrollArea() {
		if (statusScrollArea == null) {
			statusScrollArea = new ScrollableTextArea(STATUS_AREA_BOUNDS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER,
					ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
			if (defaultMessage != null) {
				statusScrollArea.setText(defaultMessage);
			}
			statusScrollArea.getTextArea().getDocument().addDocumentListener(new TweeterDocumentListener());
		}
		return statusScrollArea;
	}

	private void validateMessageLength() {
		int remainingChars = TwitterUtil.getRemainingChars(statusScrollArea.getText());
		charCounterLabel.setText(Integer.toString(remainingChars));
		if (remainingChars >= 0) {
			tweetButton.setEnabled(true);
			statusScrollArea.setError(false);
			charCounterLabel.setName(SynthFonts.BOLD_FONT20_GRAY51_51_51);
		} else {
			tweetButton.setEnabled(false);
			statusScrollArea.setError(true);
			charCounterLabel.setName(SynthFonts.BOLD_FONT20_RED);
		}
	}

	@Override
	void internationalizeDialog(Messages messages) {
		descriptionLabel.setText(messages.getMessage("twitter.updateStatusDialog.description"));
		cancelButton.setText(messages.getMessage("cancel"));
		tweetButton.setText(messages.getMessage("twitter.updateStatusDialog.tweet"));
	}

	private final class TweeterDocumentListener implements DocumentListener {
		@Override
		public void changedUpdate(DocumentEvent e) {
			validateMessageLength();
		}

		@Override
		public void insertUpdate(DocumentEvent e) {
			validateMessageLength();
		}

		@Override
		public void removeUpdate(DocumentEvent e) {
			validateMessageLength();
		}
	}

}
