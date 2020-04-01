package com.example.mybus.models;

import com.google.gson.annotations.SerializedName;

public class Shift {
    private int id;
    @SerializedName("shift_code")
    private String shiftCode;
    @SerializedName("shift_description")
    private String shiftDescription;
    @SerializedName("start_time")
    private String startTime;
    @SerializedName("end_time")
    private String endTime;
    @SerializedName("route_code")
    private String routeCode;
    @SerializedName("route_description")
    private String routeDescription;
    private Bus bus;

    public int getId() {
        return id;
    }

    public String getShiftCode() {
        return shiftCode;
    }

    public String getShiftDescription() {
        return shiftDescription;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getRouteCode() {
        return routeCode;
    }

    public String getRouteDescription() {
        return routeDescription;
    }

    public Bus getBus() {
        return bus;
    }

    public void setBus(Bus bus) {
        this.bus = bus;
    }
}
