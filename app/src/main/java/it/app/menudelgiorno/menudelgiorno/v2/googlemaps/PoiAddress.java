package it.app.menudelgiorno.menudelgiorno.v2.googlemaps;

import java.util.HashMap;

public class PoiAddress {
	public static final String KEY_ROUTE = "indirizzo";
	public static final String KEY_POSTALCODE = "cap";
	public static final String KEY_PROVINCE = "provincia";
	public static final String KEY_REGION = "regione";
	public static final String KEY_COUNTRY = "nazione";

	HashMap<String, String> address_components = new HashMap<String, String>();

	public String getComponent(String paramString) {
		if (this.address_components.containsKey(paramString))
			return this.address_components.get(paramString);
		return "";
	}

	public void putComponent(String paramString1, String paramString2) {
		this.address_components.put(paramString1, paramString2);
	}

	public String toString() {
		String str = "";
		if (getComponent("indirizzo") != null)
			str = str + getComponent("indirizzo");
		if (getComponent("cap") != null)
			str = str + " " + getComponent("cap");
		if (getComponent("provincia") != null)
			str = str + "\n" + getComponent("provincia");
		if (getComponent("regione") != null)
			str = str + " " + getComponent("regione");
		if (getComponent("nazione") != null)
			str = str + " " + getComponent("nazione");
		return str;
	}
}