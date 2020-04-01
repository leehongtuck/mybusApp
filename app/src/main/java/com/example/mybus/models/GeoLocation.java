package com.example.mybus.models;

public class GeoLocation {
    private Location location;
    private int accuracy;

    public Location getLocation() {
        return location;
    }

    public int getAccuracy() {
        return accuracy;
    }

    public class Location {
        private double lat;
        private double lng;

        public double getLat() {
            return lat;
        }

        public double getLng() {
            return lng;
        }
    }
}
