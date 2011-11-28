package com.all.client.view.toolbar.social;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;

import com.all.appControl.control.ViewEngine;
import com.all.client.view.EditingComponent;
import com.all.client.view.util.LabelUtil;
import com.all.core.actions.Actions;
import com.all.core.common.view.util.SpacerKeyListener;
import com.all.core.model.Profile;
import com.all.shared.model.ContactInfo;

public class QuotePanel extends JPanel {

	private static final long serialVersionUID = 1115688251953351050L;

	private static final int MAX_QUOTE_LENGTH = 256;

	private static final Rectangle EDIT_QUOTE_BUTTON_BOUNDS = new Rectangle(127, 7, 14, 14);

	private static final Rectangle EDITING_COMPONENT_BOUNDS = new Rectangle(0, 0, 150, 37);

	private static final Rectangle QUOTE_LABEL_BOUNDS = new Rectangle(0, 0, 150, 37);

	private static final Rectangle QUOTE_PANEL_BOUNDS = new Rectangle(0, 20, 150, 37);

	private static final String EDIT_QUOTE_BUTTON_NAME = "editQuoteProfileButton";

	private static final String QUOTE_LABEL_NAME = "profileQuoteLabel";

	private static final String QUOTE_PANEL_NAME = "profileQuoteBackground";

	private EditingComponent<JLabel, JTextField> editingComponent;

	private Color quoteColor = UIManager.getDefaults().getColor("Color.purple662D91");

	private JButton editQuoteButton;

	private JTextField quoteTextField;

	private JLabel quoteLabel;

	private final ContactInfo contactInfo;

	private final Profile profile;

	private final ViewEngine viewEngine;

	public QuotePanel(Profile profile, ViewEngine viewEngine) {
		this.profile = profile;
		this.viewEngine = viewEngine;
		this.contactInfo = profile.getContact();
		initialize();
		setup();
	}

	private void setup() {
		getQuoteTextField().addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					saveData();
				}
				if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
					getEditingComponent().endEdit();
				}
			}

			@Override
			public void keyTyped(KeyEvent e) {
				if (getQuoteTextField().getText().length() >= MAX_QUOTE_LENGTH
						&& (getQuoteTextField().getSelectedText() == null || getQuoteTextField().getSelectedText().length() == 0)) {
					Toolkit.getDefaultToolkit().beep();
					e.consume();
				}
			}
		});

		getQuoteTextField().addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				saveData();
			}
		});
	}

	private void saveData() {
		getEditQuoteButton().setVisible(false);
		viewEngine.sendValueAction(Actions.UserProfile.UPDATE_QUOTE, getQuoteTextField().getText());
		getEditingComponent().endEdit();
	}

	private void initialize() {
		this.setBounds(QUOTE_PANEL_BOUNDS);
		this.setLayout(null);
		if (profile.isLocal()) {
			this.add(getEditingComponent());
			this.add(getEditQuoteButton());
			this.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					if (e.getClickCount() == 1) {
						getEditingComponent().startEdit();
						getQuoteTextField().selectAll();
						getQuoteTextField().requestFocus();
					}
				}
				
				@Override
				public void mouseEntered(MouseEvent e) {
					setName(QUOTE_PANEL_NAME);
				}
				
				@Override
				public void mouseExited(MouseEvent e) {
					if (!getEditingComponent().isEditing()) {
						setName(null);
					}
				}
			});
		} else {
			this.add(getQuoteLabel());
		}
	}

	private EditingComponent<JLabel, JTextField> getEditingComponent() {
		if (editingComponent == null) {
			editingComponent = new EditingComponent<JLabel, JTextField>(getQuoteLabel(), getQuoteTextField(), null, null);
			editingComponent.setBounds(EDITING_COMPONENT_BOUNDS);
		}
		return editingComponent;
	}

	private JLabel getQuoteLabel() {
		if (quoteLabel == null) {
			quoteLabel = new JLabel();
			quoteLabel.setBounds(QUOTE_LABEL_BOUNDS);
			quoteLabel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
			setTextToQuoteLabel(contactInfo.getMessage());
		}
		return quoteLabel;
	}

	private JTextField getQuoteTextField() {
		if (quoteTextField == null) {
			quoteTextField = new JTextField();
			quoteTextField.setText(contactInfo.getMessage());
			quoteTextField.setForeground(quoteColor);
			quoteTextField.setName(QUOTE_LABEL_NAME);
			quoteTextField.addKeyListener(new SpacerKeyListener());
			quoteTextField.addKeyListener(new KeyAdapter() {
				@Override
				public void keyPressed(KeyEvent e) {
					int keyCode = e.getKeyCode();
					if (keyCode == KeyEvent.VK_ENTER) {
						getEditingComponent().endEdit();
						setTextToQuoteLabel(quoteTextField.getText());
					}
				}
			});
		}
		return quoteTextField;
	}

	private JButton getEditQuoteButton() {
		if (editQuoteButton == null) {
			editQuoteButton = new JButton();
			editQuoteButton.setBounds(EDIT_QUOTE_BUTTON_BOUNDS);
			editQuoteButton.setName(EDIT_QUOTE_BUTTON_NAME);
			editQuoteButton.setVisible(false);
			editQuoteButton.addMouseListener(new EditProfileMouseOverListener(this, getEditQuoteButton()));
		}
		return editQuoteButton;
	}

	private final class EditProfileMouseOverListener extends MouseAdapter {

		private final JPanel panel;

		private final JButton button;

		public EditProfileMouseOverListener(JPanel panel, JButton button) {
			this.panel = panel;
			this.button = button;
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			if (e.getClickCount() == 1) {
				getEditingComponent().startEdit();
				getQuoteTextField().selectAll();
				getQuoteTextField().requestFocus();
			}
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			panel.setName(QUOTE_PANEL_NAME);
			button.setVisible(true);
		}

		@Override
		public void mouseExited(MouseEvent e) {
			panel.setName(null);
			button.setVisible(false);
		}
	}

	public void setTextToQuoteLabel(String text) {
		getQuoteTextField().setText(text);
		String labelText = LabelUtil.splitTextInTwoRows(getQuoteLabel(), text, quoteColor, QUOTE_LABEL_BOUNDS.width);
		getQuoteLabel().setText(labelText);
	}
}
