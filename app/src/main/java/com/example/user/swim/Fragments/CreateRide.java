package com.example.user.swim.Fragments;

import android.location.LocationManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.swim.AsyncTasks.GeoCodingTask;
import com.example.user.swim.AsyncTasks.GetPOI;
import com.example.user.swim.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.example.user.swim.AsyncTasks.ReverseGeocodingTask.my_location;
import static com.example.user.swim.MainActivity.ctx;
import static com.example.user.swim.MainActivity.destinationPoint;

public class CreateRide extends Fragment {
    public static MapView map_driver;
    private EditText Destination;
    public static RecyclerView recyclerView_driver;
    private String destination_txt;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;
    public static Map<String, Object> rideOffers;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_ride, null);

        progressBar = view.findViewById(R.id.progressbar);
        TextView driver_current_location = view.findViewById(R.id.current_driver_location);
        recyclerView_driver = view.findViewById(R.id.recyclerView_driver_destination);
        recyclerView_driver.setLayoutManager(new LinearLayoutManager(getActivity()));
        driver_current_location.setText("Current location: " + my_location);
        rideOffers = new HashMap<>();
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();





        Button createRide = view.findViewById(R.id.create_ride);



        Destination = view.findViewById(R.id.driver_destination);


        Destination.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                recyclerView_driver.setVisibility(View.VISIBLE);

                if (actionId == EditorInfo.IME_ACTION_DONE) {

                    destination_txt = Destination.getText().toString();
                    if (!destination_txt.isEmpty()) {

                        new GeoCodingTask(recyclerView_driver, map_driver, 2).execute(destination_txt);

                    }


                }
                return false;
            }
        });


        createRide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (!TextUtils.isEmpty(destination_txt)) {
                    progressBar.setVisibility(View.VISIBLE);


                    new GetPOI(progressBar, getActivity()).execute(destinationPoint);

                    Destination.setText("");


                } else {

                    Toast.makeText(getActivity(), "Please select Destination ", Toast.LENGTH_SHORT).show();
                }


            }
        });
        initMap(view);


        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        map_driver.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        map_driver.onResume();
    }

    private void initMap(View view) {
        map_driver = view.findViewById(R.id.myMap);
        map_driver.setMultiTouchControls(true);

        map_driver.setTilesScaledToDpi(true);
        MapController mapController = (MapController) map_driver.getController();
        mapController.setZoom(18);
        map_driver.setTileSource(TileSourceFactory.MAPNIK);


        GpsMyLocationProvider gps = new GpsMyLocationProvider(ctx);
        gps.addLocationSource(LocationManager.NETWORK_PROVIDER);


//        mLocationNewOverlay = new MyLocationNewOverlay(gps, map_driver);
//
//        mLocationNewOverlay.enableMyLocation();
//        mLocationNewOverlay.enableFollowLocation();
//
//        mLocationNewOverlay.setDrawAccuracyEnabled(true);
//        map_driver.getOverlays().add(mLocationNewOverlay);
        map_driver.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.NEVER);
        map_driver.setHorizontalMapRepetitionEnabled(false);
        map_driver.setVerticalMapRepetitionEnabled(false);


    }

    public void hideRecyclerView(RecyclerView view) {

        view.setVisibility(View.GONE);


    }


}


