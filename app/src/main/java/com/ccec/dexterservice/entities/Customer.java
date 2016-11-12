package com.ccec.dexterservice.entities;

/**
 * Created by manish on 8/11/16.
 */

public class Customer {
    private String name;
    private String uuid;
    private String phNum;
    private String email;
    private Coordinates coord;

    public Customer(String name, String uuid, String phNum, Coordinates coord) {
        this.name = name;
        this.uuid = uuid;
        this.phNum = phNum;
        this.coord = coord;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getPhNum() {
        return phNum;
    }

    public void setPhNum(String phNum) {
        this.phNum = phNum;
    }

    public Coordinates getCoord() {
        return coord;
    }

    public void setCoord(Coordinates coord) {
        this.coord = coord;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
