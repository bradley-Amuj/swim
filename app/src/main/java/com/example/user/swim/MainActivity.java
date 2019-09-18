package com.example.user.swim;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private MapView map;
    private MapController mapController;
    private Context ctx;
    MyLocationNewOverlay mLocationNewOverlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        setContentView(R.layout.activity_main);

        initMyLocation();
        setRide();


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

    private void setRide(){
        Button set_ride_btn = findViewById(R.id.setRide);

        set_ride_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,SetPickUp.class));
                finish();
            }
        });

    }

}
