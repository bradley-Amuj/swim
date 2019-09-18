package com.example.user.swim;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

import org.osmdroid.bonuspack.location.GeocoderGraphHopper;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {
    protected GeoPoint startPoint, destinationPoint;
    protected static int START_INDEX = -2, DEST_INDEX = -1;
    protected Marker markerStart, markerDestination;


    protected MapView map;
    private MapController mapController;
    private Context ctx;
    MyLocationNewOverlay mLocationNewOverlay;

    private BottomSheetBehavior sheetBehavior;
    private LinearLayout bottom_sheet;
    private Button search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        setContentView(R.layout.activity_main);

//        startPoint = mLocationNewOverlay.getMyLocation();

        initMyLocation();

        bottom_sheet = findViewById(R.id.bottom_sheet);
        sheetBehavior = BottomSheetBehavior.from(bottom_sheet);
        search = findViewById(R.id.search_destination);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                handleSearchButton(DEST_INDEX, R.id.destination);
            }
        });

        sheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED: {

                    }
                    break;
                    case BottomSheetBehavior.STATE_COLLAPSED: {

                    }
                    break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

    }


    @Override
    protected void onResume() {
        super.onResume();
        map.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        map.onPause();
    }


    private class GeocodingTask extends AsyncTask<Object, Void, List<Address>> {
        int mIndex;

        @Override
        protected List<Address> doInBackground(Object... params) {
            Log.d("TAG", "doInBackground: In do in background");

            String locationAddress = (String) params[0];
            mIndex = (Integer) params[1];

            GeocoderGraphHopper geocoder = new GeocoderGraphHopper(Locale.getDefault(), "fda57d87-34f0-4a12-9ca1-680cc31bf6fb");

            try {
                BoundingBox viewbox = map.getBoundingBox();
                List<Address> foundAdresses = geocoder.getFromLocationName(locationAddress, 1,
                        viewbox.getLatSouth(), viewbox.getLonEast(),
                        viewbox.getLatNorth(), viewbox.getLonWest(), false);
                Log.d("TAG", "doInBackground: " + foundAdresses);
                return foundAdresses;
            } catch (Exception e) {
                return null;
            }

        }

        @Override
        protected void onPostExecute(List<Address> foundAddresses) {
            super.onPostExecute(foundAddresses);
            Log.d("TAG", "on post execute: Post execute");

            if (foundAddresses == null) {
                Toast.makeText(getApplicationContext(), "Error GeoCoding", Toast.LENGTH_SHORT).show();
            } else if (foundAddresses.size() == 0) {
                Toast.makeText(getApplicationContext(), "Couldn't find location", Toast.LENGTH_SHORT).show();
            } else {

                Address address = foundAddresses.get(0);


//                String addressDisplayName = address.getExtras().getString("display_name");
//
//                if (mIndex == START_INDEX){
//                    startPoint = new GeoPoint(address.getLatitude(), address.getLongitude());
//                    markerStart = updateItineraryMarker(markerStart, startPoint, START_INDEX,
//                            R.string.departure, R.drawable.marker_departure, -1, addressDisplayName);
//                    map.getController().setCenter(startPoint);
//                } else if (mIndex == DEST_INDEX){
//                    destinationPoint = new GeoPoint(address.getLatitude(), address.getLongitude());
//                    markerDestination = updateItineraryMarker(markerDestination, destinationPoint, DEST_INDEX,
//                            R.string.destination, R.drawable.marker_destination, -1, addressDisplayName);
//                    map.getController().setCenter(destinationPoint);
//                }
            }

        }
    }

    public String getAddress(GeoPoint p) {
        GeocoderGraphHopper geocoder = new GeocoderGraphHopper(Locale.getDefault(), "fda57d87-34f0-4a12-9ca1-680cc31bf6fb");
        String theAddress;
        try {
            double dLatitude = p.getLatitude();
            double dLongitude = p.getLongitude();
            List<Address> addresses = geocoder.getFromLocation(dLatitude, dLongitude, 1);
            StringBuilder sb = new StringBuilder();
            if (addresses.size() > 0) {
                Address address = addresses.get(0);
                int n = address.getMaxAddressLineIndex();
                for (int i = 0; i <= n; i++) {
                    if (i != 0)
                        sb.append(", ");
                    sb.append(address.getAddressLine(i));
                }
                theAddress = sb.toString();
            } else {
                theAddress = null;
            }
        } catch (IOException e) {
            theAddress = null;
        }
        if (theAddress != null) {
            return theAddress;
        } else {
            return "";
        }
    }


    private class ReverseGeocodingTask extends AsyncTask<Marker, Void, String> {
        Marker marker;

        protected String doInBackground(Marker... params) {
            marker = params[0];
            return getAddress(marker.getPosition());
        }

        protected void onPostExecute(String result) {
            marker.setSnippet(result);
            marker.showInfoWindow();
        }
    }
    private void initMyLocation(){
        map = findViewById(R.id.map);
        map.setMultiTouchControls(true);

        map.setTilesScaledToDpi(true);
        mapController = (MapController) map.getController();
        mapController.setZoom(18);
        map.setTileSource(TileSourceFactory.MAPNIK);


        GpsMyLocationProvider gps = new GpsMyLocationProvider(ctx);
        gps.addLocationSource(LocationManager.NETWORK_PROVIDER);


        mLocationNewOverlay = new MyLocationNewOverlay(gps, map);
        mLocationNewOverlay.enableMyLocation();
        mLocationNewOverlay.enableFollowLocation();
        mLocationNewOverlay.setDrawAccuracyEnabled(true);
        map.getOverlays().add(this.mLocationNewOverlay);
        map.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.NEVER);
        map.setHorizontalMapRepetitionEnabled(false);
        map.setVerticalMapRepetitionEnabled(false);

    }


    final private int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;

    void Check_Permission() {

        List<String> permissions = new ArrayList<>();
        String message = "Application permissions:";
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
            message += "\nLocation to show user location.";
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            message += "\nStorage access to store map tiles.";
        }
        if (!permissions.isEmpty()) {
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            String[] params = permissions.toArray(new String[permissions.size()]);
            ActivityCompat.requestPermissions(this, params, REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
        }
    }


    void handleSearchButton(int index, int edit_txt) {
        EditText destination = findViewById(edit_txt);


        String destination_address = destination.getText().toString();

        if (destination_address.equals("")) {

            map.invalidate();
            return;
        }

        Toast.makeText(this, "Searching:\n" + destination_address, Toast.LENGTH_LONG).show();
//        AutoCompleteOnPreferences.storePreference(this, destination_address, SHARED_PREFS_APPKEY, PREF_LOCATIONS_KEY);
        new GeocodingTask().execute(destination_address, index);

//        getRoadAsync();
//        //get and display enclosing polygon:
//        Bundle extras = address.getExtras();
//        if (extras != null && extras.containsKey("polygonpoints")){
//            ArrayList<GeoPoint> polygon = extras.getParcelableArrayList("polygonpoints");
//            //Log.d("DEBUG", "polygon:"+polygon.size());
//            updateUIWithPolygon(polygon, addressDisplayName);
//        } else {
//            updateUIWithPolygon(null, "");
//        }


    }

}
