package com.example.user.swim.AsyncTasks;

import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.user.swim.ConfirmLocation;
import com.example.user.swim.MainActivity;
import com.example.user.swim.R;

import org.osmdroid.bonuspack.routing.GraphHopperRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.Polyline;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import static com.example.user.swim.MainActivity.TAG;
import static com.example.user.swim.MainActivity.context;
import static com.example.user.swim.MainActivity.current_geoPoint;
import static com.example.user.swim.MainActivity.destinationPoint;
import static com.example.user.swim.MainActivity.mLocationNewOverlay;
import static com.example.user.swim.MainActivity.mRoadOverlays;
import static com.example.user.swim.MainActivity.mRoads;
import static com.example.user.swim.MainActivity.map;


public class SetPath extends AsyncTask<ArrayList<GeoPoint>, Void, Road[]> {
    public static Double Distance;

    public static int time;


    @Override
    protected Road[] doInBackground(ArrayList<GeoPoint>... arrayLists) {
        ArrayList<GeoPoint> wayPoints = arrayLists[0];
        RoadManager roadManager;
        Locale locale = Locale.getDefault();
        roadManager = new GraphHopperRoadManager("fda57d87-34f0-4a12-9ca1-680cc31bf6fb", false);
        roadManager.addRequestOption("locale=" + locale.getLanguage());

        return roadManager.getRoads(wayPoints);
    }

    @Override
    protected void onPostExecute(Road[] roads) {
        super.onPostExecute(roads);
        UpdateUI_with_Roads(roads);
        put_confirm_fragment();

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

                Log.d(TAG, "ROAD POLYPOINTS " + roadPolyline.getPoints() + "SIZE " + roadPolyline.getPoints().size());
                Distance = roadPolyline.getDistance();
                mapOverlays.add(1, roadPolyline);

            }

            map.invalidate();
        }
    }

    public void getRoadAsync() {

        mRoads = null;
        GeoPoint roadStartPoint = null;

        if (mLocationNewOverlay.isEnabled() && mLocationNewOverlay.getMyLocation() != null) {

            roadStartPoint = mLocationNewOverlay.getMyLocation();


        }

        if (roadStartPoint == null || destinationPoint == null) {
            UpdateUI_with_Roads(mRoads);
            return;
        }

        ArrayList<GeoPoint> waypoints = new ArrayList<GeoPoint>();
        waypoints.add(current_geoPoint);
        waypoints.add(destinationPoint);
        new SetPath().execute(waypoints);

    }


    private void put_confirm_fragment() {
        ConfirmLocation confirmLocation = new ConfirmLocation();
        FragmentManager fm = ((MainActivity) context).getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fragment_place, confirmLocation, "confirm");
        ft.commit();

    }


}
