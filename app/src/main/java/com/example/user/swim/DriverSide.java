package com.example.user.swim;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.user.swim.Fragments.CreateRide;
import com.example.user.swim.Fragments.Notifications;
import com.example.user.swim.Fragments.Profile;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

public class DriverSide extends AppCompatActivity {

    private Toolbar toolbar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_side);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(null);
        mAuth = FirebaseAuth.getInstance();

        setSupportActionBar(toolbar);


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
                        toolbar.setTitle("Profile");
                        loadFragment(new Profile());
                        break;

                }
                return false;
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.driver_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.switch_mode:
                Toast.makeText(this, " Switching to driver", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, MainActivity.class));
                finish();

                break;


            case R.id.logout:
                Toast.makeText(this, "We are logging you out", Toast.LENGTH_SHORT).show();
                mAuth.signOut();
                startActivity(new Intent(DriverSide.this, log_in.class));
                finish();
                break;


        }

        return super.onOptionsItemSelected(item);

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
