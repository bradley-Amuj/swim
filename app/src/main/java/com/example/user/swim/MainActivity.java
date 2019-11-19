package com.example.user.swim;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.user.swim.ActionListeners.DoneOnEditorActionListener;
import com.example.user.swim.AsyncTasks.ReverseGeocodingTask;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.FolderOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

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

//Todo: make appbar transparent




public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener authListener;



    protected GeoPoint startPoint;
    public static GeoPoint destinationPoint;
    protected FolderOverlay mRoadNodeMarkers;

    public static Road[] mRoads;

    public static GeoPoint current_geoPoint;

    public static Polyline[] mRoadOverlays;
    public static String TAG = "Debugging";
    protected static int START_INDEX = -2, DEST_INDEX = -1;
    protected Marker markerStart, markerDestination;


    public static MapView map;
    private MapController mapController;
    public static Context ctx;
    public static Context context;
    public static MyLocationNewOverlay mLocationNewOverlay;


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();

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


//        HandleDoneActionKeyboard(R.id.destination);
        Check_Permission();


        initMyLocation();

        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {


                    Log.d(TAG, "LOGGED OUT ");
                }
            }
        };









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
        map.getOverlays().add(mLocationNewOverlay);
        map.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.NEVER);
        map.setHorizontalMapRepetitionEnabled(false);
        map.setVerticalMapRepetitionEnabled(false);


        map.addOnFirstLayoutListener(new MapView.OnFirstLayoutListener() {
            @Override
            public void onFirstLayout(View v, int left, int top, int right, int bottom) {

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        current_geoPoint = mLocationNewOverlay.getMyLocation();
                        Log.d(TAG, "current geopint: " + current_geoPoint);

                        new ReverseGeocodingTask().execute(current_geoPoint);
                    }
                }, 5000);


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




}


