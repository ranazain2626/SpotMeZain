package com.munib.spotme.fragments;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.munib.spotme.R;
import com.munib.spotme.adapters.MyDealsViewPager;

public class MyDealsFragment extends Fragment {

    TabLayout tabLayout;
    ViewPager viewPager;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_my_deals, container, false);
        tabLayout=root.findViewById(R.id.tabs);
        viewPager=root.findViewById(R.id.pager);

        viewPager.setAdapter(new MyDealsViewPager(getChildFragmentManager()));
        tabLayout.addTab(tabLayout.newTab().setText("Pending"));
        tabLayout.addTab(tabLayout.newTab().setText("Lended"));
        tabLayout.addTab(tabLayout.newTab().setText("Borrowed"));

       //S tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        LinearLayout linearLayout= (LinearLayout) tabLayout.getChildAt(0);
        linearLayout.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
        linearLayout.setDividerPadding(10);
        GradientDrawable drawable=new GradientDrawable();
        drawable.setColor(getResources().getColor(R.color.colorPrimaryDark));
        drawable.setSize(1,1);
        linearLayout.setDividerDrawable(drawable);

        tabLayout.setupWithViewPager(viewPager);

//        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
//            @Override
//            public void onTabSelected(TabLayout.Tab tab) {
//
//            }
//
//            @Override
//            public void onTabUnselected(TabLayout.Tab tab) {
//
//            }
//
//            @Override
//            public void onTabReselected(TabLayout.Tab tab) {
//
//            }
//        });
        return root;
    }

}