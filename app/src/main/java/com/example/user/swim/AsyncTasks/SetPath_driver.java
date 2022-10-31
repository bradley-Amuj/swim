package com.example.user.swim.AsyncTasks;

import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.osmdroid.bonuspack.location.POI;
import org.osmdroid.bonuspack.routing.GraphHopperRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.Polyline;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static com.example.user.swim.MainActivity.TAG;
import static com.example.user.swim.MainActivity.current_geoPoint;
import static com.example.user.swim.MainActivity.destinationPoint;
import static com.example.user.swim.MainActivity.mRoads;
import static com.example.user.swim.MainActivity.startPoint;


public class SetPath_driver extends AsyncTask<ArrayList<GeoPoint>, Void, Road> {

    public static ArrayList<GeoPoint> driver_path_points;
    private final MapView map;
    private Polyline[] mRoadOverlays;

    private static Road driver_road;

    public static ArrayList<POI> driver_current_location_poi;
    public static ArrayList<POI> driver_destination_location_poi;

    public SetPath_driver(MapView map, Polyline[] mRoadOverlays) {
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
        driver_road = roadManager.getRoad(wayPoints);
        return driver_road;
    }

    @Override
    protected void onPostExecute(Road roads) {
        super.onPostExecute(roads);


        UpdateUI_with_Roads(roads);


    }

    private void UpdateUI_with_Roads(Road road) {
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

            BoundingBox box = BoundingBox.fromGeoPoints(Arrays.asList(current_geoPoint, destinationPoint));
            map.zoomToBoundingBox(box, false);
            map.invalidate();

        }


    }


    private void UpdateUI_with_Roads(Road[] roads) {
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
            Log.d(TAG, "UpdateUI_with_Roads:Road exists ");
            for (int i = 0; i < roads.length; i++) {
                Polyline roadPolyline = RoadManager.buildRoadOverlay(roads[i], Color.BLUE, 10.0f);
                mRoadOverlays[i] = roadPolyline;

                driver_path_points = roadPolyline.getPoints();

                ArrayList<GeoPoint> route = new ArrayList<>();
                route.add(current_geoPoint);
                route.add(destinationPoint);


                mapOverlays.add(1, roadPolyline);

            }
            BoundingBox box = BoundingBox.fromGeoPoints(Arrays.asList(current_geoPoint, destinationPoint));
            map.zoomToBoundingBox(box, false);


            map.invalidate();
        }
    }

    public void getRoadAsync() {

        mRoads = null;
        GeoPoint roadStartPoint = null;

//        if (myLocationOverlay.isEnabled() && myLocationOverlay.getLocation() != null) {
//
//            roadStartPoint = myLocationOverlay.getLocation();
//
//
//        }

        roadStartPoint = startPoint;

        if (roadStartPoint == null || destinationPoint == null) {
            UpdateUI_with_Roads(mRoads);
            return;
        }

        ArrayList<GeoPoint> waypoints = new ArrayList<GeoPoint>();
        waypoints.add(current_geoPoint);
        waypoints.add(destinationPoint);
        new SetPath_driver(map, mRoadOverlays).execute(waypoints);

    }
}
