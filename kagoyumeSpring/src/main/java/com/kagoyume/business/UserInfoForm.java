package com.kagoyume.business;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class UserInfoForm {

	@NotBlank(message="お名前を入力してください")
	@Size(max = 64, message="お名前は64文字以内で入力してください")
	private String name;

	@NotBlank(message="パスワードを入力してください")
	@Size(max = 64, message="パスワードは64文字以内で入力してください")
	private String password;

	@NotBlank(message="メールアドレスを入力してください")
	@Size(max = 128, message="メールアドレスは128文字以内で入力してください")
	@Email(message="メールアドレスを正しい形式で入力してください")
	private String mail;

	@NotBlank(message="住所を入力してください")
	@Size(max = 128, message="住所は128文字以内で入力してください")
	private String address;

	public UserInfoForm(String name, String password, String mail, String address) {
		this.name = name;
		this.password = password;
		this.mail = mail;
		this.address = address;
	}

	public UserInfoForm() {
		this("", "", "", "");
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}

	public String getMail() {
		return mail;
	}
	public void setMail(String mail) {
		this.mail = mail;
	}

	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}

}