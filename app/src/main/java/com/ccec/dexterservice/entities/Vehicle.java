package com.ccec.dexterservice.entities;

/**
 * Created by aanchalharit on 01/10/16.
 */

public class Vehicle
{
    private String make , model , registrationnumber , chessisnumber ,manufacturedin , kilometer,
            polluctionchkdate , nextpolluctionchkdate , insurancepurchasedate , insuranceduedate , addvehicle;

    public Vehicle(){}

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getRegistrationnumber() {
        return registrationnumber;
    }

    public void setRegistrationnumber(String registrationnumber) {
        this.registrationnumber = registrationnumber;
    }

    public String getChessisnumber() {
        return chessisnumber;
    }

    public void setChessisnumber(String chessisnumber) {
        this.chessisnumber = chessisnumber;
    }

    public String getManufacturedin() {
        return manufacturedin;
    }

    public void setManufacturedin(String manufacturedin) {
        this.manufacturedin = manufacturedin;
    }

    public String getKilometer() {
        return kilometer;
    }

    public void setKilometer(String kilometer) {
        this.kilometer = kilometer;
    }

    public String getPolluctionchkdate() {
        return polluctionchkdate;
    }

    public void setPolluctionchkdate(String polluctionchkdate) {
        this.polluctionchkdate = polluctionchkdate;
    }

    public String getNextpolluctionchkdate() {
        return nextpolluctionchkdate;
    }

    public void setNextpolluctionchkdate(String nextpolluctionchkdate) {
        this.nextpolluctionchkdate = nextpolluctionchkdate;
    }

    public String getInsurancepurchasedate() {
        return insurancepurchasedate;
    }

    public void setInsurancepurchasedate(String insurancepurchasedate) {
        this.insurancepurchasedate = insurancepurchasedate;
    }

    public String getInsuranceduedate() {
        return insuranceduedate;
    }

    public void setInsuranceduedate(String insuranceduedate) {
        this.insuranceduedate = insuranceduedate;
    }

    public String getAddvehicle() {
        return addvehicle;
    }

    public void setAddvehicle(String addvehicle) {
        this.addvehicle = addvehicle;
    }
}
