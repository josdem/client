package com.all.client.view.dialog;

import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.Locale;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import com.all.appControl.control.ViewEngine;
import com.all.core.actions.Actions;
import com.all.core.common.view.transparency.TransparencyManagerFactory;
import com.all.core.common.view.util.WindowDraggerMouseListener;
import com.all.core.common.view.util.WindowScalerMouseListener;
import com.all.i18n.Messages;

public class ConfirmationDeleteContactFromFolderDialog extends AllDialog {

	public static final int APPROVE_OPTION = JFileChooser.APPROVE_OPTION;
	public static final int CANCEL_OPTION = JFileChooser.CANCEL_OPTION;
	public static final int ERROR_OPTION = JFileChooser.ERROR_OPTION;
	public static final int SAVE_DIALOG = JFileChooser.SAVE_DIALOG;
	public static final int OPEN_DIALOG = JFileChooser.OPEN_DIALOG;

	private static final long serialVersionUID = 1L;

	private JButton cancelButton = null;
	private JButton removeButton = null;
	private JPanel contentPanel = null;

	private static Dimension defaultDimesion = new Dimension(350, 150);
	private final boolean allowSkip;
	private final ViewEngine viewEngine;

	public ConfirmationDeleteContactFromFolderDialog(Frame frame, Messages messages, boolean allowSkip,
			ViewEngine viewEngine) {
		super(frame, messages);
		this.allowSkip = allowSkip;
		this.viewEngine = viewEngine;
		initializeContentPane();
		initWinFunctions();
	}

	@Override
	public final String dialogTitle(Messages messages) {
		return messages.getMessage("deleteContactFromFolder.label");
	}

	@Override
	public final JComponent getContentComponent() {
		return getContentPanel();
	}

	@Override
	public void internationalizeDialog(Messages messages) {
	}

	private JPanel getSeparator() {
		JPanel separator = new JPanel();
		separator.setName("bottomPanelSeparator");
		return separator;
	}

	private JButton getCancelButton() {
		if (cancelButton == null) {
			cancelButton = new JButton();
			cancelButton.setName("buttonCancel");
			Dimension buttonSize = new Dimension(80, 22);
			cancelButton.setPreferredSize(buttonSize);
			cancelButton.setSize(buttonSize);
			cancelButton.setText(getMessages().getMessage("no"));
		}
		return cancelButton;
	}

	private JButton getRemoveButton() {
		if (removeButton == null) {
			removeButton = new JButton();
			removeButton.setName("buttonRemove");
			Dimension buttonSize = new Dimension(80, 22);
			removeButton.setPreferredSize(buttonSize);
			removeButton.setSize(buttonSize);
			removeButton.setText(getMessages().getMessage("yes"));
		}
		return removeButton;
	}

	private JPanel getContentPanel() {
		if (contentPanel == null) {
			contentPanel = new JPanel();
			contentPanel.setName("popupWindowBorder");
			contentPanel.setOpaque(false);
			contentPanel.setLayout(null);
			confirmationTitleLabel = new JLabel();

			final JCheckBox dontAskAgainCheckBox = new JCheckBox(getMessages().getMessage(
					"deleteContactFromFolder.dontAskAgain"));
			int checkWidth = 120;
			dontAskAgainCheckBox.setBounds(new Rectangle((defaultDimesion.width / 2) - (checkWidth / 2), 38, checkWidth, 20));
			dontAskAgainCheckBox.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					viewEngine.sendValueAction(Actions.UserPreference.SET_SKIP_CONTACT_DELETION_CONFIRMATION, Boolean
							.valueOf(dontAskAgainCheckBox.isSelected()));
				}
			});

			contentPanel.add(confirmationTitleLabel, null);
			confirmationTitleLabel.setBounds(5, 15, 340, 30);
			confirmationTitleLabel.setHorizontalAlignment(SwingConstants.CENTER);

			JPanel separator = getSeparator();

			int offset = 0;
			String message = getMessages().getMessage("deleteContact.confirmation");
			if (allowSkip) {
				contentPanel.add(dontAskAgainCheckBox, null);
				offset = 22;
				dontAskAgainCheckBox.setBounds(110, 55, 200, offset);
				message = getMessages().getMessage("deleteContactFromFolder.confirmation");
			}
			confirmationTitleLabel.setText(message);

			defaultDimesion = new Dimension(350, 128 + offset);

			contentPanel.setMinimumSize(defaultDimesion);
			contentPanel.setPreferredSize(defaultDimesion);
			contentPanel.setMaximumSize(defaultDimesion);
			contentPanel.setSize(defaultDimesion);

			contentPanel.add(separator, null);
			separator.setBounds(5, 63 + offset, defaultDimesion.width - 20, 1);
			contentPanel.add(getCancelButton(), null);
			getCancelButton().setBounds(80, 73 + offset, 80, 22);
			contentPanel.add(getRemoveButton(), null);
			getRemoveButton().setBounds(175, 73 + offset, 80, 22);
		}
		return contentPanel;
	}

	private String title;
	private JDialog dialog = this;
	private int returnValue;
	private JLabel confirmationTitleLabel = null;

	private void initWinFunctions() {
		getRemoveButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				yesButtonActionPerformded();
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

	public final void yesButtonActionPerformded() {
		returnValue = APPROVE_OPTION;
		dialog.setVisible(false);
	}

	public final void cancelButtonActionPerformed() {
		returnValue = CANCEL_OPTION;
		dialog.setVisible(false);
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
	 * The <code>parent</code> argument determines two things: the frame on which
	 * the open dialog depends and the component whose position the look and feel
	 * should consider when placing the dialog. If the parent is a
	 * <code>Frame</code> object (such as a <code>JFrame</code>) then the dialog
	 * depends on the frame and the look and feel positions the dialog relative to
	 * the frame (for example, centered over the frame). If the parent is a
	 * component, then the dialog depends on the frame containing the component,
	 * and is positioned relative to the component (for example, centered over the
	 * component). If the parent is <code>null</code>, then the dialog depends on
	 * no visible window, and it's placed in a look-and-feel-dependent position
	 * such as the center of the screen.
	 * 
	 * @param parent
	 *          the parent component of the dialog; can be <code>null</code>
	 * @param approveButtonText
	 *          the text of the <code>ApproveButton</code>
	 * @return the return state of the file chooser on popdown:
	 *         <ul>
	 *         <li>CANCEL_OPTION <li>APPROVE_OPTION <li>ERROR_OPTION if an error
	 *         occurs or the dialog is dismissed
	 *         </ul>
	 * @exception HeadlessException
	 *              if GraphicsEnvironment.isHeadless() returns true.
	 * @see java.awt.GraphicsEnvironment#isHeadless
	 */
	public final int showDialog() {
		i18n();
		createDialog();
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
		// dialog = null;
		return returnValue;
	}

	/**
	 * Creates and returns a new <code>JDialog</code> wrapping <code>this</code>
	 * centered on the <code>parent</code> in the <code>parent</code>'s frame.
	 * This method can be overriden to further manipulate the dialog, to disable
	 * resizing, set the location, etc. Example:
	 * 
	 * <pre>
	 * class MyFileChooser extends JFolderChooser {
	 * 	protected JDialog createDialog(Component parent) throws HeadlessException {
	 * 		JDialog dialog = super.createDialog(parent);
	 * 		dialog.setLocation(300, 200);
	 * 		dialog.setResizable(false);
	 * 		return dialog;
	 * 	}
	 * }
	 * </pre>
	 * 
	 * @param parent
	 *          the parent component of the dialog; can be <code>null</code>
	 * @return a new <code>JDialog</code> containing this instance
	 * @exception HeadlessException
	 *              if GraphicsEnvironment.isHeadless() returns true.
	 * @see java.awt.GraphicsEnvironment#isHeadless
	 */
	protected final JDialog createDialog() {
		JDialog newDialog = this;
		newDialog.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				Dialog resizedDialog = (Dialog) e.getComponent();
				Area shape = new Area(new RoundRectangle2D.Double(0, 0, resizedDialog.getWidth(), resizedDialog.getHeight(),
						15, 15));
				shape.add(new Area(new Rectangle2D.Float(0, 0, resizedDialog.getWidth(), 22)));
				TransparencyManagerFactory.getManager().setWindowShape(resizedDialog, shape);
			}
		});
		newDialog.setSize(defaultDimesion);
		newDialog.setPreferredSize(defaultDimesion);
		newDialog.setMinimumSize(defaultDimesion);
		newDialog.setComponentOrientation(this.getComponentOrientation());
		newDialog.getRootPane().setName("RootPane");
		WindowScalerMouseListener.setWindowScaler(newDialog);
		if (JDialog.isDefaultLookAndFeelDecorated()) {
			boolean supportsWindowDecorations = UIManager.getLookAndFeel().getSupportsWindowDecorations();
			if (supportsWindowDecorations) {
				newDialog.getRootPane().setWindowDecorationStyle(JRootPane.FILE_CHOOSER_DIALOG);
			}
		}
		newDialog.pack();
		return newDialog;
	}

	private void i18n() {
		Locale loc = Locale.getDefault();
		if (title == null) {
			title = UIManager.getString("FileChooser.openDialogTitleText", loc);
		}
		cancelButton.setText(getMessages().getMessage("cancel"));
		removeButton.setText(getMessages().getMessage("deleteTrack.remove"));
	}

}