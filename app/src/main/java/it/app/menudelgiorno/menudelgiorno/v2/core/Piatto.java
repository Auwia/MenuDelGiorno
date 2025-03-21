package it.app.menudelgiorno.menudelgiorno.v2.core;

class Piatto {
	private final int id_menu;
    private final int id;
	private final String descrizione;

	public Piatto(int id_menu, int id, String descrizione) {
		this.id_menu = id_menu;
		this.id = id;
		this.descrizione = descrizione;
	}

	public int getId() {
		return id;
	}

	public int getIdMenu() {
		return id_menu;
	}

	public String getDescrizione() {
		return descrizione;
	}
}