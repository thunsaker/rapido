package com.thunsaker.rapido.classes.foursquare;

import com.google.gson.JsonObject;

public class Contact {
	private Integer Phone;
	private String FormattedPhone;
	private String Twitter;
	
	public Integer getPhone() {
		return Phone;
	}
	public void setPhone(Integer phone) {
		Phone = phone;
	}
	public String getFormattedPhone() {
		return FormattedPhone;
	}
	public void setFormattedPhone(String formattedPhone) {
		FormattedPhone = formattedPhone;
	}
	public String getTwitter() {
		return Twitter;
	}
	public void setTwitter(String twitter) {
		Twitter = twitter;
	}
	public static Contact GetContactFromJson(JsonObject jsonObject) {
		try {
			Contact myContact = new Contact();
			myContact.setPhone(jsonObject.get("phone") != null ? jsonObject.get("phone").getAsInt() : 0);
			myContact.setFormattedPhone(jsonObject.get("formattedPhone") != null ? jsonObject.get("formattedPhone").getAsString() : "");
			myContact.setTwitter(jsonObject.get("twitter") != null ? jsonObject.get("twitter").getAsString() : "");
			return myContact;
		} catch (Exception e) {
			return null;
		}
	}
}
