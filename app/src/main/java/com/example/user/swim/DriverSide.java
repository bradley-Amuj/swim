package com.example.user.swim;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.user.swim.Fragments.CreateRide;
import com.example.user.swim.Fragments.Notifications;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class DriverSide extends AppCompatActivity {

    private ActionBar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        toolbar = getSupportActionBar();
        toolbar.setTitle("Notifications");
        setContentView(R.layout.activity_driver_side);


        loadFragment(new Notifications());

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch (menuItem.getItemId()) {
                    case R.id.notifications:
                        toolbar.setTitle("Notifications");
                        loadFragment(new Notifications());
                        break;

                    case R.id.create_ride:
                        toolbar.setTitle("Create Ride");
                        loadFragment(new CreateRide());
                        break;

                    case R.id.driver_profile:
                        Toast.makeText(DriverSide.this, "Driver profile", Toast.LENGTH_SHORT).show();
                        break;

                }
                return false;
            }
        });


    }

    private boolean loadFragment(Fragment fragment) {
        //switching fragment
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }

}
