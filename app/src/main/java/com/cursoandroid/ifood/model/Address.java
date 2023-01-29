package com.cursoandroid.ifood.model;

import java.io.Serializable;

public class Address implements Serializable {
    String city;
    String cep;
    String number;
    String subAdmin;
    String thoroughfare;

    public Address(){}
    public Address(String city, String cep, String number, String subAdmin, String thoroughfare) {
        this.city = city;
        this.cep = cep;
        this.number = number;
        this.subAdmin = subAdmin;
        this.thoroughfare = thoroughfare;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getSubAdmin() {
        return subAdmin;
    }

    public void setSubAdmin(String subAdmin) {
        this.subAdmin = subAdmin;
    }

    public String getThoroughfare() {
        return thoroughfare;
    }

    public void setThoroughfare(String thoroughfare) {
        this.thoroughfare = thoroughfare;
    }
}
