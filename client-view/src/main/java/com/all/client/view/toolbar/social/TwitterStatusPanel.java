package com.all.client.view.toolbar.social;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.HierarchyBoundsAdapter;
import java.awt.event.HierarchyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.StringReader;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML.Attribute;
import javax.swing.text.html.HTML.Tag;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.parser.ParserDelegator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.all.client.model.DecoratedTwitterStatus;
import com.all.client.util.Formatters;
import com.all.client.view.components.ImagePanel;
import com.all.commons.IncrementalNamedThreadFactory;
import com.all.core.common.view.SynthFonts;
import com.all.i18n.Internationalizable;
import com.all.i18n.Messages;
import com.all.twitter.TwitterStatus.TwitterStatusType;

public class TwitterStatusPanel extends JPanel implements Internationalizable {

	private static final long serialVersionUID = 1303843562739499034L;
	private static final Log LOG = LogFactory.getLog(TwitterStatusPanel.class);
	private static final String STATUS_PANEL = "twitterStatusPanel";
	private static final String DIRECT_MESSAGE_PANEL = "twitterDirectMessagePanel";
	private static final String MENTION_PANEL = "twitterMentionPanel";
	private static final String STATUS_OVER_PANEL = "twitterStatusOverPanel";
	private static final String DIRECT_MESSAGE_OVER_PANEL = "twitterDirectMessageOverPanel";
	private static final String MENTION_OVER_PANEL = "twitterMentionOverPanelStyle";
	private static final String HTTP = "http://";
	private static final String TWITTER_SEARCH_URL = "http://twitter.com/search?q=";
	private static final double IMAGE_ARC = 0.3;
	private static final Dimension STATUS_PANEL_MAXIMUM_SIZE = new Dimension(Integer.MAX_VALUE, 90);
	private static final Dimension BUTTON_SEPARATOR_PANEL_SIZE = new Dimension(30, 20);
	private static final Dimension PICTURE_DEFAULT_SIZE = new Dimension(48, 48);
	private static final Dimension TOP_PANEL_DEFAULT_SIZE = new Dimension(42, 20);
	private static final Dimension TOP_PANEL_MAXIMUM_SIZE = new Dimension(Integer.MAX_VALUE, 20);
	private static final Dimension TOP_PANEL_MINIMUM_SIZE = new Dimension(1, 20);
	private static final Dimension SEPARATOR_DEFAULT_SIZE = new Dimension(1, 1);
	private static final Dimension SEPARATOR_HORIZONTAL_SIZE = new Dimension(Integer.MAX_VALUE, 1);
	private static final Dimension BUTTON_GAP_SIZE = new Dimension(10, 20);
	private static final Insets PICTURE_INSETS = new Insets(8, 12, 8, 8);
	private static final Insets HEADER_INSETS = new Insets(0, 0, 0, 8);
	private static final Insets MESSAGE_INSETS = new Insets(0, 0, 8, 8);
	private static final Insets INSETS = new Insets(0, 12, 0, 8);
	private static final Insets BOTTOM_INSETS = new Insets(0, 0, 0, 0);
	private static final String DEFAULT_NAME = "profileWallInitialBackgroundMessage";
	private static final String SEPARATOR_NAME = "profileWallSeparatorBackgroundMessage";
	private static final String DATE_PATTERN = "MMM dd, hh:mm aaa";
	private static final String VIA = " via ";
	private static final String SPACE = " ";
	private static final String POINT = ". ";
	private static final String TWITTER_URL = "http://www.twitter.com/";
	private static final FlowLayout MESSAGE_LAYOUT_MGR = new FlowLayout(FlowLayout.LEFT, 2, 0);
	private static final GridBagLayout GRIDBAG_LAYOUT_MGR = new GridBagLayout();
	private static final FlowLayout FLOW_LAYOUT_MGR = new FlowLayout(FlowLayout.LEFT, 0, 0);
	private static final GridBagConstraints pictureConstraints = new GridBagConstraints();
	private static final GridBagConstraints headerConstraints = new GridBagConstraints();
	private static final GridBagConstraints messageConstraint = new GridBagConstraints();
	private static final GridBagConstraints bottomConstraints = new GridBagConstraints();
	private static final GridBagConstraints bottomSeparatorConstraints = new GridBagConstraints();
	private static final ExecutorService executor = Executors.newCachedThreadPool(new IncrementalNamedThreadFactory(
			"TwitterImageRetrivalThread"));

	static {
		pictureConstraints.gridx = 0;
		pictureConstraints.gridy = 0;
		pictureConstraints.gridwidth = 1;
		pictureConstraints.gridheight = 2;
		pictureConstraints.anchor = GridBagConstraints.NORTH;
		pictureConstraints.insets = PICTURE_INSETS;

		headerConstraints.gridx = 1;
		headerConstraints.gridy = 0;
		headerConstraints.weightx = 1;
		headerConstraints.fill = GridBagConstraints.HORIZONTAL;
		headerConstraints.insets = HEADER_INSETS;

		messageConstraint.gridx = 1;
		messageConstraint.gridy = 1;
		messageConstraint.weightx = 1;
		messageConstraint.weighty = 1;
		messageConstraint.fill = GridBagConstraints.BOTH;
		messageConstraint.insets = MESSAGE_INSETS;

		bottomConstraints.gridx = 0;
		bottomConstraints.gridy = 2;
		bottomConstraints.weightx = 1;
		bottomConstraints.gridwidth = 0;
		bottomConstraints.fill = GridBagConstraints.HORIZONTAL;
		bottomConstraints.insets = INSETS;

		bottomSeparatorConstraints.gridx = 0;
		bottomSeparatorConstraints.gridy = 3;
		bottomSeparatorConstraints.gridwidth = 2;
		bottomSeparatorConstraints.fill = GridBagConstraints.HORIZONTAL;
		bottomSeparatorConstraints.insets = BOTTOM_INSETS;
	}

	private static TwitterStatusPanel activePanel;
	private final DecoratedTwitterStatus twitterStatus;
	private final Collection<TwitterUserLabel> userLabels = new ArrayList<TwitterUserLabel>();

	private ImagePanel displayPicture;
	private JPanel topPanel;
	private JLabel lblUserName;
	private JLabel lblWrote;
	private JPanel messagePanel;
	private JPanel bottomPanel;
	private JLabel lblDate;
	private JPanel separator;
	private JLabel lblSource;
	private JLabel lblExtra;
	private String sourceUrl;
	private String sourceValue;
	private JButton btnReply;
	private JButton btnDirect;
	private JButton btnRetweet;
	private JLabel lblExtraName;
	private String backgroundStyle;
	private String backgroundOverStyle;

	public TwitterStatusPanel(DecoratedTwitterStatus twitterStatus, Messages messages) {
		this.twitterStatus = twitterStatus;
		this.sourceUrl = createTwitterUserURL(twitterStatus.getScreenName());
		this.sourceValue = twitterStatus.getSource();
		setMessages(messages);
		initialize();
	}

	public void addReplyTweetListener(MouseListener replyListener) {
		getButtonReply().addMouseListener(replyListener);
	}

	public void addRetweetListener(MouseListener retweetListener) {
		getButtonRetweet().addMouseListener(retweetListener);
	}

	public void addDirectListener(MouseListener directListener) {
		getButtonDirect().addMouseListener(directListener);
	}

	public void addUserClickedListener(MouseListener userClickedListener) {
		for (TwitterUserLabel userLabel : userLabels) {
			userLabel.addMouseListener(userClickedListener);
		}
	}

	private void initialize() {
		setStatusType(twitterStatus.getType());
		this.setLayout(GRIDBAG_LAYOUT_MGR);
		this.setMaximumSize(STATUS_PANEL_MAXIMUM_SIZE);
		this.add(getDisplayPicture(), pictureConstraints);
		this.add(getTopPanel(), headerConstraints);
		this.add(getMessagePanel(), messageConstraint);
		this.add(getBottomPanel(), bottomConstraints);
		this.add(getSeparator(), bottomSeparatorConstraints);
		this.addMouseListener(new MouseOverListener());
	}

	private void setStatusType(TwitterStatusType twitterStatusType) {
		switch (twitterStatusType) {
		case FRIENDS:
			backgroundStyle = STATUS_PANEL;
			backgroundOverStyle = STATUS_OVER_PANEL;
			break;
		case DIRECT:
			backgroundStyle = DIRECT_MESSAGE_PANEL;
			backgroundOverStyle = DIRECT_MESSAGE_OVER_PANEL;
			break;
		case MENTIONS:
			backgroundStyle = MENTION_PANEL;
			backgroundOverStyle = MENTION_OVER_PANEL;
			break;
		default:
			backgroundStyle = DEFAULT_NAME;
			backgroundOverStyle = DEFAULT_NAME;
		}
		this.setName(backgroundStyle);
	}

	private ImagePanel getDisplayPicture() {
		if (displayPicture == null) {
			displayPicture = new ImagePanel();
			displayPicture.setPreferredSize(PICTURE_DEFAULT_SIZE);
			displayPicture.setMinimumSize(PICTURE_DEFAULT_SIZE);
			displayPicture.setMaximumSize(PICTURE_DEFAULT_SIZE);
			displayPicture.setSize(PICTURE_DEFAULT_SIZE);
			executor.execute(new Runnable() {
				@Override
				public void run() {
					try {
						displayPicture.setImage(twitterStatus.isRetweeted() ? twitterStatus.getRetweeterProfileImage()
								: twitterStatus.getProfileImage(), IMAGE_ARC, IMAGE_ARC);
					} catch(Exception e) {
						// set to trace bug #1078 DEFECT - En el perfil de twitter, No se visualiza el avatar de algunos usuarios de
						// twitter (ver imagen)
						LOG.error("Unexpected error in thread to retrieve avatar", e);
					}
				}
			});
		}
		return displayPicture;
	}

	private JPanel getTopPanel() {
		if (topPanel == null) {
			topPanel = new JPanel(FLOW_LAYOUT_MGR);
			topPanel.setPreferredSize(TOP_PANEL_DEFAULT_SIZE);
			topPanel.setSize(TOP_PANEL_DEFAULT_SIZE);
			topPanel.setMinimumSize(TOP_PANEL_MINIMUM_SIZE);
			topPanel.setMaximumSize(TOP_PANEL_MAXIMUM_SIZE);
			topPanel.add(getLabelUserName());
			topPanel.add(getLabelWrote());
		}
		return topPanel;
	}

	private JLabel getLabelUserName() {
		if (lblUserName == null) {
			lblUserName = createUserLabel(twitterStatus.getScreenName(), SynthFonts.BOLD_FONT12_PURPLE102_45_145);
		}
		return lblUserName;
	}

	private JLabel getLabelWrote() {
		if (lblWrote == null) {
			lblWrote = new JLabel();
			lblWrote.setName(SynthFonts.PLAIN_FONT12_GRAY100_100_100);
		}
		return lblWrote;
	}

	private JPanel getMessagePanel() {
		if (messagePanel == null) {
			messagePanel = new JPanel();
			messagePanel.setLayout(MESSAGE_LAYOUT_MGR);
			String message = twitterStatus.getText();
			String[] words = message.split(" ");
			for (String word : words) {
				messagePanel.add(createKeywordLabel(word));
			}
		}
		return messagePanel;
	}

	private JLabel createKeywordLabel(String word) {
		try {
			if (word.startsWith("#")) {
				return new LinkLabel(word, TWITTER_SEARCH_URL + URLEncoder.encode(word, "UTF-8"), SynthFonts.PLAIN_FONT12_PURPLE8102_45_145);
			} else if (word.startsWith("@")) {
				TwitterUserLabel twitterUserLabel = createUserLabel(word, SynthFonts.PLAIN_FONT12_PURPLE8102_45_145);
				if (twitterStatus.getLoggedInScreenName().equalsIgnoreCase(twitterUserLabel.getScreenName())) {
					setStatusType(TwitterStatusType.MENTIONS);
				}
				return twitterUserLabel;
			} else if (word.startsWith(HTTP)) {
				return new LinkLabel(word, word, SynthFonts.PLAIN_FONT12_PURPLE8102_45_145);
			}
		} catch (Exception e) {
			LOG.error("Could not create link label for: " + word, e);
		}
		JLabel label = new JLabel(word);
		label.setName(SynthFonts.PLAIN_FONT12_GRAY100_100_100);
		return label;
	}

	private String createTwitterUserURL(String word) {
		return new StringBuilder(TWITTER_URL).append(word).toString();
	}

	private JPanel getBottomPanel() {
		if (bottomPanel == null) {
			bottomPanel = new JPanel(FLOW_LAYOUT_MGR);
			bottomPanel.setSize(TOP_PANEL_DEFAULT_SIZE);
			bottomPanel.setPreferredSize(TOP_PANEL_DEFAULT_SIZE);
			bottomPanel.setMaximumSize(TOP_PANEL_MAXIMUM_SIZE);
			bottomPanel.setMinimumSize(TOP_PANEL_MINIMUM_SIZE);
			final JPanel btnSeparatorPanel = new JPanel();
			btnSeparatorPanel.setPreferredSize(BUTTON_SEPARATOR_PANEL_SIZE);
			btnSeparatorPanel.setMinimumSize(BUTTON_SEPARATOR_PANEL_SIZE);
			btnSeparatorPanel.setSize(BUTTON_SEPARATOR_PANEL_SIZE);
			btnSeparatorPanel.setMaximumSize(BUTTON_SEPARATOR_PANEL_SIZE);
			final JPanel btnGapPanel = new JPanel();
			btnGapPanel.setPreferredSize(BUTTON_GAP_SIZE);
			btnGapPanel.setMinimumSize(BUTTON_GAP_SIZE);
			btnGapPanel.setSize(BUTTON_GAP_SIZE);
			btnGapPanel.setMaximumSize(BUTTON_GAP_SIZE);
			bottomPanel.add(getLabelDate());
			bottomPanel.add(getLabelSource());
			bottomPanel.add(getLabelExtra());
			bottomPanel.add(getLabelExtraUsername());
			bottomPanel.add(btnSeparatorPanel);
			bottomPanel.add(getButtonReply());
			bottomPanel.add(getButtonDirect());
			bottomPanel.add(btnGapPanel);
			bottomPanel.add(getButtonRetweet());
			bottomPanel.addHierarchyBoundsListener(new HierarchyBoundsAdapter() {
				@Override
				public void ancestorResized(HierarchyEvent e) {
					int totalWidth = getLabelDate().getWidth() + getLabelSource().getWidth() + getLabelExtra().getWidth()
							+ getLabelExtraUsername().getWidth() + btnSeparatorPanel.getWidth() + btnGapPanel.getWidth()
							+ (getButtonReply().getWidth() * 2) + 20;
					if (totalWidth > TwitterStatusPanel.this.getWidth()) {
						bottomPanel.remove(getLabelExtra());
						bottomPanel.remove(getLabelExtraUsername());
					} else if (bottomPanel.getComponentCount() < 8) {
						bottomPanel.add(getLabelExtra(), 2);
						bottomPanel.add(getLabelExtraUsername(), 3);
					}
				}
			});
		}
		return bottomPanel;
	}

	private JLabel getLabelExtra() {
		if (lblExtra == null) {
			lblExtra = new JLabel();
			lblExtra.setName(SynthFonts.BOLD_FONT10_CLEAR_GRAY180_180_180);
		}
		return lblExtra;
	}

	private JLabel getLabelExtraUsername() {
		if (lblExtraName == null) {
			lblExtraName = createUserLabel(null, SynthFonts.PLAIN_FONT12_PURPLE8102_45_145);
		}
		return lblExtraName;
	}

	private JButton getButtonRetweet() {
		if (btnRetweet == null) {
			btnRetweet = new TwitterStatusButton(twitterStatus);
			btnRetweet.setVisible(false);
		}
		return btnRetweet;
	}

	private JButton getButtonReply() {
		if (btnReply == null) {
			btnReply = new TwitterStatusButton(twitterStatus);
			btnReply.setVisible(false);
		}
		return btnReply;
	}

	private JButton getButtonDirect() {
		if (btnDirect == null) {
			btnDirect = new TwitterStatusButton(twitterStatus);
			btnDirect.setVisible(false);
		}
		return btnDirect;
	}

	private JLabel getLabelSource() {
		if (lblSource == null) {
			try {
				new ParserDelegator().parse(new StringReader(twitterStatus.getSource()), new HTMLEditorKit.ParserCallback() {
					public void handleStartTag(Tag tag, MutableAttributeSet attributes, int pos) {
						if (Tag.A.equals(tag)) {
							sourceUrl = (String) attributes.getAttribute(Attribute.HREF);
						}
					};

					public void handleText(char[] data, int pos) {
						sourceValue = new StringBuilder().append(data).append(POINT).toString();
					};
				}, false);
			} catch (Exception e) {
				LOG.error("Unable to parse twitter source", e);
			}

			lblSource = new LinkLabel(sourceValue, sourceUrl, SynthFonts.BOLD_FONT10_CLEAR_GRAY180_180_180);
		}
		return lblSource;
	}

	private JLabel getLabelDate() {
		if (lblDate == null) {
			StringBuilder dateAndSource = new StringBuilder(Formatters.formatDate(twitterStatus.getDateCreatedAt(),
					DATE_PATTERN)).append(VIA);
			lblDate = new JLabel(dateAndSource.toString());
			lblDate.setName(SynthFonts.BOLD_FONT10_CLEAR_GRAY180_180_180);
			lblDate.setMaximumSize(TOP_PANEL_MAXIMUM_SIZE);
			lblDate.setMinimumSize(TOP_PANEL_MINIMUM_SIZE);

		}
		return lblDate;
	}

	@Override
	public void internationalize(Messages messages) {
		getLabelWrote().setText(" " + messages.getMessage("twitter.status.wrote"));
		getButtonReply().setText(messages.getMessage("twitter.status.reply"));
		getButtonDirect().setText(messages.getMessage("twitter.status.direct"));
		getButtonRetweet().setText(messages.getMessage("twitter.status.retweet"));
		if (twitterStatus.isRetweeted()) {
			getLabelExtra().setText(messages.getMessage("twitter.status.retweeted") + SPACE);
			String name = twitterStatus.getScreenName();
			getLabelExtraUsername().setText(name);
			getLabelUserName().setText(twitterStatus.getRetweeterScreenName());
		} else if (twitterStatus.isReplied()) {
			getLabelExtra().setText(messages.getMessage("twitter.status.replied") + SPACE);
			String name = twitterStatus.getInReplyToScreenName();
			getLabelExtraUsername().setText(name);
		}
		for (TwitterUserLabel userLabel : userLabels) {
			userLabel.setToolTipText(messages.getMessage("twitter.status.show.profile", userLabel.getScreenName()));
		}
	}

	@Override
	public void setMessages(Messages messages) {
		messages.add(this);
	}

	@Override
	public void removeMessages(Messages messages) {
		messages.remove(this);
	}

	private JPanel getSeparator() {
		separator = new JPanel();
		separator.setName(SEPARATOR_NAME);
		separator.setMinimumSize(SEPARATOR_DEFAULT_SIZE);
		separator.setMaximumSize(SEPARATOR_HORIZONTAL_SIZE);
		separator.setSize(SEPARATOR_DEFAULT_SIZE);
		separator.setPreferredSize(SEPARATOR_DEFAULT_SIZE);
		return separator;
	}

	private void doMouseOver(boolean mouseOvered) {
		if (twitterStatus.isDirect()) {
			getButtonDirect().setVisible(mouseOvered);
		} else {
			getButtonReply().setVisible(mouseOvered);
			getButtonRetweet().setVisible(mouseOvered);
		}
		TwitterStatusPanel.this.setName(mouseOvered ? backgroundOverStyle : backgroundStyle);
	}

	private TwitterUserLabel createUserLabel(final String text, final String style) {
		TwitterUserLabel twitterUserLabel = new TwitterUserLabel(text, style);
		userLabels.add(twitterUserLabel);
		return twitterUserLabel;
	}

	private final class MouseOverListener extends MouseAdapter {
		@Override
		public void mouseEntered(MouseEvent e) {
			doMouseOver(true);
			if (activePanel != null && activePanel != TwitterStatusPanel.this) {
				activePanel.doMouseOver(false);
			}
			activePanel = TwitterStatusPanel.this;
		}

		@Override
		public void mouseExited(MouseEvent e) {
			if(TwitterStatusPanel.this.isShowing()){
				Rectangle absoluteBounds = new Rectangle(TwitterStatusPanel.this.getLocationOnScreen(),
								TwitterStatusPanel.this.getSize());
				if (!absoluteBounds.contains(e.getLocationOnScreen())) {
					doMouseOver(false);
				}
			}
		}
	}

	// TODO EXTRACT THIS CLASS
	protected final class TwitterUserLabel extends JLabel {
		private static final long serialVersionUID = 1775275538988417858L;

		public TwitterUserLabel(final String text, final String style) {
			setText(text);
			setName(style);
			setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		}

		public String getScreenName() {
			return getText() != null ? getText().replaceAll("[^a-zA-Z0-9_]", "") : null;
		}
	}

}
