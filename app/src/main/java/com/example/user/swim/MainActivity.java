package com.example.user.swim;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import com.example.user.swim.AsyncTasks.SetPath;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import org.osmdroid.bonuspack.location.GeocoderGraphHopper;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.FolderOverlay;
import org.osmdroid.views.overlay.Marker;
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
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.example.user.swim.MainActivity.TAG;
import static com.example.user.swim.MainActivity.current_geoPoint;


public class MainActivity extends AppCompatActivity {
    protected GeoPoint startPoint;
    public static GeoPoint destinationPoint;
    protected FolderOverlay mRoadNodeMarkers;

    public static Road[] mRoads;

    public static GeoPoint current_geoPoint;

    public static Polyline[] mRoadOverlays;
    static String TAG = "Debugging";
    protected static int START_INDEX = -2, DEST_INDEX = -1;
    protected Marker markerStart, markerDestination;


    public static MapView map;
    private MapController mapController;
    static Context ctx;
    public static MyLocationNewOverlay mLocationNewOverlay;

    private BottomSheetBehavior sheetBehavior, confirmbehavior;
    public LinearLayout locationList_bottomsheet;
    public LinearLayout confirmLocation_bottomsheet;
    private Button search_btn;
    public static Button send_request;
    private EditText destination;

    public static TextView distance, current, final_destination;





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

        locationList_bottomsheet = findViewById(R.id.bottom_sheet);


        send_request = findViewById(R.id.Send_Request);

        sheetBehavior = BottomSheetBehavior.from(locationList_bottomsheet);
//        confirmbehavior = BottomSheetBehavior.from(confirmLocation_bottomsheet);

//        distance = findViewById(R.id.distance);
//        current = findViewById(R.id.Current_Location);
//        final_destination = findViewById(R.id.Destination_location);
//
//


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

//        confirmbehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
//            @Override
//            public void onStateChanged(@NonNull View bottomSheet, int newState) {
//                switch (newState) {
//                    case BottomSheetBehavior.STATE_HIDDEN:
//                        break;
//                    case BottomSheetBehavior.STATE_EXPANDED: {
//
//                    }
//                    break;
//                    case BottomSheetBehavior.STATE_COLLAPSED: {
//
//                    }
//                    break;
//                    case BottomSheetBehavior.STATE_DRAGGING:
//                        break;
//                    case BottomSheetBehavior.STATE_SETTLING:
//                        break;
//                }
//            }
//
//            @Override
//            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
//
//            }
//        });

    }


    private void createBottomSheet(double distance, String current, String destination) {

        TextView dist, current_txt, destination_txt;
        dist = findViewById(R.id.distance);
        current_txt = findViewById(R.id.Current_Location);
        destination_txt = findViewById(R.id.Destination_location);

        dist.setText(distance + " Km");
        current_txt.setText(current);
        destination_txt.setText(destination);


        locationList_bottomsheet.setVisibility(View.GONE);
        confirmLocation_bottomsheet = findViewById(R.id.confirm_bottomSheet);


    }

    private void HandleDoneActionKeyboard(int keyID) {
        EditText keyboard = findViewById(keyID);
        keyboard.setOnEditorActionListener(new DoneOnEditorActionListener());




    } // <-Closes the keyboard when done button is clicked
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private void Set_up_RecyclerView() {

        recyclerView = findViewById(R.id.recyclerView);

        layoutManager = new LinearLayoutManager(this);

        recyclerView.setLayoutManager(layoutManager);


    }


    //    private void progressBar(int id){
//        ProgressBar progressBar = new ProgressBar(this);
//        progressBar.setVisibility(View.GONE);
//
//
//    }
    private void clear_edt(int id) {
        EditText text = findViewById(id);

        text.setText("");
    }


    public void load_details_confirm_location(int currentLocation, int destination, int distance, String myLocation, String final_destination) {

        TextView current_Location = findViewById(currentLocation);
        current_Location.setText(myLocation);

        TextView dest = findViewById(destination);
        dest.setText(final_destination);




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

    public class GeocodingTask extends AsyncTask<Object, Integer, List<Address>> {
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
                Toast.makeText(ctx.getApplicationContext(), "Error GeoCoding", Toast.LENGTH_SHORT).show();
            } else if (foundAddresses.size() == 0) {
                Toast.makeText(ctx.getApplicationContext(), "Couldn't find location", Toast.LENGTH_SHORT).show();
            } else {
                Address address = foundAddresses.get(0);

                destinationPoint = new GeoPoint(address.getLatitude(), address.getLongitude());
//                destination_geoPoint = destinationPoint;


                LocationAdapter adapter = new LocationAdapter(new Location(foundAddresses).createList(results), getSupportFragmentManager());
                recyclerView.setHasFixedSize(true);
                recyclerView.setAdapter(adapter);


//                Log.d(TAG, "Display Name"+ address.getExtras().getString("display_name"));
//                Log.d(TAG, "Display Name: Class"+ address.getExtras().getString("display_name").getClass());
//                Log.d(TAG, "Extras"+ address.getExtras());




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

//        @Override
//        protected void onProgressUpdate(Integer... values) {
//            super.onProgressUpdate(values);
//
//            progressBar.setProgress(values[0]);
//
//        }
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
        map.getOverlays().add(mLocationNewOverlay);
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

    }


}


class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.ViewHolder> {

    private List<Location> locations;
    private FragmentManager manager;


    public LocationAdapter(List<Location> locations, FragmentManager manager) {
        this.locations = locations;
        this.manager = manager;

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
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        TextView name = holder.display_name;
        name.setText(locations.get(position).getDisplay_name());


        holder.parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ArrayList<GeoPoint> wayPoints = new ArrayList<>();
                wayPoints.add(current_geoPoint);

                Log.d(TAG, "Current Location " + current_geoPoint);
                Log.d(TAG, "Destination " + locations.get(position).getPoint());
                wayPoints.add(locations.get(position).getPoint());

                new SetPath().getRoadAsync();

                BottomSheet bottomSheet = new BottomSheet();
                bottomSheet.show(manager, "Dialog");

//
//                Fragment fragment = new ConfirmLocation();
//                FragmentManager fm = manager;
//                FragmentTransaction ft = fm.beginTransaction();
//                ft.replace(R.id.fragment_place,fragment);
//                ft.commit();


            }
        });





    }


    @Override
    public int getItemCount() {

        return locations.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        final Context context;

        TextView display_name;
        LinearLayout parent;

        public ViewHolder(View itemView) {
            super(itemView);
            context = itemView.getContext();
            display_name = itemView.findViewById(R.id.display_name_txt);
            parent = itemView.findViewById(R.id.parentLayout);



        }


    }


}


