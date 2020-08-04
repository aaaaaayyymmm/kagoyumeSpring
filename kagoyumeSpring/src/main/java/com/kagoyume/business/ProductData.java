package com.kagoyume.business;

import java.io.Serializable;

public class ProductData implements Serializable{

	private String hitNum;
	private String imageURL;
	private String proName;
	private int price;
	private String proId;
	private String caption;
	private String review;
	private String proURL;
	private int searchResultNum;

	public String getHitNum() {
		return hitNum;
	}
	public void setHitNum(String hitNum) {
		this.hitNum = hitNum;
	}

	public String getImageURL() {
		return imageURL;
	}
	public void setImageURL(String imageURL) {
		this.imageURL = imageURL;
	}

	public String getProName() {
		return proName;
	}
	public void setProName(String proName) {
		this.proName = proName;
	}

	public int getPrice() {
		return price;
	}
	public void setPrice(int price) {
		this.price = price;
	}

	public String getProId() {
		return proId;
	}
	public void setProId(String proId) {
		this.proId = proId;
	}

	public String getCaption() {
		return caption;
	}
	public void setCaption(String caption) {
		this.caption = caption;
	}

	public String getReview() {
		return review;
	}
	public void setReview(String review) {
		this.review = review;
	}

	public String getProURL() {
		return proURL;
	}
	public void setProURL(String proURL) {
		this.proURL = proURL;
	}

	public int getSearchResultNum() {
		return searchResultNum;
	}
	public void setSearchResultNum(int searchResultNum) {
		this.searchResultNum = searchResultNum;
	}

}
