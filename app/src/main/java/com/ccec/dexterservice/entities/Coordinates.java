package com.ccec.dexterservice.entities;

/**
 * Created by manish on 8/11/16.
 */

public class Coordinates {
    private Double lat;
    private Double lon;


    public Coordinates(Double lat, Double lon) {
        this.lat = lat;
        this.lon = lon;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }
}
