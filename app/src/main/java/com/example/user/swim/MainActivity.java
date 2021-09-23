package com.example.user.swim;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.example.user.swim.ActionListeners.DoneOnEditorActionListener;
import com.example.user.swim.AsyncTasks.ReverseGeocodingTask;
import com.google.firebase.auth.FirebaseAuth;

import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.util.NetworkLocationIgnorer;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.FolderOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.mylocation.DirectedLocationOverlay;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

//Todo: fix bounding box


public class MainActivity extends AppCompatActivity implements LocationListener, MapEventsReceiver, SensorEventListener {
    private FirebaseAuth mAuth;
    protected GeoPoint startPoint;
    public static GeoPoint destinationPoint;
    protected FolderOverlay mRoadNodeMarkers;

    public static Road[] mRoads;

    public static GeoPoint current_geoPoint;

    public static Polyline[] mRoadOverlays;
    public static String TAG = "Debugging";
    protected static int START_INDEX = -2, DEST_INDEX = -1;
    protected Marker markerStart, markerDestination;

    protected LocationManager mLocationManager;

    public static MapView map;
    private MapController mapController;
    public static Context ctx;
    public static Context context;
    public static DirectedLocationOverlay myLocationOverlay;


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                myLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                if (myLocation == null) {

                    myLocation = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                }

                if (myLocation != null) {

                    onLocationChanged(myLocation);
                } else {

                    // no known location has been found hence disable my current location
                    myLocationOverlay.setEnabled(false);
                }

            }
        } else {

            myLocationOverlay.setLocation((GeoPoint) savedInstanceState.getParcelable("myLocation"));


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


    //Todo: Load map in background as splashscreen loads
    private void initMyLocation() {
        map = findViewById(R.id.map);
        map.setMultiTouchControls(true);
        map.setMinZoomLevel(1.0);
        map.setTilesScaledToDpi(true);
        mapController = (MapController) map.getController();
        mapController.setZoom(18);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setHorizontalMapRepetitionEnabled(false);
        map.setVerticalMapRepetitionEnabled(false);
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        myLocationOverlay = new DirectedLocationOverlay(this);
        map.getOverlays().add(myLocationOverlay);

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

        outState.putParcelable("myLocation", myLocationOverlay.getLocation());


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

    private final NetworkLocationIgnorer mIgnorer = new NetworkLocationIgnorer();
    long mLastTime = 0; // milliseconds
    double mSpeed = 0.0; // km/h

    @Override
    public void onLocationChanged(Location location) {

        long currentTime = System.currentTimeMillis();
        if (mIgnorer.shouldIgnore(location.getProvider(), currentTime))
            return;
        double dT = currentTime - mLastTime;
        if (dT < 100.0) {

            return;
        }
        mLastTime = currentTime;

        GeoPoint newLocation = new GeoPoint(location);
        if (!myLocationOverlay.isEnabled()) {
            //we get the location for the first time:
            myLocationOverlay.setEnabled(true);
            map.getController().animateTo(newLocation);
        }

        GeoPoint prevLocation = myLocationOverlay.getLocation();
        myLocationOverlay.setLocation(newLocation);
        myLocationOverlay.setAccuracy((int) location.getAccuracy());
        map.getController().animateTo(newLocation);

        current_geoPoint = newLocation;
        new ReverseGeocodingTask().execute(current_geoPoint);

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }


    @Override
    public boolean singleTapConfirmedHelper(GeoPoint p) {
        return false;
    }

    @Override
    public boolean longPressHelper(GeoPoint p) {
        return false;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        myLocationOverlay.setAccuracy(accuracy);
        map.invalidate();
    }
}


