package com.munib.spotme;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.munib.spotme.base.BaseActivity;
import com.munib.spotme.fragments.InvestExploreFragment;
import com.munib.spotme.fragments.InvestHomeFragment;
import com.munib.spotme.fragments.MessagesFragment;
import com.munib.spotme.fragments.ProfileFragment;
import com.munib.spotme.fragments.HomeFragment;
import com.munib.spotme.fragments.MyDealsFragment;
import com.munib.spotme.fragments.OffersFragment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;

import org.jetbrains.annotations.NotNull;

import me.ibrahimsn.lib.SmoothBottomBar;

public class MainActivity extends BaseActivity {

    NavController navController;
    BottomNavigationView bottomBar;
    Fragment homeFragment,messagesFragment,offersFragment,myDealsFragment,investFragment,profileFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomBar=findViewById(R.id.bottomBar);
        homeFragment=new HomeFragment();
     //   requestFragment=new RequestsFragment();
        messagesFragment=new MessagesFragment();
        myDealsFragment=new MyDealsFragment();
        investFragment=new InvestHomeFragment();
        profileFragment=new ProfileFragment();
       //myOffersFragment=new HomeFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.fl_container, homeFragment).commit();

        bottomBar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
                if(item.getItemId() == R.id.navigation_home)
                {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fl_container, homeFragment).addToBackStack("0").commit();

                }else if(item.getItemId() == R.id.navigation_msg){
                    getSupportFragmentManager().beginTransaction().replace(R.id.fl_container, messagesFragment).addToBackStack("1").commit();

                }else if(item.getItemId() == R.id.navigation_deals){
                    getSupportFragmentManager().beginTransaction().replace(R.id.fl_container, myDealsFragment).addToBackStack("3").commit();

                }else if(item.getItemId() == R.id.navigation_profile){
                    getSupportFragmentManager().beginTransaction().replace(R.id.fl_container, profileFragment).addToBackStack("4").commit();

                }
                return true;
            }
        });

    }
}