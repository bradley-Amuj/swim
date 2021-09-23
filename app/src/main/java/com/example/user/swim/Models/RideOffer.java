package com.example.user.swim.Models;

public class RideOffer {

    public String driver_Email;
    public String passenger_Email;

    public RideOffer() {

    }

    public RideOffer(String driver_Email, String passenger_Email) {
        this.driver_Email = driver_Email;
        this.passenger_Email = passenger_Email;
    }

    public String getDriver_Email() {
        return driver_Email;
    }

    public void setDriver_Email(String driver_Email) {
        this.driver_Email = driver_Email;
    }

    public String getPassenger_Email() {
        return passenger_Email;
    }

    public void setPassenger_Email(String passenger_Email) {
        this.passenger_Email = passenger_Email;
    }
}
