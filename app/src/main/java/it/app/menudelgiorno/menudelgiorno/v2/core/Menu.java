package it.app.menudelgiorno.menudelgiorno.v2.core;

public class Menu {
	private double prezzo;
	private float km, rating;
	private String nomeMenu, nomeLocale;
	private int id, counter;

	public Menu(String nomeMenu, String nomeLocale, double prezzo, float km,
			float rating, int id, int counter) {
		this.nomeLocale = nomeLocale;
		this.prezzo = prezzo;
		this.km = km;
		this.rating = rating;
		this.nomeMenu = nomeMenu;
		this.id = id;
		this.counter = counter;
	}

	public Menu() {
	}

	public String getNomeMenu() {
		return nomeMenu;
	}

	public double getPrezzo() {
		return prezzo;
	}

	public float getKm() {
		return km;
	}

	public float getRatingMenu() {
		return rating;
	}

	public int getCounter() {
		return counter;
	}

	public void setCounter(int counter) {
		this.counter = counter;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getNomeLocale() {
		return nomeLocale;
	}

	public void setNomeMenu(String nomeMenu) {
		this.nomeMenu = nomeMenu;
	}

	public void setPrezzo(double prezzo) {
		this.prezzo = prezzo;
	}

	public void setKm(float km) {
		this.km = km;
	}

	public void setRatingMenu(float rating) {
		this.rating = rating;
	}

	public void setNomeLocale(String nomeLocale) {
		this.nomeLocale = nomeLocale;
	}
}