package com.example.user.swim;

import android.location.Address;

import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.List;

public class Location {

    private List<Address> addresses;
    private String display_name;
    private GeoPoint point;


    public Location(String display_name, GeoPoint point) {
        this.display_name = display_name;
        this.point = point;
    }

    public Location(List<Address> addresses) {
        this.addresses = addresses;
    }

    public String getDisplay_name() {
        return display_name;
    }


    public GeoPoint getPoint() {
        return point;
    }


    public ArrayList<Location> createList(int size) {

        ArrayList<Location> locations = new ArrayList<Location>();

        for (int i = 0; i < size; i++) {
            try {
                locations.add(new Location(addresses.get(i).getExtras().getString("display_name"), new GeoPoint(addresses.get(i).getLatitude(), addresses.get(i).getLongitude())));
            } catch (IndexOutOfBoundsException e) {

            }

        }

        return locations;
    }
}
