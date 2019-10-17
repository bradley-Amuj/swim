package com.example.user.swim.AsyncTasks;

import android.location.Address;
import android.os.AsyncTask;
import android.widget.Toast;

import com.example.user.swim.Location;
import com.example.user.swim.LocationAdapter;

import org.osmdroid.bonuspack.location.GeocoderGraphHopper;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;

import java.util.List;
import java.util.Locale;

import androidx.recyclerview.widget.RecyclerView;

import static com.example.user.swim.MainActivity.ctx;
import static com.example.user.swim.MainActivity.map;
import static com.example.user.swim.MainActivity.results;

public class GeoCodingTask extends AsyncTask<Object, Integer, List<Address>> {

    private GeoPoint destinationPoint;
    private RecyclerView recyclerView;


    public GeoCodingTask(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;


    }

    @Override
    protected List<Address> doInBackground(Object... params) {
        String locationAddress = (String) params[0];
        GeocoderGraphHopper geocoder = new GeocoderGraphHopper(Locale.getDefault(), "fda57d87-34f0-4a12-9ca1-680cc31bf6fb");

        try {
            BoundingBox viewbox = map.getBoundingBox();
            List<Address> foundAdresses = geocoder.getFromLocationName(locationAddress, results,
                    viewbox.getLatSouth(), viewbox.getLonEast(),
                    viewbox.getLatNorth(), viewbox.getLonWest(), false);
            return foundAdresses;
        } catch (Exception e) {
            return null;
        }

    }

    @Override
    protected void onPostExecute(List<Address> foundAddresses) {
        super.onPostExecute(foundAddresses);
        if (foundAddresses == null) {
            Toast.makeText(ctx, "Error GeoCoding", Toast.LENGTH_SHORT).show();
        } else if (foundAddresses.size() == 0) {
            Toast.makeText(ctx.getApplicationContext(), "Couldn't find location", Toast.LENGTH_SHORT).show();
        } else {
            Address address = foundAddresses.get(0);

            destinationPoint = new GeoPoint(address.getLatitude(), address.getLongitude());
            LocationAdapter adapter = new LocationAdapter(new Location(foundAddresses).createList(results));
            recyclerView.setHasFixedSize(true);
            recyclerView.setAdapter(adapter);

        }

    }
}


