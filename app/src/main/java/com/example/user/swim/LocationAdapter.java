package com.example.user.swim;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.swim.AsyncTasks.ReverseGeocodingTask;
import com.example.user.swim.AsyncTasks.SetPath;
import com.example.user.swim.AsyncTasks.SetPath_driver;
import com.example.user.swim.Fragments.CreateRide;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import static com.example.user.swim.Fragments.CreateRide.map_driver;
import static com.example.user.swim.Fragments.CreateRide.recyclerView_driver;
import static com.example.user.swim.MainActivity.TAG;
import static com.example.user.swim.MainActivity.current_geoPoint;
import static com.example.user.swim.MainActivity.destinationPoint;
import static com.example.user.swim.MainActivity.mRoadOverlays;
import static com.example.user.swim.MainActivity.map;
import static com.example.user.swim.MainActivity.myLocationOverlay;

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.ViewHolder> {
    private final List<Location> locations;
    public static String location_display;


    public LocationAdapter(List<Location> locations) {
        this.locations = locations;


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
                if (v.getContext().equals(MainActivity.context)) {
                    location_display = locations.get(position).getDisplay_name().split(",")[0];
                    current_geoPoint = myLocationOverlay.getLocation();
                    destinationPoint = locations.get(position).getPoint();
                    Log.d(TAG, "Destination point: " + destinationPoint);
                    new ReverseGeocodingTask().execute(current_geoPoint);
                    new SetPath(map, mRoadOverlays).getRoadAsync();
                } else {

                    current_geoPoint = myLocationOverlay.getLocation();
                    destinationPoint = locations.get(position).getPoint();
                    Log.d(TAG, "Destination point: " + destinationPoint);
                    new ReverseGeocodingTask().execute(current_geoPoint);
                    new SetPath_driver(map_driver, mRoadOverlays).getRoadAsync();
                    Toast.makeText(holder.context, "Road map has been drawn", Toast.LENGTH_SHORT).show();
                    new CreateRide().hideRecyclerView(recyclerView_driver);


                }




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
