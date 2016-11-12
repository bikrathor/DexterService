package com.ccec.dexterservice.entities;

/**
 * Created by manish on 8/11/16.
 */

public class ServiceCenter {
    private String name;
    private String uuid;
    private String phNum;
    private String email;
    private String type;
    private Coordinates coord;

    public ServiceCenter(String name, String uuid, String phNum, String email, String type, Coordinates coord) {
        this.name = name;
        this.uuid = uuid;
        this.phNum = phNum;
        this.email = email;
        this.type = type;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Coordinates getCoord() {
        return coord;
    }

    public void setCoord(Coordinates coord) {
        this.coord = coord;
    }
}
