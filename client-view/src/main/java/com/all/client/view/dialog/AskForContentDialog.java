package com.all.client.view.dialog;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.all.core.common.view.SynthFonts;
import com.all.i18n.Messages;

public class AskForContentDialog extends AllDialog {

	private static final long serialVersionUID = 1L;
	private GrayPanel grayPanel;
	private JPanel separator;
	private JButton cancelButton;
	private JButton requestButton;
	private JLabel headerLabel;
	private JPanel contentPanel;
	private boolean request = false;

	private static final Rectangle PANEL_SEPARATOR_BOUNDS = new Rectangle(10, 133, 370, 2);
	private static final Rectangle GRAY_PANEL_BOUNDS = new Rectangle(12, 47, 364, 76);
	private static final Rectangle MC_ICON_BOUNDS = new Rectangle(16, 16, 40, 40);
	private static final Rectangle CONTENT_LABEL_BOUNDS = new Rectangle(72, 0, 280, 70);
	private static final Rectangle REQUEST_BUTTON_BOUNDS = new Rectangle(210, 142, 80, 22);
	private static final Rectangle CANCEL_BUTTON_BOUNDS = new Rectangle(120, 142, 80, 22);
	private static final Rectangle HEADER_LABEL_BOUNDS = new Rectangle(65, 13, 320, 20);

	private static final String PANEL_SEPARATOR_NAME = "bottomPanelSeparator";
	private static final String GRAY_PANEL_NAME = "selectedContactInfoPanel";
	private static final String MC_ICON_NAME = "MCIcon";
	private static final String BUTTON_NAME = "buttonName";

	private static final Dimension DIALOG_DIMENSION = new Dimension(390, 171);

	public AskForContentDialog(Frame frame, Messages messages) {
		super(frame, messages);
		initialize();
		initializeContentPane();
	}

	@Override
	String dialogTitle(Messages messages) {
		return messages.getMessage("cannotSendAlert.tittle");
	}

	@Override
	JComponent getContentComponent() {
		if (contentPanel == null) {
			contentPanel = new JPanel();
			contentPanel.setSize(DIALOG_DIMENSION);
			contentPanel.setPreferredSize(DIALOG_DIMENSION);
			contentPanel.setMaximumSize(DIALOG_DIMENSION);
			contentPanel.setMinimumSize(DIALOG_DIMENSION);
			contentPanel.setLayout(null);
			contentPanel.add(getHeaderLabel());
			contentPanel.add(getGrayPanel());
			contentPanel.add(getSeparator());
			contentPanel.add(getCancelButton());
			contentPanel.add(getRequestButton());
		}
		return contentPanel;
	}

	@Override
	void internationalizeDialog(Messages messages) {
		getCancelButton().setText(messages.getMessage("cancel"));
		getRequestButton().setText(messages.getMessage("cannotSendAlert.button.request"));
		grayPanel.getContentLabel().setText(messages.getMessage("cannotSendAlert.content"));
		getHeaderLabel().setText(messages.getMessage("cannotSendAlert.header"));
	}

	public void initialize() {
		setModal(true);
		setSize(DIALOG_DIMENSION);
		setPreferredSize(DIALOG_DIMENSION);
		setMaximumSize(DIALOG_DIMENSION);
		setMinimumSize(DIALOG_DIMENSION);
	}

	public GrayPanel getGrayPanel() {
		if (grayPanel == null) {
			grayPanel = new GrayPanel();
		}
		return grayPanel;
	}

	public JPanel getSeparator() {
		if (separator == null) {
			separator = new JPanel();
			separator.setBounds(PANEL_SEPARATOR_BOUNDS);
			separator.setName(PANEL_SEPARATOR_NAME);
		}
		return separator;
	}

	public JButton getCancelButton() {
		if (cancelButton == null) {
			cancelButton = new JButton();
			cancelButton.setName(BUTTON_NAME);
			cancelButton.setBounds(CANCEL_BUTTON_BOUNDS);
			cancelButton.addActionListener(new CloseListener());
		}
		return cancelButton;
	}

	public JButton getRequestButton() {
		if (requestButton == null) {
			requestButton = new JButton();
			requestButton.setName(BUTTON_NAME);
			requestButton.setBounds(REQUEST_BUTTON_BOUNDS);
			requestButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					request = true;
					closeDialog();
				}
			});
		}
		return requestButton;
	}

	public JLabel getHeaderLabel() {
		if (headerLabel == null) {
			headerLabel = new JLabel();
			headerLabel.setBounds(HEADER_LABEL_BOUNDS);
			headerLabel.setName(SynthFonts.BOLD_ITALIC_FONT16_PURPLE49_19_49);
		}
		return headerLabel;
	}

	class GrayPanel extends JPanel {
		private static final long serialVersionUID = 1L;
		private JLabel mcIcon;
		private JLabel contentLabel;

		public GrayPanel() {
			init();
		}

		private void init() {
			setName(GRAY_PANEL_NAME);
			setBounds(GRAY_PANEL_BOUNDS);
			setLayout(null);
			add(getContentLabel());
			add(getMcIcon());
		}

		private JLabel getMcIcon() {
			if (mcIcon == null) {
				mcIcon = new JLabel();
				mcIcon.setBounds(MC_ICON_BOUNDS);
				mcIcon.setName(MC_ICON_NAME);
			}
			return mcIcon;
		}

		public JLabel getContentLabel() {
			if (contentLabel == null) {
				contentLabel = new JLabel();
				contentLabel.setName(SynthFonts.PLAIN_FONT12_GRAY80_80_80);
				contentLabel.setBounds(CONTENT_LABEL_BOUNDS);
			}
			return contentLabel;
		}
	}

	public boolean shouldRequest() {
		return request;
	}

}
