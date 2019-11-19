package com.example.user.swim.AsyncTasks;

import android.location.Address;
import android.os.AsyncTask;
import android.widget.Toast;

import com.example.user.swim.Location;
import com.example.user.swim.LocationAdapter;

import org.osmdroid.bonuspack.location.GeocoderGraphHopper;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import java.util.List;
import java.util.Locale;

import androidx.recyclerview.widget.RecyclerView;

import static com.example.user.swim.MainActivity.ctx;

public class GeoCodingTask extends AsyncTask<Object, Integer, List<Address>> {

    private GeoPoint destinationPoint;
    private RecyclerView recyclerView;
    private MapView map;
    private int results;


    public GeoCodingTask(RecyclerView recyclerView, MapView map, int results) {
        this.recyclerView = recyclerView;
        this.map = map;
        this.results = results;


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


