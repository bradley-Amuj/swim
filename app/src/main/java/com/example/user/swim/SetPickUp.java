package com.example.user.swim;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;

import org.osmdroid.config.Configuration;
import org.osmdroid.events.DelayedMapListener;
import org.osmdroid.events.MapListener;
import org.osmdroid.events.ScrollEvent;
import org.osmdroid.events.ZoomEvent;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

public class SetPickUp extends Activity {
    private MapView map;
    private Button Done_btn;
    private MapController mapController;
    private Context ctx;
    private MyLocationNewOverlay mLocationNewOverlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_pick_up);

        map = findViewById(R.id.mapView);
        Done_btn = findViewById(R.id.done_btn);

        ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        map.setMultiTouchControls(true);

        mapController = (MapController) map.getController();
        mapController.setZoom(18);
        map.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.NEVER);
        map.setTileSource(TileSourceFactory.MAPNIK);
        mLocationNewOverlay= new MyLocationNewOverlay(map);
        mLocationNewOverlay.enableMyLocation();
        mLocationNewOverlay.enableFollowLocation();
        mLocationNewOverlay.setDrawAccuracyEnabled(true);
        map.getOverlays().add(this.mLocationNewOverlay);

        map.setHorizontalMapRepetitionEnabled(false);
        map.setVerticalMapRepetitionEnabled(false);

        hide_btn_onScroll();
    }

    private void hide_btn_onScroll() {
        map.addMapListener(new DelayedMapListener(new MapListener() {
            @Override
            public boolean onScroll(ScrollEvent event) {


                Done_btn.setVisibility(View.VISIBLE);
                return false;
            }

            @Override
            public boolean onZoom(ZoomEvent event) {
                return false;
            }
        }, 1000));

    }
}

