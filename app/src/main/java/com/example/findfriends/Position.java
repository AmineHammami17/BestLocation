package com.example.findfriends;

import android.util.Log;

import java.io.Serializable;

public class Position implements Serializable {
    private String id;
    private String name;
    private String phoneNumber;
    private String lat;
    private String longitude;

    public Position(String id, String name, String phoneNumber, double latitude, double longitude) {
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.lat = String.valueOf(latitude);
        this.longitude = String.valueOf(longitude);
    }

    public Position(String name, String phoneNumber, double latitude, double longitude) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.lat = String.valueOf(latitude);
        this.longitude = String.valueOf(longitude);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    // Return latitude as String
    public String getLatitude() {
        try {
            return String.format("%.6f", Double.parseDouble(lat));
        } catch (NumberFormatException e) {
            Log.e("PositionParsing", "Invalid latitude value: " + lat, e);
            return "0.0";
        }
    }
    public void setLat(String lat) {
        this.lat = lat;
    }

    // Return longitude as String
    public String getLongitude() {
        try {
            return String.format("%.6f", Double.parseDouble(longitude)); // Limiting to 6 decimal places
        } catch (NumberFormatException e) {
            Log.e("PositionParsing", "Invalid longitude value: " + longitude, e);
            return "0.0"; // Return a default value if parsing fails
        }
    }
    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

}
