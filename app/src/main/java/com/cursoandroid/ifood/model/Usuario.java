package com.cursoandroid.ifood.model;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import com.cursoandroid.ifood.service.DatabaseService;
import com.google.firebase.database.Exclude;

public class Usuario implements DatabaseService.ModelId, Parcelable {
    String id;
    String nome;
    String senha;
    String email;
    String foto;
    String tel;
    String tipo;
    Address endereco;

    public static final String TYPE_USER = "U";
    public static final String TYPE_COMPANY = "C";

    public Usuario(){}

    public Usuario(String nome, String email, String senha) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
    }

    protected Usuario(Parcel in) {
        id = in.readString();
        nome = in.readString();
        senha = in.readString();
        email = in.readString();
        foto = in.readString();
        tel = in.readString();
        tipo = in.readString();
        if (Build.VERSION.SDK_INT >= 33) {
            endereco =  in.readSerializable(null, Address.class);
        }else {
            endereco = (Address) in.readSerializable();
        }
    }

    public static final Creator<Usuario> CREATOR = new Creator<Usuario>() {
        @Override
        public Usuario createFromParcel(Parcel in) {
            return new Usuario(in);
        }

        @Override
        public Usuario[] newArray(int size) {
            return new Usuario[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    @Exclude
    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public Address getEndereco() {
        return endereco;
    }

    public void setEndereco(Address endereco) {
        this.endereco = endereco;
    }

    @Exclude
    @Override
    public int describeContents() {
        return 0;
    }

    @Exclude
    @Override
    public void writeToParcel(Parcel parcel, int i) {

        parcel.writeString(id);
        parcel.writeString(nome);
        parcel.writeString(senha);
        parcel.writeString(email);
        parcel.writeString(foto);
        parcel.writeString(tel);
        parcel.writeString(tipo);
        parcel.writeSerializable(endereco);
    }
}
