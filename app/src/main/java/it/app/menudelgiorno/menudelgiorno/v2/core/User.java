package it.app.menudelgiorno.menudelgiorno.v2.core;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {
	private String id, nome, cognome, account;
	private Bitmap foto;

	public User(Parcel in) {
		readFromParcel(in);
	}

	private void readFromParcel(Parcel in) {

		id = in.readString();
		nome = in.readString();
		cognome = in.readString();
		account = in.readString();
		foto = Bitmap.CREATOR.createFromParcel(in);
	}

	public User(String id, String nome, String cognome, String account,
			Bitmap foto) {
		this.id = id;
		this.nome = nome;
		this.cognome = cognome;
		this.account = account;
		this.foto = foto;
	}

	public Bitmap getFoto() {
		return foto;
	}

	public String getAccount() {
		return account;
	}

	public String getNome() {
		return nome;
	}

	public String getCognome() {
		return cognome;
	}

	public String getId() {
		return id;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {

		dest.setDataPosition(0);
		dest.writeString(id);
		dest.writeString(nome);
		dest.writeString(cognome);
		dest.writeString(account);
		foto.writeToParcel(dest, 0);
	}

	public static final Creator<User> CREATOR = new Creator<User>() {
		public User createFromParcel(Parcel in) {
			return new User(in);
		}

		public User[] newArray(int size) {
			return new User[size];
		}
	};

}