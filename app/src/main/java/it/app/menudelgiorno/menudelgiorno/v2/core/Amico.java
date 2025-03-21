package it.app.menudelgiorno.menudelgiorno.v2.core;

public class Amico {
	private String id, nome;
	private boolean flag;
	private int id_pranzo;

	public Amico(String id, String nome, boolean flag) {
		this.nome = nome;
		this.flag = flag;
		this.id = id;
	}

	public Amico() {
	}

	public String getNomeAmico() {
		return nome;
	}

	public String getId() {
		return id;
	}

	public int getIdPranzo() {
		return id_pranzo;
	}

	public void setIdPranzo(int id_pranzo) {
		this.id_pranzo = id_pranzo;
	}

	public boolean isFlaggato() {
		return flag;
	}

	public void setNomeAmico(String nome) {
		this.nome = nome;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setFlaggato(boolean flag) {
		this.flag = flag;
	}

}