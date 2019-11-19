package com.example.user.swim.Models;

public class RideOffer {

    public String Email;
    public String current_location;
    public String Destination;

    public RideOffer() {

    }

    public RideOffer(String email, String current_location, String destination) {
        Email = email;
        this.current_location = current_location;
        Destination = destination;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getCurrent_location() {
        return current_location;
    }

    public void setCurrent_location(String current_location) {
        this.current_location = current_location;
    }

    public String getDestination() {
        return Destination;
    }

    public void setDestination(String destination) {
        Destination = destination;
    }
}
