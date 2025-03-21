package it.app.menudelgiorno.menudelgiorno.v2.core;

public class Pranzo {
	private int id_locale, id_pranzo;
	private String data_ora_evento, nome_locale, id_organizzatore;
	private boolean prenotato;

	public Pranzo() {
	}

	public String getNomeLocale() {
		return nome_locale;
	}

	public int getIdLocale() {
		return id_locale;
	}

	public int getIdPranzo() {
		return id_pranzo;
	}

	public String getIdOrganizzatore() {
		return id_organizzatore;
	}

	public String getDataOraEvento() {
		return data_ora_evento;
	}

	public boolean isPrenotato() {
		return prenotato;
	}

	public void setNomeLocale(String nome_locale) {
		this.nome_locale = nome_locale;
	}

	public void setIdLocale(int id_locale) {
		this.id_locale = id_locale;
	}

	public void setIdPranzo(int id_pranzo) {
		this.id_pranzo = id_pranzo;
	}

	public void setIdOrganizzatore(String id_organizzatore) {
		this.id_organizzatore = id_organizzatore;
	}

	public void setDataOraEvento(String data_ora_evento) {
		this.data_ora_evento = data_ora_evento;
	}

	public void setPrenotato(boolean prenotato) {
		this.prenotato = prenotato;
	}

}