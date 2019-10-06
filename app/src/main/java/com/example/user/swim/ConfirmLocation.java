package com.example.user.swim;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;


public class ConfirmLocation extends Fragment {

    private TextView distance, time, current_l, destination;
    private Button send_request_btn;



    public ConfirmLocation() {

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_confirm_location, container, false);

        distance = view.findViewById(R.id.distance);
        time = view.findViewById(R.id.time);
        current_l = view.findViewById(R.id.Current_Location);
        destination = view.findViewById(R.id.Destination_location);

        //Todo: set distance, locations and time on  the fragment

//        current_l.setText();
//        destination.setText();

        send_request_btn = view.findViewById(R.id.Send_Request);

        sendRequest();


        return view;
    }

    private void sendRequest() {

        send_request_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Toast.makeText(getActivity(), "Request is being sent", Toast.LENGTH_LONG).show();
                startActivity(new Intent(getActivity(), MainActivity.class));
            }
        });
    }


}
