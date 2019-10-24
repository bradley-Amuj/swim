package com.example.user.swim.AsyncTasks;

import android.location.Address;
import android.os.AsyncTask;

import org.osmdroid.bonuspack.location.GeocoderGraphHopper;
import org.osmdroid.util.GeoPoint;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class ReverseGeocodingTask extends AsyncTask<GeoPoint, Void, List<Address>> {
    public static String my_location;
    GeoPoint geoPoint;


    public ReverseGeocodingTask(GeoPoint geoPoint) {
        this.geoPoint = geoPoint;


    }

    @Override
    protected List<Address> doInBackground(GeoPoint... geoPoints) {


        GeocoderGraphHopper geocoder = new GeocoderGraphHopper(Locale.getDefault(), "fda57d87-34f0-4a12-9ca1-680cc31bf6fb");
        try {
            List<Address> loc = geocoder.getFromLocation(geoPoint.getLatitude(), geoPoint.getLongitude(), 1);
            return loc;
        } catch (IOException e) {
            e.printStackTrace();
        }


        return null;
    }

    @Override
    protected void onPostExecute(List<Address> location) {
        super.onPostExecute(location);

        String current_location = (String) location.get(0).getExtras().get("display_name");
        my_location = current_location.split(",")[0];


    }
}
