package com.example.user.swim.AsyncTasks;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.osmdroid.bonuspack.location.NominatimPOIProvider;
import org.osmdroid.bonuspack.location.POI;
import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;

import androidx.annotation.NonNull;

import static com.example.user.swim.AsyncTasks.ReverseGeocodingTask.my_location;
import static com.example.user.swim.Fragments.CreateRide.rideOffers;
import static com.example.user.swim.MainActivity.TAG;

public class GetPOI extends AsyncTask<GeoPoint, Void, ArrayList<POI>> {

    private final FirebaseAuth mAuth;
    private final FirebaseUser user;
    private final FirebaseFirestore db;
    private final ProgressBar progressBar;
    private final Activity activity;

    public GetPOI(ProgressBar progressBar, Activity activity) {
        this.progressBar = progressBar;
        this.activity = activity;

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
    }

    @Override
    protected ArrayList<POI> doInBackground(GeoPoint... geoPoints) {
        ArrayList<POI> POI;

        NominatimPOIProvider poiProvider = new NominatimPOIProvider("OSMBonusPackTutoUserAgent");
        POI = poiProvider.getPOICloseTo(geoPoints[0], "Bus station", 10, 3.0);


        Log.d(TAG, "doInBackground: Size of POIs " + POI.size());

        return POI;
    }

    @Override
    protected void onPostExecute(ArrayList<POI> pois) {
        super.onPostExecute(pois);

        ArrayList<Long> IDs = new ArrayList<>();

        for (POI poi : pois) {
            IDs.add(poi.mId);
        }


        rideOffers.put("EmailAddress", user.getEmail());
        rideOffers.put("CurrentLocation", my_location);
        rideOffers.put("POI", IDs);

        db.collection("RideOffers")
                .add(rideOffers)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(progressBar.getContext(), "Ride Created Successfully", Toast.LENGTH_SHORT).show();

//                        activity.getFragmentManager().beginTransaction().replace(R.id.fragment_container,new Notifications()).commit();
//                        activity.getApplication().get.beginTransaction().replace(R.id.fragment_container,new Notifications()).commit();
                        Log.d(TAG, "onSuccess: DATA SAVED SUCCESSFULLY");


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFAIL: DATA NOT SAVED SUCCESSFULLY");
                        Toast.makeText(progressBar.getContext(), "Error in creating ride", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
