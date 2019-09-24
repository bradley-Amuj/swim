package com.example.user.swim;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.swim.ActionListeners.DoneOnEditorActionListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import org.osmdroid.bonuspack.location.GeocoderGraphHopper;
import org.osmdroid.bonuspack.routing.GraphHopperRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.FolderOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.example.user.swim.MainActivity.TAG;


public class MainActivity extends AppCompatActivity {
    protected GeoPoint startPoint;
    protected GeoPoint destinationPoint;
    protected FolderOverlay mRoadNodeMarkers;

    public static Road[] mRoads;

    protected Polyline[] mRoadOverlays;
    static String TAG = "Debugging";
    protected static int START_INDEX = -2, DEST_INDEX = -1;
    protected Marker markerStart, markerDestination;


    protected MapView map;
    private MapController mapController;
    private Context ctx;
    MyLocationNewOverlay mLocationNewOverlay;

    private BottomSheetBehavior sheetBehavior;
    private LinearLayout bottom_sheet;
    private Button search_btn;
    private EditText destination;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);


        ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        setContentView(R.layout.activity_main);
        HandleDoneActionKeyboard(R.id.destination);
        Check_Permission();
        initMyLocation();
        Set_up_RecyclerView();







        search_btn = findViewById(R.id.search_destination);
        bottom_sheet = findViewById(R.id.bottom_sheet);
        sheetBehavior = BottomSheetBehavior.from(bottom_sheet);

        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                handleSearchButton(R.id.destination);

            }
        });

        sheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED: {

                    }
                    break;
                    case BottomSheetBehavior.STATE_COLLAPSED: {

                    }
                    break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

    }

    private void HandleDoneActionKeyboard(int keyID) {
        EditText keyboard = findViewById(keyID);
        keyboard.setOnEditorActionListener(new DoneOnEditorActionListener());

        Log.d(TAG, "HandleDoneActionKeyboard:Inside Method");


    } // <-Closes the keyboard when done button is clicked
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private void Set_up_RecyclerView() {

        recyclerView = findViewById(R.id.recyclerView);

        layoutManager = new LinearLayoutManager(this);

        recyclerView.setLayoutManager(layoutManager);


    }




    private void PopulateSuggestions() {
        search_btn = findViewById(R.id.search_destination);

        destination = findViewById(R.id.destination);
        destination.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


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

    static int results = 7;

    private class GeocodingTask extends AsyncTask<Object, Void, List<Address>> {
        @Override
        protected List<Address> doInBackground(Object... params) {
            String locationAddress = (String) params[0];
            GeocoderGraphHopper geocoder = new GeocoderGraphHopper(Locale.getDefault(), "fda57d87-34f0-4a12-9ca1-680cc31bf6fb");

            try {
                BoundingBox viewbox = map.getBoundingBox();
                List<Address> foundAdresses = geocoder.getFromLocationName(locationAddress, results,
                        viewbox.getLatSouth(), viewbox.getLonEast(),
                        viewbox.getLatNorth(), viewbox.getLonWest(), false);
                return foundAdresses;
            } catch (Exception e) {
                return null;
            }

        }

        @Override
        protected void onPostExecute(List<Address> foundAddresses) {
            super.onPostExecute(foundAddresses);
            if (foundAddresses == null) {
                Toast.makeText(getApplicationContext(), "Error GeoCoding", Toast.LENGTH_SHORT).show();
            } else if (foundAddresses.size() == 0) {
                Toast.makeText(getApplicationContext(), "Couldn't find location", Toast.LENGTH_SHORT).show();
            } else {
                Address address = foundAddresses.get(0);

                destinationPoint = new GeoPoint(address.getLatitude(), address.getLongitude());


                LocationAdapter adapter = new LocationAdapter(new Location(foundAddresses).createLocationList(results));
                recyclerView.setHasFixedSize(true);
                recyclerView.setAdapter(adapter);

//                getRoadAsync();
                Log.d(TAG, "onPostExecute: Destination POINT " + destinationPoint);





//                String addressDisplayName = address.getExtras().getString("display_name");
//
//                if (mIndex == START_INDEX){
//                    startPoint = new GeoPoint(address.getLatitude(), address.getLongitude());
//                    markerStart = updateItineraryMarker(markerStart, startPoint, START_INDEX,
//                            R.string.departure, R.drawable.marker_departure, -1, addressDisplayName);
//                    map.getController().setCenter(startPoint);
//                } else if (mIndex == DEST_INDEX){
//                    destinationPoint = new GeoPoint(address.getLatitude(), address.getLongitude());
//                    markerDestination = updateItineraryMarker(markerDestination, destinationPoint, DEST_INDEX,
//                            R.string.destination, R.drawable.marker_destination, -1, addressDisplayName);
//                    map.getController().setCenter(destinationPoint);
//                }
            }

        }
    }


    public String getAddress(GeoPoint p) {
        GeocoderGraphHopper geocoder = new GeocoderGraphHopper(Locale.getDefault(), "fda57d87-34f0-4a12-9ca1-680cc31bf6fb");
        String theAddress;
        try {
            double dLatitude = p.getLatitude();
            double dLongitude = p.getLongitude();
            List<Address> addresses = geocoder.getFromLocation(dLatitude, dLongitude, 1);
            StringBuilder sb = new StringBuilder();
            if (addresses.size() > 0) {
                Address address = addresses.get(0);
                int n = address.getMaxAddressLineIndex();
                for (int i = 0; i <= n; i++) {
                    if (i != 0)
                        sb.append(", ");
                    sb.append(address.getAddressLine(i));
                }
                theAddress = sb.toString();
            } else {
                theAddress = null;
            }
        } catch (IOException e) {
            theAddress = null;
        }
        if (theAddress != null) {
            return theAddress;
        } else {
            return "";
        }
    }


    private class ReverseGeocodingTask extends AsyncTask<Marker, Void, String> {
        Marker marker;

        protected String doInBackground(Marker... params) {
            marker = params[0];
            return getAddress(marker.getPosition());
        }

        protected void onPostExecute(String result) {
            marker.setSnippet(result);
            marker.showInfoWindow();
        }
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


    final private int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;

    void Check_Permission() {

        List<String> permissions = new ArrayList<>();
        String message = "Application permissions:";
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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

    void handleSearchButton(int edit_txt) {
        EditText destination = findViewById(edit_txt);
        String destination_address = destination.getText().toString();

        if (destination_address.isEmpty()) {


            Toast.makeText(this, "Please enter a location", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, "Searching:\n" + destination_address, Toast.LENGTH_LONG).show();
//        AutoCompleteOnPreferences.storePreference(this, destination_address, SHARED_PREFS_APPKEY, PREF_LOCATIONS_KEY);
        new GeocodingTask().execute(destination_address);

//        //get and display enclosing polygon:
//        Bundle extras = address.getExtras();
//        if (extras != null && extras.containsKey("polygonpoints")){
//            ArrayList<GeoPoint> polygon = extras.getParcelableArrayList("polygonpoints");
//            //Log.d("DEBUG", "polygon:"+polygon.size());
//            updateUIWithPolygon(polygon, addressDisplayName);
//        } else {
//            updateUIWithPolygon(null, "");
//        }


    }

    private class UpdateRoadTask extends AsyncTask<ArrayList<GeoPoint>, Void, Road[]> { //<---- Way points between start and destination
        private final Context mContext;

        public UpdateRoadTask(Context context) {
            this.mContext = context;
        }


        @Override
        protected Road[] doInBackground(ArrayList<GeoPoint>... params) {

            ArrayList<GeoPoint> waypoints = params[0];
            RoadManager roadManager;

            Locale locale = Locale.getDefault();
            roadManager = new GraphHopperRoadManager("fda57d87-34f0-4a12-9ca1-680cc31bf6fb", false);
            roadManager.addRequestOption("locale=" + locale.getLanguage());

            return roadManager.getRoads(waypoints);


        }

        @Override
        protected void onPostExecute(Road[] results) {
            super.onPostExecute(results);
            mRoads = results;

            Log.d(TAG, "onPostExecute: Roads size " + mRoads.length);
            updateUIWithRoads(mRoads);


        }
    }

    void updateUIWithRoads(Road[] roads) {
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

//            roadPolyline.setInfoWindow(new BasicInfoWindow(org.osmdroid.bonuspack.R.layout.bonuspack_bubble, map));
//            roadPolyline.setRelatedObject(i);
//            roadPolyline.setOnClickListener(new RoadOnClickListener());
                mapOverlays.add(1, roadPolyline);
                //we insert the road overlays at the "bottom", just above the MapEventsOverlay,
                //to avoid covering the other overlays.
            }

            map.invalidate();
        }

    }

//    void selectRoad(int roadIndex){
//        mSelectedRoad = roadIndex;
//        putRoadNodes(mRoads[roadIndex]);
//        //Set route info in the text view:
//        for (int i=0; i<mRoadOverlays.length; i++){
//            Paint p = mRoadOverlays[i].getPaint();
//            if (i == roadIndex)
//                p.setColor(0x800000FF); //blue
//            else
//                p.setColor(0x90666666); //grey
//        }
//        map.invalidate();
//    }


    public void getRoadAsync() {

        mRoads = null;
        GeoPoint roadStartPoint = null;

        if (mLocationNewOverlay.isEnabled() && mLocationNewOverlay.getMyLocation() != null) {

            roadStartPoint = mLocationNewOverlay.getMyLocation();
            Log.d(TAG, "getRoadAsync: startPoint m2 " + roadStartPoint);

        }

        if (roadStartPoint == null || destinationPoint == null) {
            updateUIWithRoads(mRoads);
            return;
        }

        ArrayList<GeoPoint> waypoints = new ArrayList<GeoPoint>();
        waypoints.add(roadStartPoint);
        waypoints.add(destinationPoint);
        new UpdateRoadTask(this).execute(waypoints);
    }


}

class Location {


    private List<Address> addresses;

    public Location(List<Address> addresses) {
        this.addresses = addresses;
    }


    public ArrayList<String> createLocationList(int size) {
        ArrayList<String> locations = new ArrayList<String>();

        for (int i = 0; i < size; i++) {
            String address = addresses.get(i).getExtras().getString("display_name");
            locations.add(address);
        }

        return locations;
    }
}

class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.ViewHolder> {

    private List<String> locations;

    public LocationAdapter(List<String> locations) {
        this.locations = locations;


        for (String l : locations) {
            Log.d(TAG, "Location " + l + "\n");
        }


    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View locationView = inflater.inflate(R.layout.location_item, parent, false);

        ViewHolder viewHolder = new ViewHolder(locationView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        TextView name = holder.display_name;
        name.setText(locations.get(position));


    }

    @Override
    public int getItemCount() {
        Log.d(TAG, "getItemCount: " + locations.size());
        return locations.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {


        TextView display_name;

        public ViewHolder(View itemView) {
            super(itemView);
            display_name = itemView.findViewById(R.id.display_name_txt);


        }
    }


}


