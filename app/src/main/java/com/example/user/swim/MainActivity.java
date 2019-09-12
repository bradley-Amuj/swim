package com.example.user.swim;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;


import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;

import org.osmdroid.tileprovider.tilesource.XYTileSource;
import org.osmdroid.util.MapTileIndex;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;


import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private MapView map;
    private MapController mapController;
    private Context ctx;
    private Location currentLocation;

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
        map = (MapView) findViewById(R.id.map);

        map.setMultiTouchControls(true);


        map = (MapView) findViewById(R.id.map);
        mapController = (MapController) map.getController();
        mapController.setZoom(12);
        map.setTileSource(TileSourceFactory.MAPNIK);

//        map.setTileSource(new OnlineTileSourceBase("USGS Topo", 0, 18, 256, "",
//                new String[] { "http://basemap.nationalmap.gov/ArcGIS/rest/services/USGSTopo/MapServer/tile/" }) {
//            @Override
//            public String getTileURLString(long pMapTileIndex) {
//                return getBaseUrl()
//                        + MapTileIndex.getZoom(pMapTileIndex)
//                        + "/" + MapTileIndex.getY(pMapTileIndex)
//                        + "/" + MapTileIndex.getX(pMapTileIndex)
//                        + mImageFilenameEnding;
//            }
//        });



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

    private void setRide(){
        Button set_ride_btn = (Button)findViewById(R.id.setRide);

        set_ride_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,SetPickUp.class));
                finish();
            }
        });

    }

}
