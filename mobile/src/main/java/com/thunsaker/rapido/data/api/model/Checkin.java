package com.thunsaker.rapido.data.api.model;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

public class Checkin {
	public String id;
	public String createdAt;
	public String type;
    public String shout;
    public int timeZoneOffset;
    public CompactFoursquareUser user;
    public CompactVenue venue;
    public FoursquareSource source;

    @Deprecated
	public boolean isMayor;

    @Deprecated
    @SerializedName("private")
	public boolean isPrivate;

    @Deprecated
    public String visibility;

	public static Checkin GetCheckinFromJson(JsonObject jsonObject) {
		try {
			if(jsonObject.has("venue")) {
				Checkin myCheckin = new Checkin();
				myCheckin.id = jsonObject.get("id") != null ? jsonObject.get("id").getAsString() : "";
				myCheckin.createdAt = jsonObject.get("createdAt") != null ? jsonObject.get("createdAt").getAsString() : "";
				myCheckin.type = jsonObject.get("type") != null ? jsonObject.get("type").getAsString() : "";
				myCheckin.shout = jsonObject.get("shout") != null ? jsonObject.get("shout").getAsString() : "";
				myCheckin.timeZoneOffset = jsonObject.get("timeZoneOffset") != null ? jsonObject.get("timeZoneOffset").getAsInt() : 0;
				myCheckin.source = jsonObject.get("source") != null ? FoursquareSource.GetCheckinSourceFromJson(jsonObject.get("source").getAsJsonObject()) : null;
				myCheckin.venue = jsonObject.get("venue") != null ? CompactVenue.ParseCompactVenueFromJson(jsonObject.get("venue").getAsJsonObject()) : null;
				return myCheckin;
			} else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}