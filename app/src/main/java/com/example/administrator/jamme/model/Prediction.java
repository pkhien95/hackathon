package com.example.administrator.jamme.model;

import com.google.gson.annotations.SerializedName;

public class Prediction {
    @SerializedName("description")
    String description;


    public Prediction(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
