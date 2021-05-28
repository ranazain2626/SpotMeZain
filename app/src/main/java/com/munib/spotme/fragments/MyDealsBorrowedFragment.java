package com.munib.spotme.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.munib.spotme.R;
import com.munib.spotme.adapters.MyDealsBorrowedAdapter;
import com.munib.spotme.adapters.MyDealsLendingAdapter;
import com.munib.spotme.adapters.OffersAdapter;
import com.munib.spotme.base.BaseFragment;
import com.munib.spotme.dataModels.DataModel;
import com.munib.spotme.dataModels.OffersModel;

import java.util.ArrayList;

import static com.munib.spotme.utils.CommonUtils.TAGZ;

public class MyDealsBorrowedFragment extends BaseFragment{
    RecyclerView recyclerView;
    ArrayList<DataModel> dataModels;
    ArrayList<OffersModel> arrayList;
    MyDealsBorrowedAdapter adapter;
    TextView no_data_found;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_my_deals_borrowed, container, false);
        arrayList=new ArrayList<OffersModel>();
        no_data_found=(TextView) root.findViewById(R.id.no_data_found);

        recyclerView=root.findViewById(R.id.rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        adapter=new MyDealsBorrowedAdapter(getActivity(),arrayList);
        recyclerView.setAdapter(adapter);
        return root;
    }

    @Override
    protected void injectView() {

    }

    @Override
    public void onResume() {
        super.onResume();

        if(auth.getCurrentUser()!=null) {
            arrayList.clear();

            Log.d("mubi",auth.getCurrentUser().getUid());
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("requests");
            ref.orderByChild("borrower").equalTo(auth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                         for (DataSnapshot datas : dataSnapshot.getChildren()) {
                            Log.d(TAGZ, datas.toString());
                            OffersModel offer = datas.getValue(OffersModel.class);
                            offer.setOffer_uid(datas.getKey());
                            Log.d("mubi--","inside");
                            if(offer.getStatus()==2){
                                no_data_found.setVisibility(View.GONE);
                                arrayList.add(offer);
                            }
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Log.d("mubi","yes");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.d(TAGZ, error.getDetails());

                }
            });

            DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference().child("offers");
            ref1.orderByChild("borrower").equalTo(auth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Log.d("mubi",dataSnapshot.toString());
                        for (DataSnapshot datas : dataSnapshot.getChildren()) {
                            Log.d(TAGZ, datas.toString());
                            OffersModel offer = datas.getValue(OffersModel.class);
                            offer.setOffer_uid(datas.getKey());
                            if(offer.getStatus()==2){
                                no_data_found.setVisibility(View.GONE);
                                arrayList.add(offer);
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

}