package it.app.menudelgiorno.menudelgiorno.v2.core;

class Ranking {
	private final int id_menu;
	private final float ranking;
	private final String commenti;

	public Ranking(int id_menu, float ranking, String commenti) {
		this.id_menu = id_menu;
		this.ranking = ranking;
		this.commenti = commenti;
	}

	public float getIdMenu() {
		return id_menu;
	}

	public float getRanking() {
		return ranking;
	}

	public String getCommenti() {
		return commenti;
	}
}