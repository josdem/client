package com.all.client.view.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
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
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import com.all.core.common.view.transparency.TransparencyManagerFactory;
import com.all.core.common.view.util.WindowDraggerMouseListener;
import com.all.core.common.view.util.WindowScalerMouseListener;
import com.all.i18n.Messages;

public class ConfirmationGenericDialog extends AllDialog {

	private static final Insets CONTENT_PANEL_INSETS = new Insets(0, 7, 11, 7);
	private static final Dimension MAIN_PANEL_SIZE = new Dimension(7, 7);
	private static final Dimension BUTTON_SIZE = new Dimension(80, 22);
	private static final Dimension DIALOG_PANEL_SIZE = new Dimension(300, 27);
	private static final Insets PIXEL_SPANNER_INSETS = new Insets(2, 0, 0, 0);
	private static final Rectangle SEPARATOR_BOUNDS = new Rectangle(2, 66, 232, 1);
	private static final Dimension BUTTONS_PANEL_SIZE = new Dimension(300, 265);
	private static final Rectangle CONFIRMATION_TITLE_LABEL_BOUNDS = new Rectangle(0, 10, 280, 48);

	private static final Dimension DEFAULT_SIZE = new Dimension(300, 130);

	public static final int APPROVE_OPTION = JFileChooser.APPROVE_OPTION;
	public static final int CANCEL_OPTION = JFileChooser.CANCEL_OPTION;
	public static final int ERROR_OPTION = JFileChooser.ERROR_OPTION;
	public static final int SAVE_DIALOG = JFileChooser.SAVE_DIALOG;
	public static final int OPEN_DIALOG = JFileChooser.OPEN_DIALOG;

	private static final long serialVersionUID = 1L;

	private JPanel buttonsPanel = null;
	private JPanel dialogPane = null;
	private JButton cancelButton = null;
	private JButton removeButton = null;
	private JPanel contentPanel = null;
	private JPanel mainPanel = null;
	private JPanel middleButtonSpanner = null;
	private JPanel topOnePixelSpanner = null;
	private JPanel leftSpanner = null;
	private JPanel rightSpanner = null;
	private final String messageKey;
	private final String titleKey;
	private final String yesButton;

	public ConfirmationGenericDialog(JFrame frame, Messages messages, String messageKey, String titleKey, String yesButton) {
		super(frame, messages);
		this.messageKey = messageKey;
		this.titleKey = titleKey;
		this.yesButton = yesButton;
		initialize();
		initWinFunctions();
	}

	private void initialize() {
		setSize(DEFAULT_SIZE);
		initializeContentPane();
	}

	@Override
	public final String dialogTitle(Messages messages) {
		return messages.getMessage(titleKey);
	}

	@Override
	public final JComponent getContentComponent() {
		return getMainPanel();
	}

	@Override
	public void internationalizeDialog(Messages messages) {
	}

	private JPanel getButtonsPanel() {
		if (buttonsPanel == null) {
			confirmationTitleLabel = new JLabel();
			confirmationTitleLabel.setBounds(CONFIRMATION_TITLE_LABEL_BOUNDS);
			confirmationTitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
			confirmationTitleLabel.setName("gray");
			confirmationTitleLabel.setText(getMessages().getMessage(this.messageKey));
			buttonsPanel = new JPanel();
			buttonsPanel.setLayout(null);
			buttonsPanel.setMinimumSize(BUTTONS_PANEL_SIZE);
			buttonsPanel.setPreferredSize(BUTTONS_PANEL_SIZE);
			buttonsPanel.setMaximumSize(BUTTONS_PANEL_SIZE);
			buttonsPanel.add(getSeparator(), null);
			buttonsPanel.add(confirmationTitleLabel, null);
		}
		return buttonsPanel;
	}

	private JSeparator getSeparator() {
		JSeparator separator = new JSeparator(JSeparator.HORIZONTAL);
		separator.setBounds(SEPARATOR_BOUNDS);
		return separator;
	}

	private JPanel getDialogPane() {
		if (dialogPane == null) {
			GridBagConstraints rightSpannerConstraints = new GridBagConstraints();
			rightSpannerConstraints.gridx = 4;
			rightSpannerConstraints.weightx = 1.0;
			rightSpannerConstraints.gridy = 1;
			GridBagConstraints leftSpannerConstraints = new GridBagConstraints();
			leftSpannerConstraints.gridx = 0;
			leftSpannerConstraints.fill = GridBagConstraints.HORIZONTAL;
			leftSpannerConstraints.weightx = 1.0;
			leftSpannerConstraints.gridy = 1;
			GridBagConstraints pixelSpannerConstraints = new GridBagConstraints();
			pixelSpannerConstraints.gridx = 0;
			pixelSpannerConstraints.gridwidth = 5;
			pixelSpannerConstraints.weightx = 0.0;
			pixelSpannerConstraints.fill = GridBagConstraints.HORIZONTAL;
			pixelSpannerConstraints.anchor = GridBagConstraints.NORTH;
			pixelSpannerConstraints.insets = PIXEL_SPANNER_INSETS;
			pixelSpannerConstraints.weighty = 1.0;
			pixelSpannerConstraints.ipadx = 0;
			pixelSpannerConstraints.ipady = 0;
			pixelSpannerConstraints.gridy = 0;
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.gridx = 0;
			GridBagConstraints middleSpannerConstraints = new GridBagConstraints();
			middleSpannerConstraints.gridx = 2;
			middleSpannerConstraints.gridy = 1;
			GridBagConstraints removeButtonConstraints = new GridBagConstraints();
			removeButtonConstraints.gridx = 3;
			removeButtonConstraints.gridy = 1;
			GridBagConstraints cancelButtonConstraints = new GridBagConstraints();
			cancelButtonConstraints.gridx = 1;
			cancelButtonConstraints.anchor = GridBagConstraints.EAST;
			cancelButtonConstraints.gridy = 1;
			dialogPane = new JPanel();
			dialogPane.setLayout(new GridBagLayout());
			dialogPane.setMinimumSize(DIALOG_PANEL_SIZE);
			dialogPane.setPreferredSize(DIALOG_PANEL_SIZE);
			dialogPane.setMaximumSize(DIALOG_PANEL_SIZE);
			dialogPane.setOpaque(false);
			dialogPane.add(getTopOnePixelSpanner(), pixelSpannerConstraints);
			dialogPane.add(getCancelButton(), cancelButtonConstraints);
			dialogPane.add(getMiddleButtonSpanner(), middleSpannerConstraints);
			dialogPane.add(getRemoveButton(), removeButtonConstraints);
			dialogPane.add(getLeftSpanner(), leftSpannerConstraints);
			dialogPane.add(getRightSpanner(), rightSpannerConstraints);
		}
		return dialogPane;
	}

	private JButton getCancelButton() {
		if (cancelButton == null) {
			cancelButton = new JButton();
			cancelButton.setPreferredSize(BUTTON_SIZE);
			cancelButton.setName("gray");
			cancelButton.setText(getMessages().getMessage("cancel"));
		}
		return cancelButton;
	}

	private JButton getRemoveButton() {
		if (removeButton == null) {
			removeButton = new JButton();
			removeButton.setPreferredSize(BUTTON_SIZE);
			removeButton.setName("gray");
			removeButton.setText(getMessages().getMessage(this.yesButton));
		}
		return removeButton;
	}

	private JPanel getContentPanel() {
		if (contentPanel == null) {
			BorderLayout borderLayout = new BorderLayout();
			borderLayout.setHgap(3);
			borderLayout.setVgap(3);
			contentPanel = new JPanel();
			contentPanel.setOpaque(false);
			contentPanel.setLayout(borderLayout);
			contentPanel.add(getButtonsPanel(), BorderLayout.CENTER);
			contentPanel.add(getDialogPane(), BorderLayout.SOUTH);
		}
		return contentPanel;
	}

	private JPanel getMainPanel() {
		if (mainPanel == null) {
			GridBagConstraints contentPanelgridBagConstraints = new GridBagConstraints();
			contentPanelgridBagConstraints.gridx = 0;
			contentPanelgridBagConstraints.fill = GridBagConstraints.BOTH;
			contentPanelgridBagConstraints.insets = CONTENT_PANEL_INSETS;
			contentPanelgridBagConstraints.weightx = 1.0;
			contentPanelgridBagConstraints.weighty = 1.0;
			contentPanelgridBagConstraints.gridy = 1;
			GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
			gridBagConstraints7.gridx = 0;
			gridBagConstraints7.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints7.weightx = 1.0;
			gridBagConstraints7.gridy = 0;
			mainPanel = new JPanel();
			mainPanel.setName("popupWindowBorder");
			mainPanel.setLayout(new GridBagLayout());
			mainPanel.setMinimumSize(MAIN_PANEL_SIZE);
			mainPanel.setPreferredSize(MAIN_PANEL_SIZE);
			mainPanel.setOpaque(false);
			mainPanel.add(getContentPanel(), contentPanelgridBagConstraints);
		}
		return mainPanel;
	}

	private JPanel getMiddleButtonSpanner() {
		if (middleButtonSpanner == null) {
			middleButtonSpanner = new JPanel();
			middleButtonSpanner.setLayout(new GridBagLayout());
			middleButtonSpanner.setPreferredSize(new Dimension(10, 22));
			middleButtonSpanner.setOpaque(false);
		}
		return middleButtonSpanner;
	}

	private JPanel getTopOnePixelSpanner() {
		if (topOnePixelSpanner == null) {
			topOnePixelSpanner = new JPanel();
			topOnePixelSpanner.setLayout(new GridBagLayout());
			topOnePixelSpanner.setPreferredSize(new Dimension(0, 0));
			topOnePixelSpanner.setMaximumSize(new Dimension(2147483647, 0));
			topOnePixelSpanner.setBackground(new Color(170, 170, 170));
			topOnePixelSpanner.setMinimumSize(new Dimension(0, 0));
		}
		return topOnePixelSpanner;
	}

	private JPanel getLeftSpanner() {
		if (leftSpanner == null) {
			leftSpanner = new JPanel();
			leftSpanner.setLayout(new GridBagLayout());
		}
		return leftSpanner;
	}

	private JPanel getRightSpanner() {
		if (rightSpanner == null) {
			GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
			gridBagConstraints9.gridx = -1;
			gridBagConstraints9.gridy = -1;
			rightSpanner = new JPanel();
			rightSpanner.setLayout(new GridBagLayout());
		}
		return rightSpanner;
	}

	private String title;
	private JDialog dialog = this;
	private int returnValue;
	private JLabel confirmationTitleLabel = null;

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

	public final void yestButtonActionPerformed() {
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
	 * The <code>parent</code> argument determines two things: the frame on which the open dialog depends and the
	 * component whose position the look and feel should consider when placing the dialog. If the parent is a
	 * <code>Frame</code> object (such as a <code>JFrame</code>) then the dialog depends on the frame and the look and
	 * feel positions the dialog relative to the frame (for example, centered over the frame). If the parent is a
	 * component, then the dialog depends on the frame containing the component, and is positioned relative to the
	 * component (for example, centered over the component). If the parent is <code>null</code>, then the dialog depends
	 * on no visible window, and it's placed in a look-and-feel-dependent position such as the center of the screen.
	 * 
	 * @param parent
	 *          the parent component of the dialog; can be <code>null</code>
	 * @param approveButtonText
	 *          the text of the <code>ApproveButton</code>
	 * @return the return state of the file chooser on popdown:
	 *         <ul>
	 *         <li>CANCEL_OPTION <li>APPROVE_OPTION <li>ERROR_OPTION if an error occurs or the dialog is dismissed
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
		return returnValue;
	}

	/**
	 * Creates and returns a new <code>JDialog</code> wrapping <code>this</code> centered on the <code>parent</code> in
	 * the <code>parent</code>'s frame. This method can be overriden to further manipulate the dialog, to disable
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
		JDialog newdialog = this;
		newdialog.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				Dialog resizedDialog = (Dialog) e.getComponent();
				Area shape = new Area(new RoundRectangle2D.Double(0, 0, resizedDialog.getWidth(), resizedDialog.getHeight(),
						15, 15));
				shape.add(new Area(new Rectangle2D.Float(0, 0, resizedDialog.getWidth(), 22)));
				TransparencyManagerFactory.getManager().setWindowShape(resizedDialog, shape);
			}
		});
		Dimension defaultSize = DEFAULT_SIZE;
		newdialog.setSize(defaultSize);
		newdialog.setPreferredSize(defaultSize);
		newdialog.setMinimumSize(defaultSize);
		newdialog.setComponentOrientation(this.getComponentOrientation());
		newdialog.getRootPane().setName("RootPane");
		WindowScalerMouseListener.setWindowScaler(newdialog);
		if (JDialog.isDefaultLookAndFeelDecorated()) {
			boolean supportsWindowDecorations = UIManager.getLookAndFeel().getSupportsWindowDecorations();
			if (supportsWindowDecorations) {
				newdialog.getRootPane().setWindowDecorationStyle(JRootPane.FILE_CHOOSER_DIALOG);
			}
		}
		newdialog.pack();
		return newdialog;
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
