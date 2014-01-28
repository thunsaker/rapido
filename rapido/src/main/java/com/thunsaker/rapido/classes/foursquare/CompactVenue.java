package com.thunsaker.rapido.classes.foursquare;

import com.google.gson.JsonObject;

import java.util.List;

public class CompactVenue {
	private String Id;
	private String Name;
	private Contact Contact;
	private Location Location;
	private String CanonicalUrl;
	private List<Category> Categories;
	private Boolean Verified;
	private VenueStats Stats;
	private String Url;
/*	private VenueLikes Likes;
	private Menu Menu;
	private BeenHere BeenHere;
	private Specials Specials;
	private HereNow HereNow;
	private Listed listed; */
	private String ReferralId;

	public String getId() {
		return Id;
	}
	public void setId(String id) {
		Id = id;
	}

	public String getName() {
		return Name;
	}
	public void setName(String name) {
		Name = name;
	}

	public Contact getContact() {
		return Contact;
	}
	public void setContact(Contact contact) {
		Contact = contact;
	}

	public Location getLocation() {
		return Location;
	}
	public void setLocation(Location location) {
		Location = location;
	}

	public String getCanonicalUrl() {
		return CanonicalUrl;
	}
	public void setCanonicalUrl(String canonicalUrl) {
		CanonicalUrl = canonicalUrl;
	}

	public List<Category> getCategories() {
		return Categories;
	}
	public void setCategories(List<Category> categories) {
		Categories = categories;
	}

	public Boolean getVerified() {
		return Verified;
	}
	public void setVerified(Boolean verified) {
		Verified = verified;
	}

	public VenueStats getStats() {
		return Stats;
	}
	public void setStats(VenueStats stats) {
		Stats = stats;
	}

	public String getUrl() {
		return Url;
	}
	public void setUrl(String url) {
		Url = url;
	}

	public String getReferralId() {
		return ReferralId;
	}
	public void setReferralId(String referralId) {
		this.ReferralId = referralId;
	}

	public static CompactVenue GetCompactVenueFromJson(JsonObject jsonObject) {
		try {
			CompactVenue myVenue = new CompactVenue();

			myVenue.setId(jsonObject.get("id") != null ? jsonObject.get("id").getAsString() : "");
			myVenue.setName(jsonObject.get("name") != null ? jsonObject.get("name").getAsString() : "");
			myVenue.setContact(jsonObject.get("contact") != null
					? com.thunsaker.rapido.classes.foursquare.Contact.GetContactFromJson(jsonObject.getAsJsonObject("contact")) : null);
			myVenue.setLocation(jsonObject.get("location") != null
					? com.thunsaker.rapido.classes.foursquare.Location.GetLocationFromJson(jsonObject.getAsJsonObject("location")) : null);
			myVenue.setCanonicalUrl(jsonObject.get("canonicalUrl") != null ? jsonObject.get("canonicalUrl").getAsString() : "");
			myVenue.setCategories(null);
			myVenue.setVerified(jsonObject.get("verified") != null ? jsonObject.get("verified").getAsBoolean() : false);
			myVenue.setStats(null);
			myVenue.setUrl(jsonObject.get("url") != null ? jsonObject.get("url").getAsString() : "");
			myVenue.setReferralId(jsonObject.get("referralId") != null ? jsonObject.get("referralId").getAsString() : "");

			return myVenue;
		} catch (Exception e) {
			return null;
		}
	}
}