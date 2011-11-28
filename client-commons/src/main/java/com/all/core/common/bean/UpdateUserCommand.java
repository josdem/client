package com.all.core.common.bean;

import java.awt.Image;
import java.io.Serializable;
import java.util.Date;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import com.all.shared.model.Gender;
import com.all.shared.model.User;
import com.all.shared.model.constraints.Name;
import com.all.shared.model.constraints.Nickname;
import com.all.shared.model.constraints.NotEmpty;

public class UpdateUserCommand implements Serializable {
	private static final long serialVersionUID = 1L;
	private Long id;
	@Name
	protected String firstName;
	@Name
	protected String lastName;
	@Nickname
	private String nickName;
	@Temporal(TemporalType.DATE)
	@NotNull(message = "")
	private Date birthday;
	@Enumerated(EnumType.ORDINAL)
	@NotNull(message = "")
	private Gender gender;
	@NotEmpty(message = "Please submit an available city from the list. You cant submit a new city.")
	private String idLocation;
	private Image avatar;
	// TODO: fix this after presentation February 3rd 2010
	// @ZipCode
	private String zipCode;

	public UpdateUserCommand() {
	}

	public UpdateUserCommand(User user) {
		setGender(user.getGender());
		setFirstName(user.getFirstName());
		setLastName(user.getLastName());
		setNickName(user.getNickName());
		setBirthday(user.getBirthday());
		setIdLocation(user.getIdLocation());
		setZipCode(user.getZipCode());
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public Date getBirthday() {
		return birthday;
	}

	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}

	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

	public String getIdLocation() {
		return idLocation;
	}

	public void setIdLocation(String idLocation) {
		this.idLocation = idLocation;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public User toUser() {
		return new User(this.id, this.firstName, this.lastName, this.nickName, this.birthday, this.gender, this.idLocation,
				this.zipCode);
	}

	public Image getAvatar() {
		return avatar;
	}

	public void setAvatar(Image avatar) {
		this.avatar = avatar;
	}

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

	public String getZipCode() {
		return zipCode;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
}
