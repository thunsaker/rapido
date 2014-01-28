package com.thunsaker.rapido.classes.foursquare;

public class Category {
	private String Id;
	private String Name;
	private String PluralName;
	private String ShortName;
	private FoursquareImage Icon;
	private Boolean Primary;
	
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
	
	public String getPluralName() {
		return PluralName;
	}
	public void setPluralName(String pluralName) {
		PluralName = pluralName;
	}
	
	public String getShortName() {
		return ShortName;
	}
	public void setShortName(String shortName) {
		ShortName = shortName;
	}
	
	public FoursquareImage getIcon() {
		return Icon;
	}
	public void setIcon(FoursquareImage icon) {
		Icon = icon;
	}
	
	public Boolean getPrimary() {
		return Primary;
	}
	public void setPrimary(Boolean primary) {
		Primary = primary;
	}	
}