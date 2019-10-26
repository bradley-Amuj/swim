package com.example.user.swim;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.user.swim.AsyncTasks.ReverseGeocodingTask;
import com.example.user.swim.AsyncTasks.SetPath;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import static com.example.user.swim.MainActivity.current_geoPoint;
import static com.example.user.swim.MainActivity.destinationPoint;
import static com.example.user.swim.MainActivity.mLocationNewOverlay;

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.ViewHolder> {

    private static String current_location;
    private List<Location> locations;
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
        location_display = locations.get(position).getDisplay_name().split(",")[0];


        holder.parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                current_geoPoint = mLocationNewOverlay.getMyLocation();
                destinationPoint = locations.get(position).getPoint();
                new ReverseGeocodingTask(current_geoPoint).execute(current_geoPoint);
                new SetPath().getRoadAsync();


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
