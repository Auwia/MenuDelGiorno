package it.app.menudelgiorno.menudelgiorno.v2.core;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

public class LocaleC implements Parcelable {
	private Bitmap fotoLocale;
	private String nomeLocale, telefonoLocale, tipologiaLocale, via, numero,
			comune, provincia, descrizione, email, sito_web;
	private double km;
	private float ratingLocale;
	private Boolean offertaSpeciale = false, preferito = false;
	private int CAP, id, nVoti;

	public LocaleC(Parcel in) {
		readFromParcel(in);
	}

	private void readFromParcel(Parcel in) {
		fotoLocale = Bitmap.CREATOR.createFromParcel(in);
		tipologiaLocale = in.readString();
		nomeLocale = in.readString();
		telefonoLocale = in.readString();
		km = in.readDouble();
		ratingLocale = in.readFloat();
		offertaSpeciale = in.readByte() != 0;
		preferito = in.readByte() != 0;
		via = in.readString();
		numero = in.readString();
		CAP = in.readInt();
		comune = in.readString();
		provincia = in.readString();
		descrizione = in.readString();
		email = in.readString();
		sito_web = in.readString();
		id = in.readInt();
		nVoti = in.readInt();
	}

	public LocaleC() {

	}

	public void setSitoWeb(String sito_web) {
		this.sito_web = sito_web;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	public void setVia(String via) {
		this.via = via;
	}

	public void setComune(String comune) {
		this.comune = comune;
	}

	public void setProvincia(String provincia) {
		this.provincia = provincia;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public void setCAP(int CAP) {
		this.CAP = CAP;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setFotoLocale(Bitmap fotoLocale) {
		this.fotoLocale = fotoLocale;
	}

	public void setNomeLocale(String nomeLocale) {
		this.nomeLocale = nomeLocale;
	}

	public void setTipologiaLocale(String tipologiaLocale) {
		this.tipologiaLocale = tipologiaLocale;
	}

	public void setTelefonoLocale(String telefonoLocale) {
		this.telefonoLocale = telefonoLocale;
	}

	public void setKm(double km) {
		this.km = km;
	}

	public void setRatingLocale(float ratingLocale) {
		this.ratingLocale = ratingLocale;
	}

	public void setOffertaSpeciale(boolean offertaSpeciale) {
		this.offertaSpeciale = offertaSpeciale;
	}

	public boolean setPreferito(boolean preferito) {
		this.preferito = preferito;
		return preferito;
	}

	public boolean isPreferito() {
		return preferito;
	}

	public void setVoti(int nVoti) {
		this.nVoti = nVoti;
	}

	public int getVoti() {
		return nVoti;
	}

	public String getSitoWeb() {
		return sito_web;
	}

	public String getEmail() {
		return email;
	}

	public String getDescrizione() {
		return descrizione;
	}

	public String getVia() {
		return via;
	}

	public String getComune() {
		return comune;
	}

	public String getProvincia() {
		return provincia;
	}

	public String getNumero() {
		return numero;
	}

	public int getCAP() {
		return CAP;
	}

	public int getId() {
		return id;
	}

	public Bitmap getFotoLocale() {
		return fotoLocale;
	}

	public String getNomeLocale() {
		return nomeLocale;
	}

	public String getTipologiaLocale() {
		return tipologiaLocale;
	}

	public String getTelefonoLocale() {
		return telefonoLocale;
	}

	public double getKm() {
		return km;
	}

	public float getRatingLocale() {
		return ratingLocale;
	}

	public Boolean haveOffertaSpeciale() {
		return offertaSpeciale;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {

		fotoLocale.writeToParcel(dest, 0);
		dest.setDataPosition(0);
		dest.writeString(tipologiaLocale);
		dest.writeString(nomeLocale);
		dest.writeString(telefonoLocale);
		dest.writeDouble(km);
		dest.writeFloat(ratingLocale);
		dest.writeByte((byte) (offertaSpeciale ? 1 : 0));
		dest.writeByte((byte) (preferito ? 1 : 0));
		dest.writeString(via);
		dest.writeString(numero);
		dest.writeInt(CAP);
		dest.writeString(comune);
		dest.writeString(provincia);
		dest.writeString(descrizione);
		dest.writeString(email);
		dest.writeString(sito_web);
		dest.writeInt(id);
		dest.writeInt(nVoti);
	}

	public static final Creator<LocaleC> CREATOR = new Creator<LocaleC>() {
		public LocaleC createFromParcel(Parcel in) {
			return new LocaleC(in);
		}

		public LocaleC[] newArray(int size) {
			return new LocaleC[size];
		}
	};

}