package com.munib.spotme;

import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.munib.spotme.adapters.ExploreBusinessAdapter;
import com.munib.spotme.dataModels.DataModel;

import java.util.ArrayList;


public class SearchBusinessActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<DataModel> dataModels;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_business);
        recyclerView=findViewById(R.id.rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        dataModels=new ArrayList<>();
        dataModels.add(new DataModel());
        dataModels.add(new DataModel());
        dataModels.add(new DataModel());
        dataModels.add(new DataModel());
        dataModels.add(new DataModel());
        dataModels.add(new DataModel());

  //      recyclerView.setAdapter(new ExploreBusinessAdapter(this,dataModels));
    }
    @Override
    public void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getResources().getColor(R.color.com_facebook_blue));
        }
    }
}
