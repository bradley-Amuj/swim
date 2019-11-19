package com.example.user.swim;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import static com.example.user.swim.AsyncTasks.ReverseGeocodingTask.my_location;
import static com.example.user.swim.AsyncTasks.SetPath.Distance;
import static com.example.user.swim.LocationAdapter.location_display;
import static com.example.user.swim.MainActivity.TAG;
import static com.example.user.swim.MainActivity.current_geoPoint;
import static com.example.user.swim.MainActivity.destinationPoint;


public class ConfirmLocation extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private TextView distance, time, current_l, destination;
    private Button send_request_btn, cancel_request;



    public ConfirmLocation() {

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();


        View view = inflater.inflate(R.layout.fragment_confirm_location, container, false);

        distance = view.findViewById(R.id.distance);
        current_l = view.findViewById(R.id.Current_Location);
        destination = view.findViewById(R.id.Destination_location);



        destination.setText(location_display);
        current_l.setText(my_location);

        distance.setText("Distance: " + String.format("%.2f", (Distance / 1000)) + " Km");

        send_request_btn = view.findViewById(R.id.Send_Request);
        cancel_request = view.findViewById(R.id.Cancel_Request);

        sendRequest();
        CancelRequest();


        return view;
    }

    private void sendRequest() {

        send_request_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(getActivity(), "Request is being sent", Toast.LENGTH_LONG).show();

                db.collection("RideOffers")

                        .whereEqualTo("RoadNodes", current_geoPoint)
                        .whereEqualTo("RoadNodes", destinationPoint)

//                      .whereArrayContains("RoadNodes",current_geoPoint)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {

                                    if (task.getResult().size() == 0) {

                                        Toast.makeText(getActivity(), "Sorry there are no available rides at the moment", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(getActivity(), MainActivity.class));


                                    } else {

                                        for (QueryDocumentSnapshot documentSnapshots : task.getResult()) {

                                            Log.d(TAG, "EMAIL " + documentSnapshots.get("EmailAddress"));


                                        }
                                        Log.d(TAG, "Result " + task.getResult().size());


                                    }


                                } else {

                                    Toast.makeText(getActivity(), "Connection Error", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });




            }
        });
    }

    private void CancelRequest() {

        cancel_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Toast.makeText(getActivity(), "Request has been cancelled", Toast.LENGTH_LONG).show();
                startActivity(new Intent(getActivity(), MainActivity.class));
                getActivity().finish();
            }
        });
    }


}
