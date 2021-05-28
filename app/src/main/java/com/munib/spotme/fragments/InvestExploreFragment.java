package com.munib.spotme.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.munib.spotme.CreateBusinessProfileActivity;
import com.munib.spotme.R;
import com.munib.spotme.adapters.ExploreBusinessAdapter;
import com.munib.spotme.adapters.OffersAdapter;
import com.munib.spotme.adapters.PopularInvestmentsAdapter;
import com.munib.spotme.base.BaseFragment;
import com.munib.spotme.dataModels.BusinessProfileModel;
import com.munib.spotme.dataModels.DataModel;
import com.munib.spotme.dataModels.OffersModel;

import java.util.ArrayList;

import static com.munib.spotme.utils.CommonUtils.TAGZ;

public class InvestExploreFragment extends BaseFragment{
    RecyclerView recyclerView,recyclerView1;
    ArrayList<DataModel> dataModels;
    ArrayList<BusinessProfileModel> arrayList,arrayList1;
    ExploreBusinessAdapter adapter;
    PopularInvestmentsAdapter adapter1;
    FloatingActionButton create_business_btn;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_invest, container, false);
        arrayList=new ArrayList<BusinessProfileModel>();
        arrayList1=new ArrayList<BusinessProfileModel>();

//        no_data_found=(TextView) root.findViewById(R.id.no_data_found);

        create_business_btn=(FloatingActionButton) root.findViewById(R.id.create_business_btn);
        create_business_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent a=new Intent(getActivity(), CreateBusinessProfileActivity.class);
                startActivity(a);
            }
        });
        recyclerView=root.findViewById(R.id.rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView1=root.findViewById(R.id.rv1);
        recyclerView1.setLayoutManager(new LinearLayoutManager(getActivity(),RecyclerView.HORIZONTAL,false));

        adapter=new ExploreBusinessAdapter(getActivity(),arrayList);
        recyclerView.setAdapter(adapter);

        adapter1=new PopularInvestmentsAdapter(getActivity(),arrayList1);
        recyclerView1.setAdapter(adapter1);
        return root;
    }

    @Override
    protected void injectView() {

    }

    @Override
    public void onResume() {
        super.onResume();

        if(auth.getCurrentUser()!=null) {

            Log.d("mubi",auth.getCurrentUser().getUid());
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("business");
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        arrayList.clear();
                        Log.d("mubi",dataSnapshot.toString());
                        for (DataSnapshot datas : dataSnapshot.getChildren()) {
                            Log.d(TAGZ, datas.toString());
                            BusinessProfileModel offer = datas.getValue(BusinessProfileModel.class);
                            offer.setUid(datas.getKey());
                            if(offer.getStatus()==0 || offer.getStatus()==1) {
                            //    no_data_found.setVisibility(View.GONE);
                                arrayList.add(offer);
                            }
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                  //      no_data_found.setVisibility(View.VISIBLE);
                        Log.d("mubi","yes");
                        arrayList.clear();
                        adapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.d(TAGZ, error.getDetails());
                  //  no_data_found.setVisibility(View.VISIBLE);
                }
            });


            DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference().child("business");
            ref1.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        arrayList1.clear();
                        Log.d("mubi",dataSnapshot.toString());
                        for (DataSnapshot datas : dataSnapshot.getChildren()) {
                            Log.d(TAGZ, datas.toString());
                            BusinessProfileModel offer = datas.getValue(BusinessProfileModel.class);
                            offer.setUid(datas.getKey());
                            if(offer.getStatus()==0 || offer.getStatus()==1) {
                                //    no_data_found.setVisibility(View.GONE);
                                arrayList1.add(offer);
                            }
                        }
                        adapter1.notifyDataSetChanged();
                    } else {
                        //      no_data_found.setVisibility(View.VISIBLE);
                        Log.d("mubi","yes");
                        arrayList1.clear();
                        adapter1.notifyDataSetChanged();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.d(TAGZ, error.getDetails());
                    //  no_data_found.setVisibility(View.VISIBLE);
                }
            });
        }
    }

}