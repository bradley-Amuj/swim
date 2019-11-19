package com.example.user.swim.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.user.swim.R;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class Notifications extends Fragment {
    private Adapter myAdapter;
    private ViewPager viewPager;
    private TabLayout tab;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_notifications, null);

        viewPager = view.findViewById(R.id.pager);
        tab = view.findViewById(R.id.tab_layout);

        myAdapter = new Adapter(getChildFragmentManager());
        viewPager.setAdapter(myAdapter);

        setTabLayout();
        return view;
    }

    private void setTabLayout() {
        tab.setupWithViewPager(viewPager);


        tab.getTabAt(0).setText("My Ride Offers");
        tab.getTabAt(1).setText("Incoming Notifications");
    }


    public class Adapter extends FragmentStatePagerAdapter {

        public Adapter(@NonNull FragmentManager fm) {
            super(fm);
        }


        @Override
        public int getCount() {
            return 2;
        }


        @NonNull
        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 0:
                    return new Myrides();

                case 1:
                    return new incoming_requests();


            }
            return null;
        }


    }
}