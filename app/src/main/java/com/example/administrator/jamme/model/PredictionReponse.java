package com.example.administrator.jamme.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class PredictionReponse {
    @SerializedName("predictions")
    ArrayList<Prediction> predictions;

    public PredictionReponse(ArrayList<Prediction> predictions) {
        this.predictions = predictions;
    }

    public ArrayList<Prediction> getPredictions() {
        return predictions;
    }

    public void setPredictions(ArrayList<Prediction> predictions) {
        this.predictions = predictions;
    }
}
