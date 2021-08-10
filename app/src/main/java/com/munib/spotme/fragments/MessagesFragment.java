package com.munib.spotme.fragments;

import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import com.munib.spotme.adapters.MessagesAdapter;
import com.munib.spotme.adapters.MyDealsLendingAdapter;
import com.munib.spotme.adapters.OffersAdapter;
import com.munib.spotme.base.BaseActivity;
import com.munib.spotme.base.BaseFragment;
import com.munib.spotme.dataModels.DataModel;
import com.munib.spotme.dataModels.MessageModel;
import com.munib.spotme.dataModels.OffersModel;
import com.munib.spotme.dataModels.ThreadModel;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

import static com.munib.spotme.utils.CommonUtils.TAGZ;

public class MessagesFragment extends BaseFragment {
    RecyclerView recyclerView;
    ArrayList<DataModel> dataModels;
    ArrayList<ThreadModel> arrayList;
    MessagesAdapter adapter;
    TextView no_msg_txt;


    @androidx.annotation.Nullable
    @Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @androidx.annotation.Nullable @Nullable ViewGroup container, @androidx.annotation.Nullable @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.activity_messages, container, false);

        return root;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @androidx.annotation.Nullable @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        arrayList=new ArrayList<ThreadModel>();

        no_msg_txt=view.findViewById(R.id.no_msg_txt);
        recyclerView=view.findViewById(R.id.rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        adapter=new MessagesAdapter(getActivity(),arrayList);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        showProgress();
        if(auth.getCurrentUser()!=null) {
            arrayList.clear();
            Log.d("mubi",auth.getCurrentUser().getUid());
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users");
            ref.child(auth.getCurrentUser().getUid()).child("conversations").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        no_msg_txt.setVisibility(View.GONE);
                        Log.d("mubi",dataSnapshot.toString());
                        for (DataSnapshot datas : dataSnapshot.getChildren()) {
                            Log.d(TAGZ, datas.toString());
                            ThreadModel offer = datas.getValue(ThreadModel.class);
                            offer.setThread_id(datas.getKey());
                            arrayList.add(offer);
                        }
                        adapter.notifyDataSetChanged();
                    } else {

                    }
                    hideProgress();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.d(TAGZ, error.getDetails());
                    hideProgress();
                }
            });
        }
    }

    @Override
    protected void injectView() {

    }
}