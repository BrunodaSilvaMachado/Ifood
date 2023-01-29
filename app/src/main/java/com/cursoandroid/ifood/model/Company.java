package com.cursoandroid.ifood.model;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import com.cursoandroid.ifood.service.DatabaseService;

public class Company implements DatabaseService.ModelId, Parcelable {
    private String id;
    private String urlPhoto;
    private String name;
    private String category;
    private String deliveryTime;
    private String deliveryTax;

    public Company() {
    }

    public Company(String id, String urlPhoto, String name, String category, String deliveryTime, String deliveryTax) {
        this.id = id;
        this.urlPhoto = urlPhoto;
        this.name = name;
        this.category = category;
        this.deliveryTime = deliveryTime;
        this.deliveryTax = deliveryTax;
    }

    protected Company(Parcel in) {
        id = in.readString();
        urlPhoto = in.readString();
        name = in.readString();
        category = in.readString();
        deliveryTime = in.readString();
        deliveryTax = in.readString();
    }

    public static final Creator<Company> CREATOR = new Creator<Company>() {
        @Override
        public Company createFromParcel(Parcel in) {
            return new Company(in);
        }

        @Override
        public Company[] newArray(int size) {
            return new Company[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrlPhoto() {
        return urlPhoto;
    }

    public void setUrlPhoto(String urlPhoto) {
        this.urlPhoto = urlPhoto;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDeliveryTax() {
        return deliveryTax;
    }

    public void setDeliveryTax(String deliveryTax) {
        this.deliveryTax = deliveryTax;
    }

    public String getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(String deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(urlPhoto);
        dest.writeString(name);
        dest.writeString(category);
        dest.writeString(deliveryTime);
        dest.writeString(deliveryTax);
    }
}
