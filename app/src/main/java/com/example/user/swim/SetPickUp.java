package com.example.user.swim;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceManager;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

public class SetPickUp extends Activity {
    private MapView map;
    private MapController mapController;
    private Context ctx;
    private MyLocationNewOverlay mLocationNewOverlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_pick_up);

        map = (MapView)findViewById(R.id.mapView);
        ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        map.setMultiTouchControls(true);

        mapController = (MapController) map.getController();
        mapController.setZoom(12);
        map.setTileSource(TileSourceFactory.MAPNIK);
        mLocationNewOverlay= new MyLocationNewOverlay(map);
        mLocationNewOverlay.enableMyLocation();
        mLocationNewOverlay.enableFollowLocation();
        mLocationNewOverlay.setDrawAccuracyEnabled(true);
        map.getOverlays().add(this.mLocationNewOverlay);

        mLocationNewOverlay.runOnFirstFix(new Runnable() {
            @Override
            public void run() {
                mapController.animateTo(mLocationNewOverlay.getMyLocation());
            }
        });

    }
    }

