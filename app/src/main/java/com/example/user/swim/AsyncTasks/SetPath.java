package com.example.user.swim.AsyncTasks;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;

import com.example.user.swim.ConfirmLocation;
import com.example.user.swim.MainActivity;
import com.example.user.swim.R;

import org.osmdroid.bonuspack.location.NominatimPOIProvider;
import org.osmdroid.bonuspack.location.OverpassAPIProvider;
import org.osmdroid.bonuspack.location.POI;
import org.osmdroid.bonuspack.routing.GraphHopperRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.Polyline;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import static com.example.user.swim.MainActivity.TAG;
import static com.example.user.swim.MainActivity.context;
import static com.example.user.swim.MainActivity.current_geoPoint;
import static com.example.user.swim.MainActivity.destinationPoint;
import static com.example.user.swim.MainActivity.mRoads;
import static com.example.user.swim.MainActivity.myLocationOverlay;


public class SetPath extends AsyncTask<ArrayList<GeoPoint>, Void, Road> {
    public static Double Distance;
    private final MapView map;
    private Polyline[] mRoadOverlays;
    private Road road;

    public static ArrayList<POI> destPOI;
    NominatimPOIProvider poiProvider;
    OverpassAPIProvider overpassProvider = new OverpassAPIProvider();
    ArrayList<POI> pois = new ArrayList<>();


    public SetPath(MapView map, Polyline[] mRoadOverlays) {
        this.map = map;
        this.mRoadOverlays = mRoadOverlays;
    }

    @Override
    protected Road doInBackground(ArrayList<GeoPoint>... arrayLists) {
        ArrayList<GeoPoint> wayPoints = arrayLists[0];
        RoadManager roadManager;
        Locale locale = Locale.getDefault();
        roadManager = new GraphHopperRoadManager("fda57d87-34f0-4a12-9ca1-680cc31bf6fb", false);
        roadManager.addRequestOption("locale=" + locale.getLanguage());


        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        //poiProvider = new NominatimPOIProvider("OsmNavigator/1.0");
        poiProvider = new NominatimPOIProvider("OSMBonusPackTutoUserAgent");

        destPOI = poiProvider.getPOICloseTo(destinationPoint, "Bus station", 2, 1);

        road = roadManager.getRoad(wayPoints);

        //pois = poiProvider.getPOIAlong(road.getRouteLow(), "Bus station", 10, 5.0);

        //Log.d(TAG, "doInBackground: pois" + pois);


        return road;

    }

    @Override
    protected void onPostExecute(Road road) {
        super.onPostExecute(road);


        UpdateUI_with_Roads(road);
        put_confirm_fragment();
//
//        for (POI poi:destPOI) {
//            Log.d(TAG, "POIS NEAR ME "+ poi.mId);
//        }


        Log.d(TAG, " along POI: " + pois.size());

    }

    private void UpdateUI_with_Roads(Road road) {
        Marker endpoint = new Marker(map);
        endpoint.setPosition(destinationPoint);
        endpoint.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        endpoint.setTitle("Destination");
        List<Overlay> mapOverlays = map.getOverlays();
        if (mRoadOverlays != null) {
            for (int i = 0; i < mRoadOverlays.length; i++)
                mapOverlays.remove(mRoadOverlays[i]);
            mRoadOverlays = null;
        }

        if (road == null) {

            return;
        }
        if (road.mStatus == Road.STATUS_TECHNICAL_ISSUE) {

            Toast.makeText(map.getContext(), "Technical issue when getting the route", Toast.LENGTH_SHORT).show();
        } else if (road.mStatus > Road.STATUS_TECHNICAL_ISSUE) {
            Toast.makeText(map.getContext(), "No possible route here", Toast.LENGTH_SHORT).show();
        } else {
            Polyline roadOverlay = RoadManager.buildRoadOverlay(road, Color.BLUE, 10.0f);
            map.getOverlays().add(roadOverlay);
            map.getOverlays().add(endpoint);
            Distance = roadOverlay.getDistance();
            BoundingBox box = BoundingBox.fromGeoPoints(Arrays.asList(current_geoPoint, destinationPoint));
            map.zoomToBoundingBox(box, false);
            map.invalidate();

        }


    }

    private void UpdateUI_with_Roads(Road[] roads) {
        Marker endpoint = new Marker(map);
        endpoint.setPosition(destinationPoint);
        endpoint.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        endpoint.setTitle("Destination");

        List<Overlay> mapOverlays = map.getOverlays();
        if (mRoadOverlays != null) {
            for (int i = 0; i < mRoadOverlays.length; i++)
                mapOverlays.remove(mRoadOverlays[i]);
            mRoadOverlays = null;
        }

        if (roads == null)
            return;
        if (roads[0].mStatus == Road.STATUS_TECHNICAL_ISSUE)
            Toast.makeText(map.getContext(), "Technical issue when getting the route", Toast.LENGTH_SHORT).show();
        else if (roads[0].mStatus > Road.STATUS_TECHNICAL_ISSUE) //functional issues
            Toast.makeText(map.getContext(), "No possible route here", Toast.LENGTH_SHORT).show();
        else {
            mRoadOverlays = new Polyline[roads.length];
            for (int i = 0; i < roads.length; i++) {
                Polyline roadPolyline = RoadManager.buildRoadOverlay(roads[i], Color.BLUE, 10.0f);
                mRoadOverlays[i] = roadPolyline;
                Distance = roadPolyline.getDistance();
                mapOverlays.add(1, roadPolyline);


            }

            map.getOverlays().add(endpoint);
            BoundingBox box = BoundingBox.fromGeoPoints(Arrays.asList(current_geoPoint, destinationPoint));
            map.zoomToBoundingBox(box, false);
            map.invalidate();
        }
    }

    public void getRoadAsync() {


        mRoads = null;
        GeoPoint roadStartPoint = null;

        if (myLocationOverlay.isEnabled() && myLocationOverlay.getLocation() != null) {

            roadStartPoint = myLocationOverlay.getLocation();


        }

        if (roadStartPoint == null || destinationPoint == null) {
            UpdateUI_with_Roads(mRoads);
            return;
        }

        ArrayList<GeoPoint> waypoints = new ArrayList<GeoPoint>();
        waypoints.add(current_geoPoint);
        waypoints.add(destinationPoint);
        new SetPath(map, mRoadOverlays).execute(waypoints);

    }


    private void put_confirm_fragment() {
        ConfirmLocation confirmLocation = new ConfirmLocation();
        FragmentManager fm = ((MainActivity) context).getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fragment_place, confirmLocation, "confirm");
        ft.commit();

    }


}
