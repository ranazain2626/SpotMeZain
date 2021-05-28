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
import com.munib.spotme.adapters.OffersAdapter;
import com.munib.spotme.base.BaseFragment;
import com.munib.spotme.dataModels.DataModel;
import com.munib.spotme.dataModels.OffersModel;

import java.util.ArrayList;

import static com.munib.spotme.utils.CommonUtils.TAGZ;

public class OffersFragment extends BaseFragment{
    RecyclerView recyclerView;
    ArrayList<DataModel> dataModels;
    ArrayList<OffersModel> arrayList;
    OffersAdapter adapter;
    TextView no_data_found;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_offers, container, false);
        arrayList=new ArrayList<OffersModel>();

        no_data_found=(TextView) root.findViewById(R.id.no_data_found);

        recyclerView=root.findViewById(R.id.rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        adapter=new OffersAdapter(getActivity(),arrayList);
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

            Log.d("mubi",auth.getCurrentUser().getUid());
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("offers");
            ref.orderByChild("borrower").equalTo(auth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        arrayList.clear();
                        Log.d("mubi",dataSnapshot.toString());
                        for (DataSnapshot datas : dataSnapshot.getChildren()) {
                            Log.d(TAGZ, datas.toString());
                            OffersModel offer = datas.getValue(OffersModel.class);
                            offer.setOffer_uid(datas.getKey());
                            if(offer.getStatus()==0 || offer.getStatus()==1) {
                                no_data_found.setVisibility(View.GONE);
                                arrayList.add(offer);
                            }
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        no_data_found.setVisibility(View.VISIBLE);
                        Log.d("mubi","yes");
                        arrayList.clear();
                        adapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.d(TAGZ, error.getDetails());
                    no_data_found.setVisibility(View.VISIBLE);

                }
            });
        }
    }

}