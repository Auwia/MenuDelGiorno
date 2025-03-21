package it.app.menudelgiorno.menudelgiorno.v2;

public class ListaReportRecensioniIndice {
	private final String recensionista;
    private final String recensione;
	private final Double voto;

	public ListaReportRecensioniIndice(String recensionista, String recensione,
			Double voto) {
		this.recensionista = recensionista;
		this.recensione = recensione;
		this.voto = voto;
	}

	public String getRecensione() {
		return recensione;
	}

	public String getRecensionista() {
		return recensionista;
	}

	public Double getVoto() {
		return voto;
	}
}