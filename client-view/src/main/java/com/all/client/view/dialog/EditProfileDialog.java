package com.all.client.view.dialog;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagLayout;
import java.awt.Rectangle;
import java.awt.dnd.DropTarget;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Set;

import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.WordUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.Assert;

import com.all.action.ResponseCallback;
import com.all.appControl.control.ViewEngine;
import com.all.client.model.ResizeImageType;
import com.all.client.view.components.ImagePanel;
import com.all.client.view.dnd.ImageDropListener;
import com.all.client.view.dnd.MainFrameDragOverListener;
import com.all.client.view.dnd.MultiLayerDropTargetListener;
import com.all.core.common.bean.UpdateUserCommand;
import com.all.core.common.model.ApplicationActions;
import com.all.core.common.util.ImageUtil;
import com.all.core.common.util.SequenceUtil;
import com.all.core.common.view.SynthFonts;
import com.all.core.common.view.util.CopyPasteKeyAdapterForMac;
import com.all.core.common.view.util.FocusOrderPolicy;
import com.all.core.common.view.util.SelectedTextForeground;
import com.all.core.model.Model;
import com.all.i18n.Messages;
import com.all.observ.ObservValue;
import com.all.observ.Observer;
import com.all.shared.model.City;
import com.all.shared.model.Gender;
import com.all.shared.model.User;

public final class EditProfileDialog extends AllDialog {

	private static final double ARC_HEIGHT = .17;

	private static final double ARC_WIDTH = .17;

	private static final long serialVersionUID = 1L;

	private static final Rectangle BIRTH_LABEL_BOUNDS = new Rectangle(7, 316, 98, 13);

	private static final Dimension CONTENT_PANEL_DEFAULT_SIZE = new Dimension(350, 431);

	private static final Rectangle CANCEL_BUTTON_BOUNDS = new Rectangle(90, 398, 80, 22);

	private static final Rectangle DAY_COMBOBOX_BOUNDS = new Rectangle(215, 313, 50, 22);

	private static final Rectangle DROP_ME_HERE_PANEL_BOUNDS = new Rectangle(4, 24, 88, 48);

	private static final Rectangle EMAIL_LABEL_BOUNDS = new Rectangle(124, 125, 204, 15);

	private static final Rectangle FEMALE_RADIOBUTTON_BOUNDS = new Rectangle(118, 277, 12, 30);

	private static final Rectangle FEMALE_TOGGLE_BUTTON_BOUNDS = new Rectangle(134, 277, 14, 30);

	private static final Rectangle GENDER_LABEL_BOUNDS = new Rectangle(7, 285, 98, 13);

	private static final Rectangle ID_LABEL_BOUNDS = new Rectangle(124, 100, 204, 13);

	private static final Rectangle IMAGE_PANEL_BOUNDS = new Rectangle(21, 70, 96, 96);

	private static final Rectangle LASTNAME_LABEL_BOUNDS = new Rectangle(7, 253, 98, 13);

	private static final Rectangle LASTNAME_TEXTFIELD_BOUNDS = new Rectangle(118, 248, 210, 22);

	private static final Rectangle LOCATION_COMBOBOX_BOUNDS = new Rectangle(118, 347, 210, 22);

	private static final Rectangle LOCATION_LABEL_BOUNDS = new Rectangle(7, 351, 98, 13);

	private static final Rectangle MALE_RADIOBUTTON_BOUNDS = new Rectangle(152, 277, 12, 30);

	private static final Rectangle MALE_TOGGLE_BUTTON_BOUNDS = new Rectangle(168, 277, 14, 30);

	private static final Rectangle MONTH_COMBOBOX_BOUNDS = new Rectangle(118, 313, 92, 22);

	private static final Rectangle NAME_LABEL_BOUNDS = new Rectangle(6, 219, 98, 13);

	private static final Rectangle NAME_TEXTFIELD_BOUNDS = new Rectangle(118, 214, 210, 22);

	private static final Rectangle NICKNAME_LABEL_BOUNDS = new Rectangle(6, 185, 98, 13);

	private static final Rectangle NICKNAME_TEXTFIELD_BOUNDS = new Rectangle(118, 180, 210, 22);

	private static final Rectangle PROFILE_PICTURE_MASK_BOUNDS = new Rectangle(0, 0, 96, 96);

	private static final Rectangle SAVE_BUTTON_BOUNDS = new Rectangle(180, 398, 80, 22);

	private static final Rectangle SEPARATOR_PANEL_BOUNDS = new Rectangle(5, 386, 340, 2);

	private static final Rectangle YEAR_COMBOBOX_BOUNDS = new Rectangle(270, 313, 58, 22);

	private static final Rectangle ZIP_CODE_LABEL_BOUNDS = new Rectangle(196, 246, 56, 30);

	private static final Rectangle ZIP_CODE_TEXTFIELD_BOUNDS = new Rectangle(258, 250, 70, 22);

	private static final String CANCEL_BUTTON_NAME = "buttonCancel";

	private static final String CONTENT_PANEL_NAME = "newUserAccountPanel";

	private static final String DAY_COMBOBOX_NAME = "comboBoxDefaultDay";

	private static final String DROP_ME_HERE_PANEL_NAME = "roundedBlackTransparentBackgroundLabel";

	private static final String FEMALE_TOGGLE_BUTTON_FOCUS_NAME = "femaleToggleButtonFocus";

	private static final String FEMALE_TOGGLE_BUTTON_NAME = "femaleToggleButton";

	private static final String LASTNAME_TEXTFIELD_NAME = "textFieldLastName";

	private static final String LOCATION_COMBOBOX_NAME = "comboBoxDefaultCity";

	private static final String MALE_TOGGLE_BUTTON_FOCUS_NAME = "maleToggleButtonFocus";

	private static final String MALE_TOGGLE_BUTTON_NAME = "maleToggleButton";

	private static final String MONTH_COMBOBOX_NAME = "comboBoxDefaultMonth";

	private static final String NAME_TEXTFIELD_NAME = "textFieldName";

	private static final String NICKNAME_TEXTFIELD_NAME = "textFieldNickname";

	private static final String PROFILE_PICTURE_MASK_NAME = "editProfilePictureMask";

	private static final String RADIO_BUTTON_NAME = "radioButton";

	private static final String SAVE_BUTTON_CANCEL = "buttonSave";

	private static final String SEPARATOR_PANEL_NAME = "bottomPanelSeparator";

	private static final String YEAR_COMBOBOX_NAME = "comboBoxDefaultYear";

	private static final String ZIP_CODE_TEXTFIELD_NAME = "textFieldZipCode";

	private boolean saved;

	private boolean changeImage;

	private ButtonGroup radioButtonGroup = null;

	private final DialogFactory dialogFactory;

	private JButton cancelButton = null;

	private JButton saveButton = null;

	private JComboBox dayComboBox = null;

	private JComboBox locationComboBox = null;

	private JComboBox monthComboBox = null;

	private JComboBox yearComboBox = null;

	private ImagePanel imagePanel = null;

	private JLabel birthLabel = null;

	private JLabel dropMeHereLabel;

	private JLabel emailLabel = null;

	private JLabel genderLabel = null;

	private JLabel idLabel = null;

	private JLabel lastNameLabel = null;

	private JLabel locationLabel = null;

	private JLabel nameLabel = null;

	private JLabel nickNameLabel = null;

	private JLabel zipCodeLabel;

	private JRadioButton femaleRadioButton = null;

	private JRadioButton maleRadioButton = null;

	private JTextField lastNameTextField = null;

	private JTextField nameTextField = null;

	private JTextField nickNameTextField = null;

	private JTextField zipCodeTextField;

	private JToggleButton maleToggleButton = null;

	private JToggleButton femaleToggleButton = null;

	private JPanel contentPanel = null;

	private JPanel profilePictureMask;

	private JPanel separatorPanel = null;

	private Log log = LogFactory.getLog(getClass());

	private String[] birthMonthValues;

	private UpdateUserCommand userCommand;

	private final Validator validator;

	private final ViewEngine viewEngine;

	private boolean isLocationLoaded = false;

	public EditProfileDialog(Frame parentFrame, Messages messages, Validator validator,
			MultiLayerDropTargetListener multiLayerDropTargetListener, DialogFactory dialogFactory, ViewEngine viewEngine) {
		super(parentFrame, messages);
		this.validator = validator;
		this.dialogFactory = dialogFactory;
		this.viewEngine = viewEngine;
		this.saved = false;
		this.changeImage = false;
		User user = viewEngine.get(Model.CURRENT_USER);
		this.userCommand = new UpdateUserCommand(user);
		getExitButton().addActionListener(cancelChanges());
		initializeMonths(messages);
		initializeContentPane();
		setDragAndDrop(messages, multiLayerDropTargetListener);
		pack();
		populate(user);
		setVisible(true);
	}

	// TODO: autowire this method, it shouldnt have to receive the factory
	private void setDragAndDrop(Messages messages, MultiLayerDropTargetListener multiLayerDropTargetListener) {
		setDropTarget(new DropTarget(this, multiLayerDropTargetListener));
		multiLayerDropTargetListener.addDragListener(this, new MainFrameDragOverListener(this, messages));
		ImageDropListener listener = new ImageDropListener(imagePanel, dialogFactory, ResizeImageType.editPhotoDialog);
		listener.onDropped().add(new Observer<ObservValue<ImagePanel>>() {
			@Override
			public void observe(ObservValue<ImagePanel> t) {
				validateAllUserData();
				changeImage = true;
			}
		});
		multiLayerDropTargetListener.addDropListener(imagePanel, listener);
	}

	private void initializeMonths(Messages messages) {
		birthMonthValues = new String[] { messages.getMessage("editContact.birthLabel.january"),
				messages.getMessage("editContact.birthLabel.february"), messages.getMessage("editContact.birthLabel.march"),
				messages.getMessage("editContact.birthLabel.april"), messages.getMessage("editContact.birthLabel.may"),
				messages.getMessage("editContact.birthLabel.june"), messages.getMessage("editContact.birthLabel.july"),
				messages.getMessage("editContact.birthLabel.august"), messages.getMessage("editContact.birthLabel.september"),
				messages.getMessage("editContact.birthLabel.october"), messages.getMessage("editContact.birthLabel.november"),
				messages.getMessage("editContact.birthLabel.december") };
	}

	private void populate(User user) {
		if (user != null) {
			this.emailLabel.setText(user.getEmail());
			this.getNameTextField().setText(WordUtils.capitalize(user.getFirstName()));
			this.getLastNameTextField().setText(WordUtils.capitalize(user.getLastName()));
			this.getNickNameTextField().setText(user.getNickName());
			Gender gender = user.getGender();
			this.getFemaleRadioButton().setSelected(gender == Gender.FEMALE);
			this.getFemaleToggleButton().setSelected(gender == Gender.FEMALE);
			this.getMaleRadioButton().setSelected(gender == Gender.MALE);
			this.getMaleToggleButton().setSelected(gender == Gender.MALE);
			this.getZipCodeTextField().setText(user.getZipCode());
			Calendar calendar = GregorianCalendar.getInstance();
			calendar.setTime(user.getBirthday());
			this.getMonthComboBox().setSelectedIndex(calendar.get(Calendar.MONTH));
			this.getDayComboBox().setSelectedItem("" + calendar.get(Calendar.DATE));
			this.getYearComboBox().setSelectedItem("" + calendar.get(Calendar.YEAR));
			getSaveButton().setEnabled(false);
			this.imagePanel.setImage(ImageUtil.getImage(user.getAvatar()), ARC_WIDTH, ARC_HEIGHT);
		}
	}

	@Override
	String dialogTitle(Messages messages) {
		return messages.getMessage("editContact.title");
	}

	@Override
	JComponent getContentComponent() {
		return getContentPanel();
	}

	@Override
	void internationalizeDialog(Messages messages) {
		getIdLabel().setText(getMessages().getMessage("editContact.userId"));
		getNameLabel().setText(messages.getMessage("editContact.name"));
		getCancelButton().setText(messages.getMessage("editContact.cancel"));
		getSaveButton().setText(messages.getMessage("editContact.save"));
		getLocationLabel().setText(messages.getMessage("editContact.location"));
		getBirthLabel().setText(messages.getMessage("editContact.birth"));
		getNickNameLabel().setText(messages.getMessage("editContact.nickname"));
		getZipCodeLabel().setText(messages.getMessage("editContact.zipCodeLabel"));
		getLastNameLabel().setText(messages.getMessage("editContact.lastName"));
		getGenderLabel().setText(messages.getMessage("editContact.gender"));
		getDropMeHereLabel().setText(messages.getMessage("editContact.portrait"));
	}

	private JPanel getContentPanel() {
		if (contentPanel == null) {
			contentPanel = new JPanel();
			contentPanel.setLayout(null);
			contentPanel.setName(CONTENT_PANEL_NAME);
			contentPanel.setPreferredSize(CONTENT_PANEL_DEFAULT_SIZE);
			contentPanel.add(getIdLabel());
			contentPanel.add(getEmailLabel());
			contentPanel.add(getImagePanel());
			contentPanel.add(getNickNameLabel());
			contentPanel.add(getNickNameTextField());
			contentPanel.add(getNameLabel());
			contentPanel.add(getNameTextField());
			contentPanel.add(getLastNameLabel());
			contentPanel.add(getLastNameTextField());
			contentPanel.add(getGenderLabel());
			contentPanel.add(getMaleRadioButton());
			contentPanel.add(getFemaleRadioButton());
			contentPanel.add(getMaleToggleButton());
			contentPanel.add(getFemaleToggleButton());
			contentPanel.add(getZipCodeLabel());
			contentPanel.add(getZipCodeTextField());
			contentPanel.add(getLocationLabel());
			contentPanel.add(getLocationComboBox());
			contentPanel.add(getBirthLabel());
			contentPanel.add(getMonthComboBox());
			contentPanel.add(getDayComboBox());
			contentPanel.add(getYearComboBox());
			contentPanel.add(getSeparatorPanel());
			contentPanel.add(getCancelButton());
			contentPanel.add(getSaveButton());
			setFocusOrder();
			initializeButtonGroup();
		}
		return contentPanel;
	}

	private void initializeButtonGroup() {
		radioButtonGroup = new ButtonGroup();
		radioButtonGroup.add(getFemaleRadioButton());
		radioButtonGroup.add(getMaleRadioButton());
	}

	private JLabel getIdLabel() {
		if (idLabel == null) {
			idLabel = new JLabel();
			idLabel.setBounds(ID_LABEL_BOUNDS);
			idLabel.setName(SynthFonts.BOLD_FONT12_GRAY77_77_77);
		}
		return idLabel;
	}

	private JLabel getEmailLabel() {
		if (emailLabel == null) {
			emailLabel = new JLabel();
			emailLabel.setBounds(EMAIL_LABEL_BOUNDS);
			emailLabel.setName(SynthFonts.BOLD_ITALIC_FONT12_PURPLE32_0f_32);
		}
		return emailLabel;
	}

	private ImagePanel getImagePanel() {
		if (imagePanel == null) {
			imagePanel = new ImagePanel();
			imagePanel.setLayout(null);
			imagePanel.setBounds(IMAGE_PANEL_BOUNDS);
			imagePanel.add(getProfilePictureMask());
			imagePanel.add(getDropMeHereLabel());
		}
		return imagePanel;
	}

	private JLabel getDropMeHereLabel() {
		if (dropMeHereLabel == null) {
			dropMeHereLabel = new JLabel();
			dropMeHereLabel.setBounds(DROP_ME_HERE_PANEL_BOUNDS);
			dropMeHereLabel.setName(DROP_ME_HERE_PANEL_NAME);
			dropMeHereLabel.setHorizontalAlignment(JLabel.CENTER);
			dropMeHereLabel.setVerticalAlignment(JLabel.CENTER);
		}
		return dropMeHereLabel;
	}

	private JLabel getGenderLabel() {
		if (genderLabel == null) {
			genderLabel = new JLabel();
			genderLabel.setBounds(GENDER_LABEL_BOUNDS);
			genderLabel.setName(SynthFonts.BOLD_FONT12_GRAY77_77_77);
			genderLabel.setHorizontalAlignment(SwingConstants.RIGHT);
			genderLabel.setHorizontalTextPosition(SwingConstants.RIGHT);
		}
		return genderLabel;
	}

	private JLabel getLastNameLabel() {
		if (lastNameLabel == null) {
			lastNameLabel = new JLabel();
			lastNameLabel.setBounds(LASTNAME_LABEL_BOUNDS);
			lastNameLabel.setName(SynthFonts.BOLD_FONT12_GRAY77_77_77);
			lastNameLabel.setHorizontalAlignment(SwingConstants.RIGHT);
			lastNameLabel.setHorizontalTextPosition(SwingConstants.RIGHT);
		}
		return lastNameLabel;
	}

	private JLabel getNameLabel() {
		if (nameLabel == null) {
			nameLabel = new JLabel();
			nameLabel.setBounds(NAME_LABEL_BOUNDS);
			nameLabel.setName(SynthFonts.BOLD_FONT12_GRAY77_77_77);
			nameLabel.setHorizontalAlignment(SwingConstants.RIGHT);
			nameLabel.setHorizontalTextPosition(SwingConstants.RIGHT);
		}
		return nameLabel;
	}

	private JLabel getNickNameLabel() {
		if (nickNameLabel == null) {
			nickNameLabel = new JLabel();
			nickNameLabel.setBounds(NICKNAME_LABEL_BOUNDS);
			nickNameLabel.setName(SynthFonts.BOLD_FONT12_GRAY77_77_77);
			nickNameLabel.setHorizontalAlignment(SwingConstants.RIGHT);
			nickNameLabel.setHorizontalTextPosition(SwingConstants.RIGHT);
		}
		return nickNameLabel;
	}

	private JLabel getZipCodeLabel() {
		if (zipCodeLabel == null) {
			zipCodeLabel = new JLabel();
			zipCodeLabel.setBounds(ZIP_CODE_LABEL_BOUNDS);
			zipCodeLabel.setHorizontalAlignment(SwingConstants.RIGHT);
			zipCodeLabel.setName(SynthFonts.BOLD_FONT12_GRAY90_90_90);
		}
		return zipCodeLabel;
	}

	private JLabel getLocationLabel() {
		if (locationLabel == null) {
			locationLabel = new JLabel();
			locationLabel.setBounds(LOCATION_LABEL_BOUNDS);
			locationLabel.setName(SynthFonts.BOLD_FONT12_GRAY77_77_77);
			locationLabel.setHorizontalAlignment(SwingConstants.RIGHT);
			locationLabel.setHorizontalTextPosition(SwingConstants.RIGHT);
		}
		return locationLabel;
	}

	private JLabel getBirthLabel() {
		if (birthLabel == null) {
			birthLabel = new JLabel();
			birthLabel.setBounds(BIRTH_LABEL_BOUNDS);
			birthLabel.setName(SynthFonts.BOLD_FONT12_GRAY77_77_77);
			birthLabel.setHorizontalAlignment(SwingConstants.RIGHT);
			birthLabel.setHorizontalTextPosition(SwingConstants.RIGHT);
		}
		return birthLabel;
	}

	private void setFocusOrder() {
		List<Component> focusList = getFocusList();
		setFocusCycleRoot(true);
		setFocusTraversalPolicy(new FocusOrderPolicy(focusList));
	}

	private List<Component> getFocusList() {
		List<Component> components = new ArrayList<Component>();
		components.add(getNickNameTextField());
		components.add(getNameTextField());
		components.add(getLastNameTextField());
		components.add(getFemaleToggleButton());
		components.add(getMaleToggleButton());
		components.add(getMonthComboBox());
		components.add(getDayComboBox());
		components.add(getYearComboBox());
		components.add(getLocationComboBox());
		components.add(getSaveButton());
		return components;
	}

	private JPanel getProfilePictureMask() {
		if (profilePictureMask == null) {
			profilePictureMask = new JPanel();
			profilePictureMask.setLayout(null);
			profilePictureMask.setBounds(PROFILE_PICTURE_MASK_BOUNDS);
			profilePictureMask.setName(PROFILE_PICTURE_MASK_NAME);
		}
		return profilePictureMask;
	}

	private JTextField getNickNameTextField() {
		if (nickNameTextField == null) {
			nickNameTextField = new JTextField();
			nickNameTextField.setBounds(NICKNAME_TEXTFIELD_BOUNDS);
			nickNameTextField.setSelectionColor(SelectedTextForeground.SELECTED_FOREGROUND_COLOR);
			nickNameTextField.setName(NICKNAME_TEXTFIELD_NAME);
			nickNameTextField.addKeyListener(new FocusListenerWithCapitalize(nickNameTextField, "nickName", false));
			nickNameTextField.addFocusListener(new NickNameFocusListenerValidator(nickNameTextField, "nickName"));
			nickNameTextField.addKeyListener(new CopyPasteKeyAdapterForMac());
		}
		return nickNameTextField;
	}

	private JTextField getNameTextField() {
		if (nameTextField == null) {
			nameTextField = new JTextField();
			nameTextField.setBounds(NAME_TEXTFIELD_BOUNDS);
			nameTextField.setSelectionColor(SelectedTextForeground.SELECTED_FOREGROUND_COLOR);
			nameTextField.setName(NAME_TEXTFIELD_NAME);
			nameTextField.addKeyListener(new FocusListenerWithCapitalize(nameTextField, "firstName", true));
			nameTextField.addKeyListener(new CopyPasteKeyAdapterForMac());
		}
		return nameTextField;
	}

	private JTextField getLastNameTextField() {
		if (lastNameTextField == null) {
			lastNameTextField = new JTextField();
			lastNameTextField.setBounds(LASTNAME_TEXTFIELD_BOUNDS);
			lastNameTextField.setSelectionColor(SelectedTextForeground.SELECTED_FOREGROUND_COLOR);
			lastNameTextField.setName(LASTNAME_TEXTFIELD_NAME);
			lastNameTextField.addKeyListener(new FocusListenerWithCapitalize(lastNameTextField, "lastName", true));
			lastNameTextField.addKeyListener(new CopyPasteKeyAdapterForMac());
		}
		return lastNameTextField;
	}

	private JRadioButton getFemaleRadioButton() {
		if (femaleRadioButton == null) {
			femaleRadioButton = new JRadioButton();
			femaleRadioButton.setName(RADIO_BUTTON_NAME);
			femaleRadioButton.setBounds(FEMALE_RADIOBUTTON_BOUNDS);
			femaleRadioButton.addActionListener(new SexListener(Gender.FEMALE));
		}
		return femaleRadioButton;
	}

	private JToggleButton getFemaleToggleButton() {
		if (femaleToggleButton == null) {
			femaleToggleButton = new JToggleButton();
			femaleToggleButton.setBounds(FEMALE_TOGGLE_BUTTON_BOUNDS);
			femaleToggleButton.setName(FEMALE_TOGGLE_BUTTON_NAME);
			femaleToggleButton.addActionListener(new SexListener(Gender.FEMALE));
		}
		femaleToggleButton.addFocusListener(new FocusListener() {

			@Override
			public void focusLost(FocusEvent e) {
				femaleToggleButton.setName(FEMALE_TOGGLE_BUTTON_NAME);
			}

			@Override
			public void focusGained(FocusEvent e) {
				femaleToggleButton.setName(FEMALE_TOGGLE_BUTTON_FOCUS_NAME);

			}
		});
		return femaleToggleButton;
	}

	private JRadioButton getMaleRadioButton() {
		if (maleRadioButton == null) {
			maleRadioButton = new JRadioButton();
			maleRadioButton.setName(RADIO_BUTTON_NAME);
			maleRadioButton.setBounds(MALE_RADIOBUTTON_BOUNDS);
			maleRadioButton.addActionListener(new SexListener(Gender.MALE));
		}
		return maleRadioButton;
	}

	private JToggleButton getMaleToggleButton() {
		if (maleToggleButton == null) {
			maleToggleButton = new JToggleButton();
			maleToggleButton.setBounds(MALE_TOGGLE_BUTTON_BOUNDS);
			maleToggleButton.setName(MALE_TOGGLE_BUTTON_NAME);
			maleToggleButton.addActionListener(new SexListener(Gender.MALE));
			maleToggleButton.addFocusListener(new FocusListener() {

				@Override
				public void focusLost(FocusEvent e) {
					maleToggleButton.setName(MALE_TOGGLE_BUTTON_NAME);
				}

				@Override
				public void focusGained(FocusEvent e) {
					maleToggleButton.setName(MALE_TOGGLE_BUTTON_FOCUS_NAME);

				}
			});
		}
		return maleToggleButton;
	}

	private JTextField getZipCodeTextField() {
		if (zipCodeTextField == null) {
			zipCodeTextField = new JTextField();
			zipCodeTextField.setBounds(ZIP_CODE_TEXTFIELD_BOUNDS);
			zipCodeTextField.setSelectionColor(SelectedTextForeground.SELECTED_FOREGROUND_COLOR);
			zipCodeTextField.setName(ZIP_CODE_TEXTFIELD_NAME);
			zipCodeTextField.addKeyListener(new CopyPasteKeyAdapterForMac());
			// TODO: fix when NewAccountFormPanel's ZipCodeTextField is fixed.
			// zipCodeTextField.addKeyListener(new
			// KeyValidator(zipCodeTextField, "zipCode", true));

		}
		return zipCodeTextField;
	}

	private JComboBox getLocationComboBox() {
		if (locationComboBox == null) {
			viewEngine.request(ApplicationActions.GET_ALL_CITIES, new ResponseCallback<List<City>>() {
				@Override
				public void onResponse(List<City> locations) {
					Collections.sort(locations);
					locationComboBox.setModel(new DefaultComboBoxModel(locations.toArray()));
					locationComboBox.addActionListener(new LocationListener());
					User user = viewEngine.get(Model.CURRENT_USER);
					locationComboBox.setSelectedItem(user.getCity());
					isLocationLoaded = true;
					locationComboBox.setEnabled(true);
				}
			});
			locationComboBox = new JComboBox(new String[] { "Loading..." });
			locationComboBox.setBounds(LOCATION_COMBOBOX_BOUNDS);
			locationComboBox.setName(LOCATION_COMBOBOX_NAME);
			locationComboBox.setEnabled(false);
		}
		return locationComboBox;
	}

	private JComboBox getMonthComboBox() {
		if (monthComboBox == null) {
			monthComboBox = new JComboBox(birthMonthValues);
			monthComboBox.setBounds(MONTH_COMBOBOX_BOUNDS);
			monthComboBox.setName(MONTH_COMBOBOX_NAME);
			monthComboBox.addActionListener(new DateListener());
		}
		return monthComboBox;
	}

	private JComboBox getDayComboBox() {
		if (dayComboBox == null) {
			String[] birthDayValues = SequenceUtil.createSequence(null, 1, 31);
			dayComboBox = new JComboBox(birthDayValues);
			dayComboBox.setBounds(DAY_COMBOBOX_BOUNDS);
			dayComboBox.setName(DAY_COMBOBOX_NAME);
			dayComboBox.addActionListener(new DateListener());
		}
		return dayComboBox;
	}

	private JComboBox getYearComboBox() {
		if (yearComboBox == null) {
			int currentYear = GregorianCalendar.getInstance().get(GregorianCalendar.YEAR);
			String[] birthYearValues = SequenceUtil.createSequence(null, 1930, currentYear, true);
			yearComboBox = new JComboBox(birthYearValues);
			yearComboBox.setBounds(YEAR_COMBOBOX_BOUNDS);
			yearComboBox.setName(YEAR_COMBOBOX_NAME);
			yearComboBox.addActionListener(new DateListener());
		}
		return yearComboBox;
	}

	private JPanel getSeparatorPanel() {
		if (separatorPanel == null) {
			separatorPanel = new JPanel();
			separatorPanel.setLayout(new GridBagLayout());
			separatorPanel.setBounds(SEPARATOR_PANEL_BOUNDS);
			separatorPanel.setName(SEPARATOR_PANEL_NAME);
		}
		return separatorPanel;
	}

	private JButton getCancelButton() {
		if (cancelButton == null) {
			cancelButton = new JButton();
			cancelButton.setBounds(CANCEL_BUTTON_BOUNDS);
			cancelButton.setName(CANCEL_BUTTON_NAME);
			cancelButton.addActionListener(new CancelListener());
			cancelButton.addKeyListener(new KeyAdapter() {
				@Override
				public void keyPressed(KeyEvent keyEvent) {
					if (keyEvent.getKeyCode() == KeyEvent.VK_RIGHT) {
						saveButton.requestFocus();
					}
				}
			});
		}
		return cancelButton;
	}

	private JButton getSaveButton() {
		if (saveButton == null) {
			saveButton = new JButton();
			saveButton.setBounds(SAVE_BUTTON_BOUNDS);
			saveButton.setName(SAVE_BUTTON_CANCEL);
			saveButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					validateAllUserData();
					saved = true;
				}
			});
			saveButton.addActionListener(new CloseListener());
			saveButton.addKeyListener(new KeyAdapter() {
				@Override
				public void keyPressed(KeyEvent keyEvent) {
					if (keyEvent.getKeyCode() == KeyEvent.VK_LEFT) {
						cancelButton.requestFocus();
					}
				}
			});
		}
		return saveButton;
	}

	public boolean isNewImage() {
		return changeImage;
	}

	public UpdateUserCommand getUserCommand() {
		return saved ? this.userCommand : null;
	}

	// TODO refactor these methods copied from new account dialog, DUPLICATED
	// CODE
	// copied from here up to the bottom
	private void capitalizeText(JTextField field) {
		field.setText(WordUtils.capitalizeFully(field.getText()));
	}

	private Set<ConstraintViolation<UpdateUserCommand>> invokeMethod(String propertyName, Object param) {
		try {
			BeanUtils.setProperty(userCommand, propertyName, param);
		} catch (IllegalAccessException e1) {
			log.error(e1, e1);
		} catch (InvocationTargetException e1) {
			log.error(e1, e1);
		}
		return validator.validateProperty(userCommand, propertyName);
	}

	private void showViolationMessage(JComponent field, String propertyName,
			Set<ConstraintViolation<UpdateUserCommand>> violations) {
		field.setName("invalidTextField" + propertyName);
		StringBuffer sb = new StringBuffer();
		for (ConstraintViolation<UpdateUserCommand> violation : violations) {
			sb.append(violation.getMessage());
		}
		field.setToolTipText(sb.toString());
	}

	private void settingsForRightText(JComponent field, String originalName) {
		field.setToolTipText(null);
		field.setName(originalName);
	}

	private void validateAllUserData() {
		// TODO Add ZipcodeTextField to validator also do the same in
		// NewAccountFormPanel
		userCommand.setZipCode(zipCodeTextField.getText());
		Assert.notNull(imagePanel.getImage());
		if (changeImage) {
			userCommand.setAvatar(imagePanel.getImage());
		}
		Set<ConstraintViolation<UpdateUserCommand>> userViolations = validator.validate(userCommand);
		getSaveButton().setEnabled(userViolations.isEmpty());
	}

	private ActionListener cancelChanges() {
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				saved = false;
				changeImage = false;
				userCommand = null;
			}
		};
	}

	// TODO refactor these listeners with the new accoutn dialog, DUPLICATED
	// CODE
	class FocusListenerWithCapitalize extends KeyAdapter {
		private String propertyName;
		private JTextField field;
		private String originalName;
		private final boolean capitalize;

		public FocusListenerWithCapitalize(JTextField component, String propertyName, boolean capitalize) {
			this.propertyName = propertyName;
			this.capitalize = capitalize;
			field = component;
			originalName = field.getName();
		}

		@Override
		public void keyReleased(KeyEvent e) {
			if (capitalize) {
				capitalizeText(field);
			}
			Set<ConstraintViolation<UpdateUserCommand>> violations = invokeMethod(propertyName, field.getText());

			if (violations.isEmpty()) {
				settingsForRightText(field, originalName);
			} else {
				showViolationMessage(field, propertyName, violations);
			}
			validateAllUserData();
		}
	}

	class NickNameFocusListenerValidator extends FocusAdapter {

		private JTextField field;
		private String propertyName;
		private String originalName;

		public NickNameFocusListenerValidator(JTextField field, String propertyName) {
			super();
			this.propertyName = propertyName;
			this.field = field;
			this.originalName = field.getName();
		}

		@Override
		public void focusLost(FocusEvent e) {
			updateView();
		}

		@Override
		public void focusGained(FocusEvent e) {
			updateView();
		}

		private void updateView() {
			Set<ConstraintViolation<UpdateUserCommand>> violations;
			violations = invokeMethod(propertyName, field.getText());
			if (violations.size() > 0) {
				showViolationMessage(field, propertyName, violations);
			} else {
				settingsForRightText(field, originalName);
			}
		}
	}

	void enableAndDisableButtons(Gender gender) {
		getFemaleRadioButton().setSelected(Gender.FEMALE.equals(gender));
		getFemaleToggleButton().setSelected(Gender.FEMALE.equals(gender));
		getMaleRadioButton().setSelected(Gender.MALE.equals(gender));
		getMaleToggleButton().setSelected(Gender.MALE.equals(gender));
		userCommand.setGender(gender);
	}

	class SexListener implements ActionListener {
		private final Gender gender;

		public SexListener(Gender gender) {
			this.gender = gender;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			enableAndDisableButtons(gender);
			validateAllUserData();
		}
	}

	class LocationListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			JComboBox comboBox = (JComboBox) e.getSource();
			Object selectedItem = comboBox.getSelectedItem();
			if (selectedItem != null) {
				String cityId = ((City) selectedItem).getCityId();
				userCommand.setIdLocation(cityId);
				if (isLocationLoaded) {
					getSaveButton().setEnabled(true);
				}
			}
		}
	}

	// TODO use this listener in the new account dialog
	class DateListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			JComboBox comboBox = (JComboBox) e.getSource();
			int day = Integer.parseInt(getDayComboBox().getSelectedItem().toString());
			int year = Integer.parseInt(getYearComboBox().getSelectedItem().toString());
			int month = getMonthComboBox().getSelectedIndex();
			Calendar cal = new GregorianCalendar(year, month, day);
			cal.setLenient(false);
			try {
				userCommand.setBirthday(cal.getTime());
				comboBox.setName("comboBoxDefaultDate");
				getSaveButton().setEnabled(true);
			} catch (IllegalArgumentException iae) {
				// throw if invalid date
				comboBox.setName("comboBoxInvalidDate");
				getSaveButton().setEnabled(false);
			}
		}
	}

	class CancelListener extends CloseListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			userCommand = null;
			super.actionPerformed(e);
		}
	}
}
