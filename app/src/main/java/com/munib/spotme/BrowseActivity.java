package com.munib.spotme;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.munib.spotme.adapters.RequestBrowseAdapter;
import com.munib.spotme.adapters.RequestsAdapter;
import com.munib.spotme.base.BaseActivity;
import com.munib.spotme.dataModels.DataModel;
import com.munib.spotme.dataModels.OffersModel;
import com.stripe.android.Stripe;

import java.io.IOException;
import java.util.ArrayList;

import static com.munib.spotme.utils.CommonUtils.TAGZ;

public class BrowseActivity extends BaseActivity {
    RecyclerView recyclerView;
    ArrayList<DataModel> dataModels;
    ArrayList<OffersModel> arrayList;
    RequestBrowseAdapter adapter;
    TextView no_data_found,button;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse);


        arrayList=new ArrayList<OffersModel>();

        no_data_found=(TextView) findViewById(R.id.no_data_found);

        recyclerView=findViewById(R.id.rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(BrowseActivity.this));

        adapter=new RequestBrowseAdapter(BrowseActivity.this,arrayList);
        recyclerView.setAdapter(adapter);

        button=findViewById(R.id.button2);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(BrowseActivity.this);
                Boolean charges_enabled = preferences.getBoolean("charges_enabled", false);
                if(charges_enabled){
                    startActivity(new Intent(BrowseActivity.this,BrowseRequestMoneyActivity.class));
                }else{
                    showMessage("Please activate your payment setup before making a request");
                }

            }
        });

        ImageView back_btn=findViewById(R.id.back_btn);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();

        if(auth.getCurrentUser()!=null) {
            showProgress();
            Log.d("mubi",auth.getCurrentUser().getUid());
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("universal_requests");
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        arrayList.clear();

                        for (DataSnapshot datas : dataSnapshot.getChildren()) {
                            Log.d(TAGZ, datas.toString());
                            OffersModel offer = datas.getValue(OffersModel.class);
                            offer.setOffer_uid(datas.getKey());
                            if(offer.getStatus()==1) {
                                if(!(offer.getBorrower().equals(currentUserData.getUid()))) {
                                    no_data_found.setVisibility(View.GONE);
                                    arrayList.add(offer);
                                }
                            }
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        no_data_found.setVisibility(View.VISIBLE);
                        Log.d("mubi","yes");
                        arrayList.clear();
                        adapter.notifyDataSetChanged();
                    }
                    hideProgress();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.d(TAGZ, error.getDetails());
                    no_data_found.setVisibility(View.VISIBLE);
                    hideProgress();

                }
            });
        }

    }
}
