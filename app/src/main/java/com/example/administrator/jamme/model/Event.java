package com.example.administrator.jamme.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Event implements Serializable {

    @SerializedName("lat") private double lat;
    @SerializedName("lng") private double  lng;
    @SerializedName("type") private int type;
    @SerializedName("message") private String message;
    @SerializedName("img") private String img;

    public Event(String id, String user_post, double lat, double lng, int type, String message, String img) {
        this.lat = lat;
        this.lng = lng;
        this.type = type;
        this.message = message;
        this.img = img;
    }

    public Event(){}

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }
}
