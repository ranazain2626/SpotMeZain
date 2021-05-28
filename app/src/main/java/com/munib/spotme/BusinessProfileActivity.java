package com.munib.spotme;

import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.munib.spotme.adapters.InvestmentsListAdapter;
import com.munib.spotme.adapters.MyDealsLendingAdapter;
import com.munib.spotme.base.BaseActivity;
import com.munib.spotme.dataModels.BusinessProfileModel;
import com.munib.spotme.dataModels.DataModel;
import com.munib.spotme.dataModels.InvestmentModel;
import com.munib.spotme.dataModels.OffersModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import static com.munib.spotme.utils.CommonUtils.TAGZ;

public class BusinessProfileActivity extends BaseActivity {
    CardView invest_now_btn;
    TextView business_name,business_phone,business_email,value,shares,total_investment;
    ImageView business_image;
    int total_sum=0;
    String key;
    BusinessProfileModel model;
    RecyclerView recyclerView;
    ArrayList<InvestmentModel> arrayList;
    InvestmentsListAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_profile);

        arrayList=new ArrayList<InvestmentModel>();


        recyclerView=findViewById(R.id.rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter=new InvestmentsListAdapter(this,arrayList);
        recyclerView.setAdapter(adapter);

        business_email=findViewById(R.id.business_email);
        business_name=findViewById(R.id.business_name);
        business_phone=findViewById(R.id.business_phone);
        value=findViewById(R.id.value);
        shares=findViewById(R.id.shares);
        total_investment=findViewById(R.id.total_investment);
        business_image=findViewById(R.id.business_image);

        try {
            total_investment.setText(getIntent().getStringExtra("investment"));
        }catch (Exception ex)
        {

        }
        invest_now_btn=(CardView) findViewById(R.id.invest_now_btn);
        invest_now_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    LayoutInflater factory = LayoutInflater.from(BusinessProfileActivity.this);
                    final View deleteDialogView = factory.inflate(R.layout.dialog_invest_now, null);
                    final AlertDialog deleteDialog = new AlertDialog.Builder(BusinessProfileActivity.this).create();
                    deleteDialog.setView(deleteDialogView);
                    deleteDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

                    Spinner share_spinner=deleteDialogView.findViewById(R.id.shares_spinner);
                    TextView share_value=deleteDialogView.findViewById(R.id.share_value);
                    TextView total_investment_sum=deleteDialogView.findViewById(R.id.total_investment_sum);
                    share_value.setText("$ "+model.getShare_value());

                    List<String> categories = new ArrayList<String>();
                    for(int i=0;i<model.getShares();i++)
                     {
                         categories.add((i+1)+"");
                     }

                    ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>( BusinessProfileActivity.this, android.R.layout.simple_spinner_item, categories);
                    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    share_spinner.setAdapter(dataAdapter);

                    share_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                         total_sum=model.getShare_value()*Integer.parseInt(share_spinner.getSelectedItem().toString());
                        total_investment_sum.setText("$ "+total_sum);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });

                deleteDialogView.findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            InvestmentModel investmentModel = new InvestmentModel(total_sum,Integer.parseInt(share_spinner.getSelectedItem().toString()),model.getShare_value());
                            database.getReference("investments").child(key).child(auth.getCurrentUser().getUid()).setValue(investmentModel);
                            deleteDialog.dismiss();
                        }
                });

                    deleteDialog.show();
            }
        });
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

        if(auth.getCurrentUser()!=null) {

            key=getIntent().getStringExtra("key");

            Log.d("mubi",auth.getCurrentUser().getUid());
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("business").child(key);
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Log.d("mubi",dataSnapshot.toString());
                        Log.d(TAGZ, dataSnapshot.toString());
                        BusinessProfileModel offer = dataSnapshot.getValue(BusinessProfileModel.class);
                        offer.setUid(dataSnapshot.getKey());
                        model=offer;
                        business_email.setText(offer.getEmail());
                        business_name.setText(offer.getBusiness_name());
                        business_phone.setText(offer.getPhone());
                        value.setText("$ "+offer.getShare_value()+"");
                        shares.setText(offer.getShares()+"");
                        Picasso.get().load(offer.getImage_url()).into(business_image);
                    } else {
                        //      no_data_found.setVisibility(View.VISIBLE);
                        Log.d("mubi","yes");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.d(TAGZ, error.getDetails());
                    //  no_data_found.setVisibility(View.VISIBLE);
                }
            });

            DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference().child("investments").child(key);
            ref1.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Log.d("mubi",dataSnapshot.toString());
                        for (DataSnapshot datas : dataSnapshot.getChildren()) {
                            Log.d(TAGZ, datas.toString());
                            InvestmentModel offer = datas.getValue(InvestmentModel.class);
                            offer.setUser_uid(datas.getKey());
                            arrayList.add(offer);
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
    public void onPause() {
        super.onPause();
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }

    }
}
