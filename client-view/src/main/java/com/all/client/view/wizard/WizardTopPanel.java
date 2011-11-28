package com.all.client.view.wizard;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import com.all.core.common.view.SynthFonts;
import com.all.i18n.Messages;
import com.all.observ.ObserveObject;
import com.all.observ.ObservValue;
import com.all.observ.Observable;
import com.all.observ.ObserverCollection;

public class WizardTopPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private static final int CHECKBOX_ICON_TEXT_GAP = 12;

	private static final Dimension DEFAULT_SIZE = new Dimension(692, 146);

	private static final Rectangle ALERT_INSTRUCTIONS_LABEL_BOUNDS = new Rectangle(457, 74, 400, 20);

	private static final Rectangle ITUNES_BUTTON_BOUNDS = new Rectangle(59, 36, 176, 68);

	private static final Rectangle ITUNES_INSTRUCTIONS_LABEL_BOUNDS = new Rectangle(8, 92, 262, 46);

	private static final Rectangle ITUNES_TITLE_BOUNDS = new Rectangle(8, 8, 262, 36);

	private static final Rectangle FACEBOOK_CHECKBOX_BOUNDS = new Rectangle(292, 106, 400, 20);

	private static final Rectangle MCALERT_CHECKBOX_BOUNDS = new Rectangle(292, 54, 400, 20);

	private static final Rectangle MCALERT_LABEL_BOUNDS = new Rectangle(324, 74, 400, 20);

	private static final Rectangle SETTINGS_LABEL_BOUNDS = new Rectangle(292, 8, 391, 40);

	private static final String CHECKBOX_NAME = "checkButtonFont15Wizard";

	private static final String ITUNES_BUTTON_NAME = "importItunesButtonWizard";

	private JLabel itunesTitleLabel;

	private JButton itunesButton;

	private JToggleButton facebookCheckBox;

	private JToggleButton mcAlertCheckBox;

	private JLabel settingsLabel;

	private JTextPane itunesInstructionsLabel;

	private JLabel mcAlertLabel;

	private JLabel alertInstructionsLabel;

	private Observable<ObserveObject> onItunesButtonEvent = new Observable<ObserveObject>();

	private Observable<ObservValue<Boolean>> onFacebookCheckBoxEvent = new Observable<ObservValue<Boolean>>();

	private Observable<ObservValue<Boolean>> onMCCheckBoxEvent = new Observable<ObservValue<Boolean>>();

	public WizardTopPanel() {
		initialize();
	}

	private void initialize() {
		this.setLayout(null);
		this.setPreferredSize(DEFAULT_SIZE);
		this.add(getSettingsLabel());
		this.add(getItunesTitleLabel());
		this.add(getItunesButton());
		this.add(getItunesInstructionsLabel());
		this.add(getMCAlertCheckBox());
		this.add(getMCAlertLabel());
		this.add(getMCAlertInstructionsLabel());
		this.add(getFacebookCheckBox());
	}

	private JLabel getMCAlertInstructionsLabel() {
		if (alertInstructionsLabel == null) {
			alertInstructionsLabel = new JLabel();
			alertInstructionsLabel.setBounds(ALERT_INSTRUCTIONS_LABEL_BOUNDS);
			alertInstructionsLabel.setName(SynthFonts.PLAIN_FONT12_PURPLE70_40_90);
			alertInstructionsLabel.addMouseListener(new InstructionsLabelMouseListener());
		}
		return alertInstructionsLabel;
	}

	private JTextPane getItunesInstructionsLabel() {
		if (itunesInstructionsLabel == null) {
			itunesInstructionsLabel = new JTextPane();
			itunesInstructionsLabel.setBounds(ITUNES_INSTRUCTIONS_LABEL_BOUNDS);
			itunesInstructionsLabel.setName(SynthFonts.PLAIN_FONT11_PURPLE70_40_90);
			itunesInstructionsLabel.setEditable(false);
			itunesInstructionsLabel.setEnabled(false);
			StyledDocument doc = itunesInstructionsLabel.getStyledDocument();
			SimpleAttributeSet center = new SimpleAttributeSet();
			StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
			doc.setParagraphAttributes(0, doc.getLength(), center, false);
		}
		return itunesInstructionsLabel;
	}

	private JLabel getSettingsLabel() {
		if (settingsLabel == null) {
			settingsLabel = new JLabel();
			settingsLabel.setBounds(SETTINGS_LABEL_BOUNDS);
			settingsLabel.setName(SynthFonts.BOLD_FONT16_PURPLE_70_40_90);
		}
		return settingsLabel;
	}

	JToggleButton getMCAlertCheckBox() {
		if (mcAlertCheckBox == null) {
			mcAlertCheckBox = new JToggleButton();
			mcAlertCheckBox.setBounds(MCALERT_CHECKBOX_BOUNDS);
			mcAlertCheckBox.setIconTextGap(CHECKBOX_ICON_TEXT_GAP);
			mcAlertCheckBox.setHorizontalAlignment(SwingConstants.LEFT);
			mcAlertCheckBox.setName(CHECKBOX_NAME);
			mcAlertCheckBox.setSelected(true);
			mcAlertCheckBox.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					onMCCheckBoxEvent.fire(new ObservValue<Boolean>(mcAlertCheckBox.isSelected()));
				}
			});
		}
		return mcAlertCheckBox;
	}

	private JLabel getMCAlertLabel() {
		if (mcAlertLabel == null) {
			mcAlertLabel = new JLabel();
			mcAlertLabel.setBounds(MCALERT_LABEL_BOUNDS);
			mcAlertLabel.setName(SynthFonts.PLAIN_FONT15_PURPLE70_40_90);
			mcAlertLabel.addMouseListener(new InstructionsLabelMouseListener());
		}
		return mcAlertLabel;
	}

	JToggleButton getFacebookCheckBox() {
		if (facebookCheckBox == null) {
			facebookCheckBox = new JToggleButton();
			facebookCheckBox.setBounds(FACEBOOK_CHECKBOX_BOUNDS);
			facebookCheckBox.setIconTextGap(CHECKBOX_ICON_TEXT_GAP);
			facebookCheckBox.setHorizontalAlignment(SwingConstants.LEFT);
			facebookCheckBox.setName(CHECKBOX_NAME);
			facebookCheckBox.setSelected(true);
			facebookCheckBox.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					onFacebookCheckBoxEvent.fire(new ObservValue<Boolean>(facebookCheckBox.isSelected()));
				}
			});
		}
		return facebookCheckBox;
	}

	JButton getItunesButton() {
		if (itunesButton == null) {
			itunesButton = new JButton();
			itunesButton.setBounds(ITUNES_BUTTON_BOUNDS);
			itunesButton.setName(ITUNES_BUTTON_NAME);
			itunesButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					onItunesButtonEvent.fire(ObserveObject.EMPTY);
					itunesButton.setEnabled(false);
				}
			});
		}
		return itunesButton;
	}

	private JLabel getItunesTitleLabel() {
		if (itunesTitleLabel == null) {
			itunesTitleLabel = new JLabel();
			itunesTitleLabel.setBounds(ITUNES_TITLE_BOUNDS);
			itunesTitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
			itunesTitleLabel.setName(SynthFonts.PLAIN_FONT16_PURPLE70_40_90);
		}
		return itunesTitleLabel;
	}

	class InstructionsLabelMouseListener extends MouseAdapter {
		@Override
		public void mouseClicked(MouseEvent arg0) {
			mcAlertCheckBox.setSelected(!mcAlertCheckBox.isSelected());
		}
	}

	public void internationalize(Messages messages) {
		alertInstructionsLabel.setText(messages.getMessage("wizard.mc.instructions.label"));
		facebookCheckBox.setText(messages.getMessage("wizard.facebook.checkbox"));
		itunesTitleLabel.setText(messages.getMessage("wizard.itunes.label"));
		mcAlertLabel.setText(messages.getMessage("wizard.mc.label"));
		mcAlertCheckBox.setText(messages.getMessage("wizard.mc.checkbox"));
		itunesInstructionsLabel.setText(messages.getMessage("wizard.itunes.instructions.label"));
		settingsLabel.setText(messages.getMessage("wizard.settings.label"));
	}

	public ObserverCollection<ObserveObject> onItunesButton() {
		return onItunesButtonEvent;
	}

	public ObserverCollection<ObservValue<Boolean>> onFacebookCheckBox() {
		return onFacebookCheckBoxEvent;
	}

	public ObserverCollection<ObservValue<Boolean>> onMCCheckBox() {
		return onMCCheckBoxEvent;
	}
}
