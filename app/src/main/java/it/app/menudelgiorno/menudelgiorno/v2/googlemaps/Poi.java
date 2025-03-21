package it.app.menudelgiorno.menudelgiorno.v2.googlemaps;

import android.location.Location;

public class Poi implements Comparable<Object> {
	private PoiAddress address = new PoiAddress();
	private String description, nome_locale;
	private double distance;
	private final Location location = new Location("poiLocation");
	private int resourseIconId;
    private final int id_locale;
	private String tel = "";
	private String trade = "";

	Poi(double paramDouble1, double paramDouble2, int id_locale) {
		this.location.setLatitude(paramDouble1);
		this.location.setLongitude(paramDouble2);
		this.id_locale = id_locale;
		this.distance = 3.402823466385289E+038D;
	}

	public int compareTo(Object paramObject) {
		Poi localPoi = (Poi) paramObject;
		if (this.distance < localPoi.distance)
			return -1;
		if (this.distance > localPoi.distance)
			return 1;
		return 0;
	}

	public int getIdLocale() {
		return this.id_locale;
	}

	public PoiAddress getAddress() {
		return this.address;
	}

	public String getDescription() {
		return this.description;
	}

	public String getNomeLocale() {
		return this.nome_locale;
	}

	public double getDistance() {
		return this.distance;
	}

	public Location getLocation() {
		return this.location;
	}

	public int getResourseIconId() {
		return this.resourseIconId;
	}

	public String getTel() {
		return this.tel;
	}

	public String getTrade() {
		return this.trade;
	}

	public void setAddress(PoiAddress paramPoiAddress) {
		this.address = paramPoiAddress;
	}

	public void setDistance(float paramFloat) {
		this.distance = paramFloat;
	}

	public void setResourseIconId(int paramInt) {
		this.resourseIconId = paramInt;
	}

	public void setTel(String paramString) {
		this.tel = paramString;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public void setNomeLocale(String nome_locale) {
		this.nome_locale = nome_locale;
	}

	public void setTrade(String paramString) {
		this.trade = paramString;
	}

	public String toString() {
		return this.description + " " + this.distance + " m";
	}
}
