package com.example.mybus.models;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

public class Bus {
    private int id;
    private String model;
    @SerializedName("number_plate")
    private String numberPlate;

    public int getId() {
        return id;
    }

    public String getModel() {
        return model;
    }

    public String getNumberPlate() {
        return numberPlate;
    }

    @NonNull
    @Override
    public String toString() {
        return numberPlate + " (" + model + ")";
    }
}
