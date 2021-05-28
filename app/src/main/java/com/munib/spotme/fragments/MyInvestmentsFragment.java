package com.munib.spotme.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.munib.spotme.R;
import com.munib.spotme.adapters.BorrowedDealsAdapter;
import com.munib.spotme.adapters.InvestmentsListAdapter;
import com.munib.spotme.adapters.MyInvestmentsAdapter;
import com.munib.spotme.base.BaseFragment;
import com.munib.spotme.dataModels.BusinessProfileModel;
import com.munib.spotme.dataModels.DataModel;
import com.munib.spotme.dataModels.InvestmentModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static com.munib.spotme.utils.CommonUtils.TAGZ;

public class MyInvestmentsFragment extends BaseFragment {
    RecyclerView recyclerView;
    ArrayList<InvestmentModel> arrayList;
    MyInvestmentsAdapter adapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_investments, container, false);
        arrayList=new ArrayList<InvestmentModel>();


        recyclerView=view.findViewById(R.id.rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        adapter=new MyInvestmentsAdapter(getActivity(),arrayList);
        recyclerView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(auth.getCurrentUser()!=null) {
            arrayList.clear();
            DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference().child("investments");
            ref1.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Log.d("mubi",dataSnapshot.toString());
                        for (DataSnapshot datas : dataSnapshot.getChildren()) {
                            for (DataSnapshot dataSnapshot1 : datas.getChildren()) {
                                InvestmentModel offer = dataSnapshot1.getValue(InvestmentModel.class);
                                offer.setUser_uid(dataSnapshot1.getKey());
                                offer.setBusiness_uid(datas.getKey());
                                if (offer.getUser_uid().equals(auth.getCurrentUser().getUid())) {
                                    arrayList.add(offer);
                                }
                            }
                        }
                        adapter.notifyDataSetChanged();
                    } else {

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.d(TAGZ, error.getDetails());

                }
            });

        }
    }

    @Override
    protected void injectView() {

    }
}
