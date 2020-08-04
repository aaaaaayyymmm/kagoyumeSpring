package com.kagoyume.business;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;

public class UserDataDTO implements Serializable {
	private int userID;
	private String name;
	private String password;
	private String mail;
	private String address;
	private int total;
	private Timestamp newDate;
	private ArrayList<CartProductsDTO> cart;	//いらないかも

	private String itemCode;
	private int type;
	private Timestamp buyDate;
	private String proName;

	public int getUserID() {
		return userID;
	}
	public void setUserID(int userID) {
		this.userID = userID;
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

	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}

	public Timestamp getNewDate() {
		return newDate;
	}
	public void setNewDate(Timestamp newDate) {
		this.newDate = newDate;
	}

	public ArrayList<CartProductsDTO> getCart() {
		return cart;
	}
	public void setCart(ArrayList<CartProductsDTO> cart) {
		this.cart = cart;
	}
	public String getItemCode() {
		return itemCode;
	}
	public void setItemCode(String itemCode) {
		this.itemCode = itemCode;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public Timestamp getBuyDate() {
		return buyDate;
	}
	public void setBuyDate(Timestamp buyDate) {
		this.buyDate = buyDate;
	}
	public String getProName() {
		return proName;
	}
	public void setProName(String proName) {
		this.proName = proName;
	}

}