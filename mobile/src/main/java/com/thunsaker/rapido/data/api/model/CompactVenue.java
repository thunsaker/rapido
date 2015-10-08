package com.thunsaker.rapido.data.api.model;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class CompactVenue {
	public String id;
	public String name;
	public Location location;
	public List<FoursquareCategory> categories;
	public String url;
	public String referralId;

	public CompactVenue() {
		this.location = new Location();
		this.categories = new ArrayList<>();
	}

	@Override
	public String toString() {
		return toJson(this);
	}

	public static String toJson(CompactVenue myCompactVenue) {
		Gson gson = new Gson();
		return myCompactVenue != null ? gson.toJson(myCompactVenue, CompactVenue.class) : "";
	}

	public static CompactVenue GetCompactVenueFromJson(String jsonString) {
		Gson gson = new Gson();
		return jsonString != null ? gson.fromJson(jsonString, CompactVenue.class) : null;
	}

	public static CompactVenue ParseCompactVenueFromJson(JsonObject jsonObject) {
		try {
			CompactVenue myVenue = new CompactVenue();

			myVenue.id = jsonObject.get("id") != null
                    ? jsonObject.get("id").getAsString()
                    : "";
			myVenue.name =  jsonObject.get("name") != null
					? jsonObject.get("name").getAsString()
					: "";
			myVenue.location = jsonObject.get("location") != null
					? Location.GetLocationFromJson(
                        jsonObject.getAsJsonObject("location"))
					: null;
			myVenue.categories =
                    jsonObject.get("categories") != null
					? FoursquareCategory.GetCategoriesFromJson(
                            jsonObject.getAsJsonArray("categories"), false)
					: null;
			myVenue.url = jsonObject.get("url") != null
					? jsonObject.get("url").getAsString()
					: "";
			myVenue.referralId = jsonObject.get("referralId") != null
					? jsonObject.get("referralId").getAsString()
					: "";

			return myVenue;
		} catch (Exception e) {
			return null;
		}
	}

    public static CompactVenue GetCompactVenueFromFoursquareCompactVenueResponse(FoursquareCompactVenueResponse response) {
        try {
            CompactVenue myVenue = new CompactVenue();

            myVenue.id = response.id != null
                    ? response.id
                    : "";
            myVenue.name = response.name != null
                    ? response.name
                    : "";
            myVenue.location = response.location != null
                    ? Location
                    .GetLocationFromFoursquareLocationResponse(response.location)
                    : null;
            myVenue.categories = response.categories != null
                    ? response.categories
                    : null;
            myVenue.referralId = response.referralId!= null
                    ? response.referralId
                    : "";
            return myVenue;
        } catch (Exception e) {
            return null;
        }
    }
}