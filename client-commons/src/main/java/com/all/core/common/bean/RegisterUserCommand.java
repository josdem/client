package com.all.core.common.bean;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.springframework.beans.factory.annotation.Autowired;

import com.all.i18n.Internationalizable;
import com.all.i18n.Messages;
import com.all.shared.model.User;
import com.all.shared.model.constraints.Email;
import com.all.shared.model.constraints.NotEmpty;
import com.all.shared.model.constraints.SameValue;

public class RegisterUserCommand extends UpdateUserCommand implements Internationalizable {
	public static final int MINIMUM_PASSWORD_SIZE = 8;
	public static final int MAXIMUM_PASSWORD_SIZE = 25;
	public static final int MINIMUM_NICKNAME_SIZE = 4;
	public static final int MAXIMUM_NICKNAME_SIZE = 25;
	private static final long serialVersionUID = 1L;
	@NotEmpty
	@Pattern(regexp = Email.patternString, message = "{validation.email.invalid}")
	private String email;
	@NotEmpty
	@Size(max = 25, min = 8)
	private String password;
	@Size(max = MAXIMUM_PASSWORD_SIZE, min = MINIMUM_PASSWORD_SIZE)
	private String confirmPassword;
	@SameValue(first = "password", second = "confirmPassword")
	private RegisterUserCommand passwordValidation = this;
	private String encryptedPwd;
	@Autowired
	private Messages messages;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	/*
	 * NOTE: This Setter must be like this to make work out the annotation
	 * SameValue
	 */
	public void setEmailValidation(RegisterUserCommand emailValidation) {
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getConfirmPassword() {
		return confirmPassword;
	}

	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}

	public RegisterUserCommand getPasswordValidation() {
		return passwordValidation;
	}

	/*
	 * NOTE: This Setter must be like this to make work out the annotation
	 * SameValue
	 */
	public void setPasswordValidation(RegisterUserCommand passwordValidation) {
	}

	public User toUser() {
		return new User(this.firstName, this.lastName, getNickName(), getGender(), this.email, this.getEncryptedPwd(),
				getBirthday(), getIdLocation(), getZipCode());
	}

	public void setEncryptedPwd(String encryptedPwd) {
		this.encryptedPwd = encryptedPwd;
	}

	public String getEncryptedPwd() {
		return encryptedPwd;
	}

	@Override
	public void internationalize(Messages messages) {

	}

	@Override
	public void removeMessages(Messages messages) {
		this.messages.remove(this);
	}

	@Override
	public void setMessages(Messages messages) {
	}
}
