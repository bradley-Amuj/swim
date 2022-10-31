package com.example.user.swim;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.example.user.swim.ActionListeners.DoneOnEditorActionListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.FolderOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends AppCompatActivity implements MapEventsReceiver {
    private FirebaseAuth mAuth;
    public static GeoPoint startPoint;
    public static GeoPoint destinationPoint;
    protected FolderOverlay mRoadNodeMarkers;

    public static Road[] mRoads;

    public static GeoPoint current_geoPoint;

    public static Polyline[] mRoadOverlays;
    public static String TAG = "Debugging";
    protected static int START_INDEX = -2, DEST_INDEX = -1;
    protected Marker markerStart, markerDestination;

    //Location Objects
    private FusedLocationProviderClient fusedLocationProviderClient;
    protected LocationManager mLocationManager;


    public static MapView map;
    private MapController mapController;
    public static Context ctx;
    public static Context context;
//    public static DirectedLocationOverlay myLocationOverlay;


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if (findViewById(R.id.fragment_place) != null) {
            if (savedInstanceState != null) {
                return;
            }

            SearchLocation fragment = new SearchLocation();
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.add(R.id.fragment_place, fragment, "Search_fragment");
            ft.addToBackStack(null);
            ft.commit();

        }

        ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        context = this;

        Check_Permission();
        initMyLocation();

        if (savedInstanceState == null) {

            mAuth = FirebaseAuth.getInstance();
            Location myLocation = null;

//            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//
//                myLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//
//                if (myLocation == null) {
//
//                    myLocation = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//                }
//
//                if (myLocation != null) {
//
//                    onLocationChanged(myLocation);
//                } else {
//
//                    // no known location has been found hence disable my current location
//                    myLocationOverlay.setEnabled(false);
//                }
//
//            }
//        } else {
//
//            myLocationOverlay.setLocation((GeoPoint) savedInstanceState.getParcelable("myLocation"));


        }


//        HandleDoneActionKeyboard(R.id.destination);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.passenger_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.switch_mode:
                Toast.makeText(this, " Switching to driver", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, DriverSide.class));
                finish();

                break;

            case R.id.logout:
                Toast.makeText(this, "We are logging you out", Toast.LENGTH_SHORT).show();
                mAuth.signOut();
                startActivity(new Intent(MainActivity.this, log_in.class));
                finish();
                break;


        }
        return super.onOptionsItemSelected(item);


    }

    private void HandleDoneActionKeyboard(int keyID) {
        EditText keyboard = findViewById(keyID);
        keyboard.setOnEditorActionListener(new DoneOnEditorActionListener());


    } // <-Closes the keyboard when done button is clicked


    private void initMyLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
//        myLocationOverlay = new DirectedLocationOverlay(this);
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {


                    map = findViewById(R.id.map);
                    map.setMultiTouchControls(true);
                    map.setMinZoomLevel(1.0);
                    mapController = (MapController) map.getController();
                    mapController.setZoom(20);
                    map.setTileSource(TileSourceFactory.MAPNIK);
                    map.setHorizontalMapRepetitionEnabled(false);
                    map.setVerticalMapRepetitionEnabled(false);

                    startPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
//                    myLocationOverlay.setLocation(startPoint);
//                    map.getOverlays().add(myLocationOverlay);
//                    map.getController().animateTo(startPoint);
                    Marker startMarker = new Marker(map);
                    startMarker.setPosition(startPoint);
                    Drawable my_location_icon = ResourcesCompat.getDrawable(getResources(), R.drawable.baseline_my_location_black_48, null);
                    startMarker.setTextIcon("Current location");
                    startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
                    startMarker.setIcon(my_location_icon);
                    map.getOverlays().add(startMarker);
                    map.getController().animateTo(startPoint);


                    Log.d(TAG, "onSuccess: This is my current location " + location);
                }

            }
        }).addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, "Failed to get current location", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onFailure: Failed to get current location " + e);
            }
        });

    }


    final private int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;
    void Check_Permission() {

        List<String> permissions = new ArrayList<>();
        String message = "Application permissions:";
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

//            mLocationNewOverlay.enableMyLocation();
//            mLocationNewOverlay.enableFollowLocation();
        } else {

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

    // callback to store activity status before a restart
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        // outState.putParcelable("myLocation", myLocationOverlay.getLocation());


    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {

            case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS: {
                Map<String, Integer> perms = new HashMap<>();
                // Initial
                perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                // Fill with results
                for (int i = 0; i < permissions.length; i++)
                    perms.put(permissions[i], grantResults[i]);
                // Check for WRITE_EXTERNAL_STORAGE
                Boolean storage = perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
                if (!storage) {
                    // Permission Denied
                    Toast.makeText(this, "Storage permission is required to store map tiles to reduce data usage and for offline usage.", Toast.LENGTH_LONG).show();
                } // else: permission was granted, yay!
            }

        }
    }

    @Override
    public boolean singleTapConfirmedHelper(GeoPoint p) {
        return false;
    }

    @Override
    public boolean longPressHelper(GeoPoint p) {
        return false;
    }



}


