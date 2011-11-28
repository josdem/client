package com.all.client.view.dialog;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;

import com.all.core.common.view.util.WindowDraggerMouseListener;
import com.all.i18n.Messages;

public class ConfirmationDeleteDialog extends AllDialog {

	private static final long serialVersionUID = 7126355930848598600L;

	private static final Dimension DEFAULT_SIZE = new Dimension(324, 110);
	private static final Dimension MAIN_PANEL_SIZE = new Dimension(324, 110);
	private static final Rectangle PANEL_SEPARATOR_BOUNDS = new Rectangle(5, 71, 313, 2);
	private static final String PANEL_SEPARATOR_NAME = "bottomPanelSeparator";
	private static final String WARNING_PANEL_NAME = "grayRoundedBorderPanel";

	public static final int APPROVE_OPTION = JFileChooser.APPROVE_OPTION;
	public static final int CANCEL_OPTION = JFileChooser.CANCEL_OPTION;
	public static final int ERROR_OPTION = JFileChooser.ERROR_OPTION;

	private JButton cancelButton = null;
	private JPanel contentPanel = null;
	private JLabel messageLabel = null;
	private JPanel panelSeparator = null;
	private JButton removeButton = null;
	private JPanel warningPanel = null;
	private JLabel warningIconLabel = null;

	private final boolean hasTracks;
	private int returnValue;

	public ConfirmationDeleteDialog(JFrame frame, Messages messages, boolean hasTracks) {
		super((JFrame) frame, messages);
		this.hasTracks = hasTracks;
		initialize();
		initWinFunctions();
	}

	private void initialize() {
		this.setSize(DEFAULT_SIZE);
		this.setPreferredSize(DEFAULT_SIZE);
		this.setMinimumSize(DEFAULT_SIZE);
		this.setMaximumSize(DEFAULT_SIZE);
		initializeContentPane();
	}

	@Override
	public final String dialogTitle(Messages messages) {
		if (hasTracks) {
			return messages.getMessage("deleteTrack.label");
		} else {
			return messages.getMessage("deletePlaylist.label");
		}
	}

	@Override
	public final JComponent getContentComponent() {
		if (contentPanel == null) {
			contentPanel = new JPanel();
			contentPanel.setSize(MAIN_PANEL_SIZE);
			contentPanel.setPreferredSize(MAIN_PANEL_SIZE);
			contentPanel.setMinimumSize(MAIN_PANEL_SIZE);
			contentPanel.setMaximumSize(MAIN_PANEL_SIZE);
			contentPanel.setLayout(null);
			contentPanel.setOpaque(false);

			contentPanel.add(getWarningPanel());
			contentPanel.add(getPanelSeparator());
			contentPanel.add(getRemoveButton());
			contentPanel.add(getCancelButton());
		}
		return contentPanel;
	}

	@Override
	public void internationalizeDialog(Messages messages) {
	}

	private JPanel getWarningPanel() {
		if (warningPanel == null) {
			warningPanel = new JPanel();
			warningPanel.setLayout(null);
			warningPanel.setName(WARNING_PANEL_NAME);
			warningPanel.setBounds(12, 8, 300, 55);
			warningPanel.add(getWarningIcon());
			warningPanel.add(getMessageLabel());
		}
		return warningPanel;
	}

	private JLabel getWarningIcon() {
		if (warningIconLabel == null) {
			warningIconLabel = new JLabel();
			warningIconLabel.setBounds(10, 10, 40, 35);
			Icon icon = UIManager.getDefaults().getIcon("icons.warningBig");
			warningIconLabel.setIcon(icon);
		}
		return warningIconLabel;
	}

	private JLabel getMessageLabel() {
		if (messageLabel == null) {
			messageLabel = new JLabel();
			messageLabel.setBounds(64, 5, 222, 40);
			if (hasTracks) {
				messageLabel.setText(getMessages().getMessage("deleteTrack.confirmation"));
			} else {
				messageLabel.setText(getMessages().getMessage("deletePlaylist.confirmation"));
			}
		}
		return messageLabel;
	}

	private JPanel getPanelSeparator() {
		if (panelSeparator == null) {
			panelSeparator = new JPanel();
			panelSeparator.setBounds(PANEL_SEPARATOR_BOUNDS);
			panelSeparator.setName(PANEL_SEPARATOR_NAME);
		}
		return panelSeparator;
	}

	private JButton getCancelButton() {
		if (cancelButton == null) {
			cancelButton = new JButton();
			cancelButton.setBounds(77, 80, 80, 22);
			cancelButton.setName("gray");
			cancelButton.setText(getMessages().getMessage("cancel"));
		}
		return cancelButton;
	}

	private JButton getRemoveButton() {
		if (removeButton == null) {
			removeButton = new JButton();
			removeButton.setBounds(167, 80, 80, 22);
			removeButton.setName("gray");
			if (hasTracks) {
				removeButton.setText(getMessages().getMessage("deleteTrack.remove"));
			} else {
				removeButton.setText(getMessages().getMessage("deletePlaylist.remove"));
			}
		}
		return removeButton;
	}

	private void initWinFunctions() {
		getRemoveButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				yestButtonActionPerformed();
			}
		});
		getCancelButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cancelButtonActionPerformed();
			}
		});
		WindowDraggerMouseListener draggerMouseListener = new WindowDraggerMouseListener();
		draggerMouseListener.setup(getTitlePanel());
	}

	private void yestButtonActionPerformed() {
		returnValue = APPROVE_OPTION;
		this.setVisible(false);
	}

	private void cancelButtonActionPerformed() {
		returnValue = CANCEL_OPTION;
		this.setVisible(false);
	}

	/**
	 * Pops a custom directory chooser dialog Example
	 * 
	 * <pre>
	 * JFolderChooser chooser = new JFolderChooser(null);
	 * chooser.showDialog(parentFrame, null);
	 * </pre>
	 * 
	 * <p>
	 * 
	 * The <code>parent</code> argument determines two things: the frame on
	 * which the open dialog depends and the component whose position the look
	 * and feel should consider when placing the dialog. If the parent is a
	 * <code>Frame</code> object (such as a <code>JFrame</code>) then the dialog
	 * depends on the frame and the look and feel positions the dialog relative
	 * to the frame (for example, centered over the frame). If the parent is a
	 * component, then the dialog depends on the frame containing the component,
	 * and is positioned relative to the component (for example, centered over
	 * the component). If the parent is <code>null</code>, then the dialog
	 * depends on no visible window, and it's placed in a
	 * look-and-feel-dependent position such as the center of the screen.
	 * 
	 * @param parent
	 *            the parent component of the dialog; can be <code>null</code>
	 * @param approveButtonText
	 *            the text of the <code>ApproveButton</code>
	 * @return the return state of the file chooser on popdown:
	 *         <ul>
	 *         <li>CANCEL_OPTION <li>APPROVE_OPTION <li>ERROR_OPTION if an error
	 *         occurs or the dialog is dismissed
	 *         </ul>
	 * @exception HeadlessException
	 *                if GraphicsEnvironment.isHeadless() returns true.
	 * @see java.awt.GraphicsEnvironment#isHeadless
	 */
	public final int showDialog() {
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				returnValue = CANCEL_OPTION;
			}
		});
		getCancelButton().addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
					cancelButtonActionPerformed();
				}
			}
		});
		getCancelButton().requestFocus();
		returnValue = ERROR_OPTION;
		this.setVisible(true);
		this.dispose();
		return returnValue;
	}

}