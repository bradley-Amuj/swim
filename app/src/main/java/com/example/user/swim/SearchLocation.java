package com.example.user.swim;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.user.swim.AsyncTasks.GeoCodingTask;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class SearchLocation extends Fragment {
    private EditText destination;
    private Button search;
    private RecyclerView recyclerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_search_locatiom, null);

        search = view.findViewById(R.id.search_destination);
        destination = view.findViewById(R.id.destination);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        searchLocation(view);


        return view;
    }


    private void searchLocation(View view) {

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String destination_address = destination.getText().toString();
                if (destination_address.isEmpty()) {


                    Toast.makeText(getActivity(), "Please enter a location", Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(getActivity(), "Searching:\n" + destination_address, Toast.LENGTH_LONG).show();

                new GeoCodingTask(recyclerView).execute(destination_address);


            }
        });


    }


}
