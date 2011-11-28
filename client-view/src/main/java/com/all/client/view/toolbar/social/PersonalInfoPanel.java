package com.all.client.view.toolbar.social;

import java.awt.Dimension;
import java.awt.Rectangle;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import com.all.client.util.Formatters;
import com.all.core.common.view.SynthFonts;
import com.all.i18n.Internationalizable;
import com.all.i18n.Messages;
import com.all.shared.model.ContactInfo;

public final class PersonalInfoPanel extends JPanel implements Internationalizable {

	private static final long serialVersionUID = 1L;

	private static final Dimension DEFAULT_SIZE = new Dimension(150, 113);

	private static final Rectangle BIRTHDAY_LABEL_BOUNDS = new Rectangle(0, 94, 144, 18);

	private static final Rectangle BIRTHDAY_TITLE_LABEL_BOUNDS = new Rectangle(0, 71, 90, 18);

	private static final Rectangle LOCATION_LABEL_BOUNDS = new Rectangle(0, 145, 149, 18);

	private static final Rectangle LOCATION_TITLE_LABEL_BOUNDS = new Rectangle(0, 122, 64, 18);

	private static final Rectangle SEX_LABEL_BOUNDS = new Rectangle(0, 43, 144, 18);

	private static final Rectangle SEX_TITLE_LABEL_BOUNDS = new Rectangle(0, 20, 64, 18);

	private final ContactInfo contactInfo;

	private JLabel birthdayLabel;

	private JLabel birthdayTitleLabel;

	private JLabel locationLabel;

	private JLabel locationTitleLabel;

	private JLabel sexTitleLabel;

	private JLabel sexLabel;

	public PersonalInfoPanel(ContactInfo contactInfo) {
		this.contactInfo = contactInfo;
		initialize();
	}

	private void initialize() {
		this.setLayout(null);
		this.setPreferredSize(DEFAULT_SIZE);
		this.setMinimumSize(DEFAULT_SIZE);
		this.add(getSexTitleLabel());
		this.add(getSexLabel());
		this.add(getBirthdayTitleLabel());
		this.add(getBirthdayLabel());
		this.add(getLocationTitleLabel());
		this.add(getLocationLabel());
	}

	private JLabel getSexTitleLabel() {
		if (sexTitleLabel == null) {
			sexTitleLabel = new JLabel();
			sexTitleLabel.setBounds(SEX_TITLE_LABEL_BOUNDS);
			sexTitleLabel.setHorizontalAlignment(SwingConstants.LEFT);
			sexTitleLabel.setName(SynthFonts.PLAIN_FONT12_107_83_125);
		}
		return sexTitleLabel;
	}

	JLabel getSexLabel() {
		if (sexLabel == null) {
			sexLabel = new JLabel();
			sexLabel.setBounds(SEX_LABEL_BOUNDS);
			sexLabel.setHorizontalAlignment(SwingConstants.LEFT);
			sexLabel.setText(contactInfo.getGender().getLabel());
			sexLabel.setName(SynthFonts.PLAIN_FONT12_GRAY100_100_100);
		}
		return sexLabel;
	}

	private JLabel getBirthdayTitleLabel() {
		if (birthdayTitleLabel == null) {
			birthdayTitleLabel = new JLabel();
			birthdayTitleLabel.setBounds(BIRTHDAY_TITLE_LABEL_BOUNDS);
			birthdayTitleLabel.setHorizontalAlignment(SwingConstants.LEFT);
			birthdayTitleLabel.setName(SynthFonts.PLAIN_FONT12_107_83_125);
		}
		return birthdayTitleLabel;
	}

	JLabel getBirthdayLabel() {
		if (birthdayLabel == null) {
			birthdayLabel = new JLabel();
			birthdayLabel.setBounds(BIRTHDAY_LABEL_BOUNDS);
			birthdayLabel.setHorizontalAlignment(SwingConstants.LEFT);
			birthdayLabel.setName(SynthFonts.PLAIN_FONT12_GRAY100_100_100);
		}
		return birthdayLabel;
	}

	private JLabel getLocationTitleLabel() {
		if (locationTitleLabel == null) {
			locationTitleLabel = new JLabel();
			locationTitleLabel.setBounds(LOCATION_TITLE_LABEL_BOUNDS);
			locationTitleLabel.setHorizontalAlignment(SwingConstants.LEFT);
			locationTitleLabel.setName(SynthFonts.PLAIN_FONT12_107_83_125);
		}
		return locationTitleLabel;
	}

	JLabel getLocationLabel() {
		if (locationLabel == null) {
			locationLabel = new JLabel();
			locationLabel.setText(contactInfo.getCity().getCityName());
			locationLabel.setBounds(LOCATION_LABEL_BOUNDS);
			locationLabel.setHorizontalTextPosition(11);
			locationLabel.setName(SynthFonts.PLAIN_FONT12_GRAY100_100_100);
		}
		return locationLabel;
	}

	@Override
	public void internationalize(final Messages messages) {
		sexTitleLabel.setText(messages.getMessage("profile.sexTitle"));
		locationTitleLabel.setText(messages.getMessage("profile.locationTitle"));
		birthdayTitleLabel.setText(messages.getMessage("profile.birthDayTitle"));
		String dateFormatted = Formatters.formatDate(contactInfo.getBirthday(), messages.getMessage("dateFormat"));
		birthdayLabel.setText(dateFormatted);
	}

	@Override
	public void setMessages(Messages messages) {
		messages.add(this);
	}

	@Override
	public void removeMessages(Messages messages) {
		messages.remove(this);
	}
}
