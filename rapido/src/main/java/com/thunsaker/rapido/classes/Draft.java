package com.thunsaker.rapido.classes;

import com.google.gson.Gson;

public class Draft {
	private String Key;
	private String Message;
	private Boolean FailedToPost;

	private Boolean TwitterPosted;
	private Boolean FacebookPosted;
	private Boolean FoursquarePosted;
	private Boolean GooglePlusPosted;
	private Boolean AppDotNetPosted;
	
	private String DateSaved;
	
	public Draft() {
	}
	
	public Draft(String message) {
		setFailedToPost(false);
	}
	
	public String getKey() {
		return Key;
	}
	public void setKey(String key) {
		Key = key;
	}
	
	public String getMessage() {
		return Message;
	}
	public void setMessage(String message) {
		Message = message;
	}
	
	public Boolean getFailedToPost() {
		return FailedToPost;
	}
	public void setFailedToPost(Boolean failedToPost) {
		FailedToPost = failedToPost;
	}
	
	public Boolean getTwitterPosted() {
		return TwitterPosted;
	}
	public void setTwitterPosted(Boolean twitterPosted) {
		TwitterPosted = twitterPosted;
	}
	
	public Boolean getFacebookPosted() {
		return FacebookPosted;
	}
	public void setFacebookPosted(Boolean facebookPosted) {
		FacebookPosted = facebookPosted;
	}
	
	public Boolean getFoursquarePosted() {
		return FoursquarePosted;
	}
	public void setFoursquarePosted(Boolean foursquarePosted) {
		FoursquarePosted = foursquarePosted;
	}
	
	public Boolean getGooglePlusPosted() {
		return GooglePlusPosted;
	}
	public void setGooglePlusPosted(Boolean googlePlusPosted) {
		GooglePlusPosted = googlePlusPosted;
	}
	
	public Boolean getAppDotNetPosted() {
		return AppDotNetPosted;
	}
	public void setAppDotNetPosted(Boolean appDotNetPosted) {
		AppDotNetPosted = appDotNetPosted;
	}

	public String getDateSaved() {
		return DateSaved;
	}
	public void setDateSaved(String dateSaved) {
		DateSaved = dateSaved;
	}
	
	@Override
	public String toString() {
		return toJson(this);
	}
	
	public static String toJson(Draft myDraft) {
		Gson gson = new Gson();
		return myDraft != null ? gson.toJson(myDraft) : "";
	}
	
	public static Draft GetDraftFromJson(String myDraftJson) {
		Gson gson = new Gson();
		return gson.fromJson(myDraftJson, Draft.class);
	}
}