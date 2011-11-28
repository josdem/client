package com.all.client.view.dialog;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.net.URI;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.all.core.common.view.SynthFonts;
import com.all.i18n.Messages;

public class CodecErrorDialog extends AllDialog {
	public enum CodecErrorType {
		PERIAN("popup.cantplaytrack.perian", "popup.cantplaytrack.perian.url"), KLITE("popup.cantplaytrack.klite",
				"popup.cantplaytrack.klite.url"), FLIP4MAC("popup.cantplaytrack.flip4mac", "popup.cantplaytrack.flip4mac.url");
		private final String url;
		private final String message;

		private CodecErrorType(String message, String url) {
			this.message = message;
			this.url = url;
		}
	}

	private static final long serialVersionUID = 1L;
	private static final Log log = LogFactory.getLog(CodecErrorDialog.class);

	private static final Dimension DEFAULT_SIZE = new Dimension(390, 200);

	private static final Dimension CONTENT_COMPONENT_DEFAULT_SIZE = new Dimension(390, 170);

	private static final Rectangle PANEL_SEPARATOR_BOUNDS = new Rectangle(5, 130, 380, 2);

	private static final String PANEL_SEPARATOR_NAME = "bottomPanelSeparator";

	private static final String WARNING_PANEL_NAME = "grayRoundedBorderPanel";

	private JPanel contentComponent = null;
	private JPanel panelSeparator = null;
	private JLabel messageLabel = null;
	private JLabel headerLabel = null;
	private JButton cancelButton = null;
	private JButton downloadButton = null;
	private JLabel warningLabel = null;
	private JPanel warningPanel = null;

	private final CodecErrorType type;

	public CodecErrorDialog(Frame frame, final Messages messages, final CodecErrorType type) {
		super(frame, messages);
		this.type = type;
		this.setModal(true);
		this.setSize(DEFAULT_SIZE);
		this.setPreferredSize(DEFAULT_SIZE);
		this.setMinimumSize(DEFAULT_SIZE);
		this.setMaximumSize(DEFAULT_SIZE);
		this.initializeContentPane();
		getCancelButton().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				CodecErrorDialog that = CodecErrorDialog.this;
				that.dispatchEvent(new WindowEvent(that, WindowEvent.WINDOW_CLOSING));
			}
		});
		getDownloadButton().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					java.awt.Desktop.getDesktop().browse(new URI(messages.getMessage(type.url)));
				} catch (Exception ex) {
					log.error(ex, ex);
				}
				CodecErrorDialog that = CodecErrorDialog.this;
				that.dispatchEvent(new WindowEvent(that, WindowEvent.WINDOW_CLOSING));
			}
		});
	}

	@Override
	String dialogTitle(Messages messages) {
		return messages.getMessage("popup.cantplaytrack.title");
	}

	@Override
	JComponent getContentComponent() {
		if (contentComponent == null) {
			contentComponent = new JPanel();
			contentComponent.setSize(CONTENT_COMPONENT_DEFAULT_SIZE);
			contentComponent.setPreferredSize(CONTENT_COMPONENT_DEFAULT_SIZE);
			contentComponent.setMinimumSize(CONTENT_COMPONENT_DEFAULT_SIZE);
			contentComponent.setMaximumSize(CONTENT_COMPONENT_DEFAULT_SIZE);
			contentComponent.setLayout(null);
			contentComponent.add(getHeaderLabel());
			contentComponent.add(getWarningPanel());
			contentComponent.add(getPanelSeparator());
			contentComponent.add(getCancelButton());
			contentComponent.add(getDownloadButton());
		}
		return contentComponent;
	}

	private JPanel getWarningPanel() {
		if (warningPanel == null) {
			warningPanel = new JPanel();
			warningPanel.setLayout(null);
			warningPanel.setName(WARNING_PANEL_NAME);
			warningPanel.setBounds(12, 54, 364, 70);
			warningPanel.add(getWarningIcon());
			warningPanel.add(getMessageLabel());
		}

		return warningPanel;
	}

	public JButton getCancelButton() {
		if (cancelButton == null) {
			cancelButton = new JButton("Cancel");
			cancelButton.setBounds(109, 140, 80, 22);
			cancelButton.setName("buttonCancel");
		}
		return cancelButton;
	}

	public JButton getDownloadButton() {
		if (downloadButton == null) {
			downloadButton = new JButton("Download");
			downloadButton.setBounds(199, 140, 80, 22);
			downloadButton.setName("buttonDownload");
		}
		return downloadButton;
	}

	private JPanel getPanelSeparator() {
		if (panelSeparator == null) {
			panelSeparator = new JPanel();
			panelSeparator.setBounds(PANEL_SEPARATOR_BOUNDS);
			panelSeparator.setName(PANEL_SEPARATOR_NAME);
		}
		return panelSeparator;
	}

	private JLabel getMessageLabel() {
		if (messageLabel == null) {
			messageLabel = new JLabel(getMessages().getMessage(type.message));
			messageLabel.setBounds(60, 5, 300, 45);
		}
		return messageLabel;
	}

	private JLabel getHeaderLabel() {
		if (headerLabel == null) {
			headerLabel = new JLabel(getMessages().getMessage("popup.cantplaytrack.header"));
			headerLabel.setName(SynthFonts.BOLD_ITALIC_FONT14_51_17_41);
			headerLabel.setHorizontalAlignment(JLabel.CENTER);
			headerLabel.setBounds(0, 10, 390, 45);
		}
		return headerLabel;
	}

	private JLabel getWarningIcon() {
		if (warningLabel == null) {
			warningLabel = new JLabel();
			warningLabel.setBounds(10, 15, 40, 35);
			Icon icon = UIManager.getDefaults().getIcon("icons.warningBig");
			warningLabel.setIcon(icon);
		}
		return warningLabel;
	}

	@Override
	void internationalizeDialog(Messages messages) {
	}

}
