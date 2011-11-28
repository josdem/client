package com.all.client.view.dialog;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.all.client.model.Picture;
import com.all.client.model.ResizeImageType;
import com.all.client.view.components.SquareTransparencyCropArea;
import com.all.i18n.Messages;

public class EditPhotoDialog extends AllDialog {

	private static final long serialVersionUID = 1L;

	private static final Dimension CONTENT_PANEL_BOUNDS = new Dimension(440, 521);

	private static final Dimension DIALOG_BOUNDS = new Dimension(450, 550);

	private static final Rectangle BOTTOM_SEPARATOR_PANEL_BOUNDS = new Rectangle(5, 482, 430, 2);

	private static final Rectangle CANCEL_BUTTON_BOUNDS = new Rectangle(135, 491, 80, 22);
	
	private static final Rectangle DONE_BUTTON_BOUNDS = new Rectangle(225, 491, 80, 22);
	
	private static final Rectangle INSTRUCTIONS_LABEL_BOUNDS = new Rectangle(9, 440, 421, 31);
	
	private static final Rectangle LABEL_PICTURE_BOUNDS = new Rectangle(1, 1, 419, 419);
	
	private static final Rectangle PICTURE_PANEL_BOUNDS = new Rectangle(10, 10, 420, 420);

	private static final String BOTTOM_SEPARATOR_PANEL_NAME = "bottomPanelSeparator";

	private static final String BUTTON_NAME = "buttonCancel";
	
	private static final String DIALOG_NAME = "NewAccountDialog";
	
	private static final String PICTURE_PANEL_NAME = "cropPanel";
	
	private JButton cancelButton;

	private JButton doneButton;
	
	private JPanel bottomSeparatorPanel;

	private JPanel contentPanel;
	
	private JPanel picturePanel;
	
	private JLabel instructionsLabel;
	
	private boolean isDone = false;

	private final Picture pic;
	
	private SquareTransparencyCropArea squareTransparencyCropArea;

	public EditPhotoDialog(JFrame frame, Messages messages, Picture pic) {
		super(frame, messages);
		this.pic = pic;
		setName(DIALOG_NAME);
		setSize(DIALOG_BOUNDS);
		initializeContentPane();
		pack();
		setVisible(true);
	}

	@Override
	String dialogTitle(Messages messages) {
		return messages.getMessage("editPhoto.dialog.title");
	}

	@Override
	JComponent getContentComponent() {
		return getContentPanel();
	}
	
	@Override
	void internationalizeDialog(Messages messages) {
		instructionsLabel.setText(messages.getMessage("editPhoto.message"));
		doneButton.setText(messages.getMessage("editPhoto.done"));
		cancelButton.setText(messages.getMessage("editPhoto.cancel"));
	}

	private JPanel getContentPanel() {
		if (contentPanel == null) {
			contentPanel = new JPanel();
			contentPanel.setLayout(null);
			contentPanel.setMinimumSize(CONTENT_PANEL_BOUNDS);
			contentPanel.setMaximumSize(CONTENT_PANEL_BOUNDS);
			contentPanel.setPreferredSize(CONTENT_PANEL_BOUNDS);

			contentPanel.add(getPicturePanel());
			contentPanel.add(getInstructionsLabel());
			contentPanel.add(getSeparatorPanel());
			contentPanel.add(getCancelButton());
			contentPanel.add(getDoneButton());

		}
		return contentPanel;
	}

	private JPanel getPicturePanel() {
		if (picturePanel == null) {
			picturePanel = new JPanel();
			picturePanel.setBounds(PICTURE_PANEL_BOUNDS);
			picturePanel.setName(PICTURE_PANEL_NAME);
			picturePanel.setLayout(null);
			JLabel labelPicture = new JLabel();
			labelPicture.setBounds(LABEL_PICTURE_BOUNDS);
			labelPicture.setIcon(new ImageIcon(pic.getScaledImageLongestSide(ResizeImageType.editPhotoDialog)));
			picturePanel.add(labelPicture);
			squareTransparencyCropArea = new SquareTransparencyCropArea(pic.getBounds());
			squareTransparencyCropArea.setBounds(pic.getBounds().x+1,pic.getBounds().y+1, pic.getBounds().width, pic.getBounds().height);
			picturePanel.add(squareTransparencyCropArea);
			picturePanel.setComponentZOrder(squareTransparencyCropArea, 0);
		}
		return picturePanel;
	}
	
	private JLabel getInstructionsLabel() {
		if (instructionsLabel == null) {
			instructionsLabel = new JLabel();
			instructionsLabel.setBounds(INSTRUCTIONS_LABEL_BOUNDS);
			instructionsLabel.setHorizontalAlignment(JLabel.CENTER);
		}
		return instructionsLabel;
	}


	private JPanel getSeparatorPanel() {
		if (bottomSeparatorPanel == null) {
			bottomSeparatorPanel = new JPanel();
			bottomSeparatorPanel.setName(BOTTOM_SEPARATOR_PANEL_NAME);
			bottomSeparatorPanel.setBounds(BOTTOM_SEPARATOR_PANEL_BOUNDS);
		}
		return bottomSeparatorPanel;
	}

	/**
	 * @return
	 */
	private JButton getCancelButton() {
		if (cancelButton == null) {
			cancelButton = new JButton();
			cancelButton.setName(BUTTON_NAME);
			cancelButton.setBounds(CANCEL_BUTTON_BOUNDS);
			 cancelButton.addActionListener(new CloseListener());
		}
		return cancelButton;
	}
	
	private JButton getDoneButton() {
		if (doneButton == null) {
			doneButton = new JButton();
			doneButton.setName(BUTTON_NAME);
			doneButton.setBounds(DONE_BUTTON_BOUNDS);
			doneButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					isDone = true;
					dispose();
				}
			});
		}
		return doneButton;
	}
	
	
	public Image croppedImage(ResizeImageType resizeImageType) {
		if(isDone) {
			Rectangle cropArea = squareTransparencyCropArea.getCropArea();
			return pic.crop(cropArea, resizeImageType);
		}
		return null;
	}

}
