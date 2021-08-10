package com.munib.spotme.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.munib.spotme.MainActivity;
import com.munib.spotme.R;
import com.munib.spotme.adapters.MyDealsPendingBorrowAdapter;
import com.munib.spotme.adapters.MyDealsPendingLendAdapter;
import com.munib.spotme.base.BaseFragment;
import com.munib.spotme.dataModels.DataModel;
import com.munib.spotme.dataModels.OffersModel;
import com.stripe.android.ApiResultCallback;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.PaymentIntentResult;
import com.stripe.android.Stripe;
import com.stripe.android.model.PaymentIntent;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Objects;

import static com.munib.spotme.utils.CommonUtils.TAGZ;

public class MyDealsPendingFragment extends BaseFragment{
    RecyclerView recyclerView,recyclerView1;
    ArrayList<DataModel> dataModels;
    ArrayList<OffersModel> arrayList,arrayList1;
    MyDealsPendingLendAdapter adapter;
    MyDealsPendingBorrowAdapter adapter1;
    TextView no_data_found;
    Stripe stripe;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_my_deals_pending, container, false);
        arrayList=new ArrayList<OffersModel>();
        arrayList1=new ArrayList<OffersModel>();

        stripe = new Stripe(
                getActivity(),
                PaymentConfiguration.getInstance(getActivity()).getPublishableKey());


        no_data_found=(TextView) root.findViewById(R.id.no_data_found);

        recyclerView=root.findViewById(R.id.rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        recyclerView1=root.findViewById(R.id.rv1);
        recyclerView1.setLayoutManager(new LinearLayoutManager(getActivity()));

        adapter=new MyDealsPendingLendAdapter(getActivity(),arrayList);
        recyclerView.setAdapter(adapter);

        adapter1=new MyDealsPendingBorrowAdapter(getActivity(),arrayList1);
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
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("offers");
            ref.orderByChild("lender").equalTo(auth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
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

            FirebaseDatabase.getInstance().getReference().child("requests")
            .orderByChild("borrower").equalTo(auth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        arrayList1.clear();
                        Log.d("mubi",dataSnapshot.toString());
                        for (DataSnapshot datas : dataSnapshot.getChildren()) {
                            Log.d(TAGZ, datas.toString());
                            OffersModel offer = datas.getValue(OffersModel.class);
                            offer.setOffer_uid(datas.getKey());
                            if(offer.getStatus()==0 || offer.getStatus()==1) {
                                arrayList1.add(offer);
                            }
                        }
                        adapter1.notifyDataSetChanged();
                    } else {
                   //     no_data_found1.setVisibility(View.VISIBLE);
                        Log.d("mubi","yes");
                        arrayList1.clear();
                        adapter1.notifyDataSetChanged();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.d(TAGZ, error.getDetails());
                 //    no_data_found1.setVisibility(View.VISIBLE);

                }
            });


        }
    }


}