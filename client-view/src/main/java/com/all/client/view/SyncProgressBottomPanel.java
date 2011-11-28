package com.all.client.view;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Rectangle;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.UIManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.all.client.view.i18n.InternationalizableComponent;
import com.all.client.view.i18n.Ji18nLabel;
import com.all.core.common.view.SynthFonts;
import com.all.i18n.Internationalizable;
import com.all.i18n.Messages;

@Component
@Scope("prototype")
public class SyncProgressBottomPanel extends JPanel implements Internationalizable, InternationalizableComponent {

	private static final long serialVersionUID = 4166803045744980441L;

	private static final int ICON_TEXT_GAP = 7;

	private static final int PROGRESS_BAR_DEFAULT_VALUE = 0;

	private static final int PROGRESS_BAR_MAXIMUM_VALUE = 100;

	private static final Dimension PROGRESS_BAR_PANEL_MAXIMUM_SIZE = new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
	
	private static final Dimension PROGRESS_BAR_PANEL_MINIMUM_SIZE = new Dimension(Integer.MIN_VALUE, Integer.MIN_VALUE);

	private static final Dimension PROGRESS_PANEL_DIMENSION = new Dimension(168, 28);

	private static final Dimension SEPARATOR_PANEL_PREFERRED_SIZE = new Dimension(2, 26);

	private static final Dimension SPACER_DEFAULT_SIZE = new Dimension(7, 26);

	private static final Rectangle PROGRESS_BAR_BOUNDS = new Rectangle(0, 7, 140, 12);

	private static final Rectangle PROGRESS_LABEL_BOUNDS = new Rectangle(145, 7, 38, 14);
	
	private static final String SEPARATOR_PANEL_NAME = "verticalSeparator";

	private static final String ICON_NAME = "libraryIcon";

	private static final String PROGRESS_BAR_NAME = "bigProgressBar";

	private Ji18nLabel label = null;
	
	private JPanel progressBarPanel = null;
	
	private JSlider progressBar = null;
	
	private JLabel progressLabel = null;

	private Messages messages;

	public SyncProgressBottomPanel() {
		super();
		this.setName("bottomProgressPanel");
		JPanel separatorPanel = new JPanel();
		separatorPanel.setPreferredSize(SEPARATOR_PANEL_PREFERRED_SIZE);
		separatorPanel.setName(SEPARATOR_PANEL_NAME);

		this.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
		this.add(BottomPanel.getSeparatorPanel());
		this.add(getSpacer());
		this.add(getLabel());
		this.add(getSpacer());
		this.add(getProgressBarPanel());
		this.add(getSpacer());
		this.add(separatorPanel);
		this.setVisible(false);
		this.setMessage("bottomPanel.synching");
	}

	protected final JPanel getSpacer() {
		JPanel spacer = new JPanel();
		spacer.setSize(SPACER_DEFAULT_SIZE);
		spacer.setMaximumSize(SPACER_DEFAULT_SIZE);
		spacer.setMinimumSize(SPACER_DEFAULT_SIZE);
		spacer.setPreferredSize(SPACER_DEFAULT_SIZE);
		return spacer;
	}

	private Ji18nLabel getLabel() {
		if (label == null) {
			label = new Ji18nLabel();
			label.setName(SynthFonts.BOLD_FONT12_GRAY100_100_100);
			label.setIconTextGap(ICON_TEXT_GAP);
			label.setIcon(UIManager.getIcon(ICON_NAME));
		}
		return label;
	}

	private JPanel getProgressBarPanel() {
		if (progressBarPanel == null) {
			progressBarPanel = new JPanel();
			progressBarPanel.setPreferredSize(PROGRESS_PANEL_DIMENSION);
			progressBarPanel.setMinimumSize(PROGRESS_BAR_PANEL_MINIMUM_SIZE);
			progressBarPanel.setMaximumSize(PROGRESS_BAR_PANEL_MAXIMUM_SIZE);
			progressBarPanel.setLayout(null);
			progressBarPanel.add(getProgressBar());
			progressBarPanel.add(getProgressLabel());
		}
		return progressBarPanel;
	}

	private JSlider getProgressBar() {
		if (progressBar == null) {
			progressBar = new JSlider();
			progressBar.setName(PROGRESS_BAR_NAME);
			progressBar.setMaximum(PROGRESS_BAR_MAXIMUM_VALUE);
			progressBar.setRequestFocusEnabled(false);
			progressBar.setValue(PROGRESS_BAR_DEFAULT_VALUE);
			progressBar.setPaintLabels(false);
			progressBar.setOpaque(false);
			progressBar.setFocusable(false);
			progressBar.setEnabled(false);
			progressBar.setBounds(PROGRESS_BAR_BOUNDS);
		}
		return progressBar;
	}

	private JLabel getProgressLabel() {
		if (progressLabel == null) {
			progressLabel = new JLabel();
			progressLabel.setName(SynthFonts.BOLD_FONT12_GRAY100_100_100);
			progressLabel.setBounds(PROGRESS_LABEL_BOUNDS);
		}
		return progressLabel;
	}

	public void setIcon(String iconName) {
		getLabel().setIcon(UIManager.getIcon(iconName));
	}
	
	public void updateProgress(int percentage) {
		progressBar.setValue(percentage);
		progressLabel.setText(messages.getMessage("bottomPanel.syncingPercentage", percentage + ""));
	}

	@Override
	public void internationalize(Messages messages) {
		progressLabel.setText(messages.getMessage("bottomPanel.syncingPercentage", "0"));
	}

	@Override
	public void removeMessages(Messages messages) {
		messages.remove(this);
		getLabel().removeMessages(messages);
	}

	@Autowired
	@Override
	public void setMessages(Messages messages) {
		this.messages = messages;
		messages.add(this);
		getLabel().setMessages(messages);
	}

	@Override
	public String getText() {
		return getLabel().getText();
	}

	@Override
	public void setText(String text) {
		getLabel().setText(text);
	}

	@Override
	public void setMessage(String key, String... parameters) {
		getLabel().setMessage(key, parameters);
	}

}
