package com.munib.spotme;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.munib.spotme.adapters.ReviewAdapter;
import com.munib.spotme.base.BaseActivity;
import com.munib.spotme.dataModels.OffersModel;
import com.munib.spotme.dataModels.ReviewsModel;
import com.munib.spotme.dataModels.UserModel;
import com.squareup.picasso.Picasso;
import com.stripe.android.model.Card;

import java.util.ArrayList;
import java.util.Map;

import static com.munib.spotme.utils.CommonUtils.TAGZ;

public class UserProfileActivity extends BaseActivity {
    String user_id;
    TextView lender_rating,borrower_rating,name,username,email,phone,lender_ratings_txt,borrower_rating_txt,total_loaned,total_borrowed,total_shares,total_invested;
    ImageView user_image;
    Button btn_offer,btn_request;
    RecyclerView rv;
    FrameLayout msg;
    int total_loaned_value=0,total_borrowed_value=0;
    int lender_rating_value=0,borrower_rating_value=0;
    ArrayList<ReviewsModel> reviewsModelArrayList;
    ReviewAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        user_id=getIntent().getStringExtra("user_id");

        ImageView back_btn=(ImageView) findViewById(R.id.back_btn);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        reviewsModelArrayList=new ArrayList<>();
        adapter=new ReviewAdapter(this,reviewsModelArrayList);

        lender_rating=(TextView) findViewById(R.id.lender_rating);
        borrower_rating=(TextView) findViewById(R.id.borrower_rating);
        lender_ratings_txt=(TextView) findViewById(R.id.lender_rating_txt);
        borrower_rating_txt=(TextView) findViewById(R.id.borrower_rating_txt);

        name=(TextView) findViewById(R.id.name);
        username=(TextView) findViewById(R.id.username);
        phone=(TextView) findViewById(R.id.phone);
        email=(TextView) findViewById(R.id.email);
        phone=(TextView) findViewById(R.id.phone);

        total_borrowed=(TextView) findViewById(R.id.total_borrowed);
        total_loaned=(TextView) findViewById(R.id.total_loaned);
        total_shares=(TextView) findViewById(R.id.total_shares);
        total_invested=(TextView) findViewById(R.id.total_investment);
        user_image=(ImageView) findViewById(R.id.image);

        btn_offer=(Button) findViewById(R.id.offer_btn);
        btn_request=(Button) findViewById(R.id.request_btn);
        msg=findViewById(R.id.msg);

        btn_offer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(UserProfileActivity.this,LendMoneyActivity.class));
            }
        });


        msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent a=new Intent(UserProfileActivity.this, SendMessageActivity.class);
                a.putExtra("uid1",auth.getCurrentUser().getUid());
                a.putExtra("uid2",user_id);
                a.putExtra("thread_id","0");
                startActivity(a);
            }
        });

        btn_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(UserProfileActivity.this,RequestMoneyActivity.class));
            }
        });

        rv=(RecyclerView) findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);

        showProgress();
        if(user_id!=null){
            FirebaseDatabase.getInstance().getReference().child("users").
                    child(user_id).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                            Log.d(TAGZ, dataSnapshot.toString());
                             Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                             UserModel user = new UserModel();
                             user.setUid(dataSnapshot.getKey());
                            try {
                                user.setImage_url(map.get("image_url").toString());
                                Picasso.get().load(map.get("image_url").toString()).into(user_image);
                              }catch (Exception ex)
                            {

                            }
                            name.setText(map.get("name").toString());
                            username.setText("@"+map.get("username").toString());
//                            email.setText(offer.getEmail());
//                            phone.setText(offer.getPhone());

                    } else {

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.d(TAGZ, error.getDetails());

                }
            });
            setStats();
        }

    }

    public void setStats()
    {
        total_borrowed_value=0;
        total_loaned_value=0;
        FirebaseDatabase.getInstance().getReference().child("requests").
                orderByChild("lender").equalTo(auth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot datas : dataSnapshot.getChildren()) {
                        Log.d(TAGZ, datas.toString());
                        OffersModel offer = datas.getValue(OffersModel.class);
                        offer.setOffer_uid(datas.getKey());
                        if(offer.getStatus()==2){
                            total_loaned_value+=Integer.parseInt(offer.getAmount());
                        }
                    }
                    total_loaned.setText("$"+total_loaned_value+".00");
                } else {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAGZ, error.getDetails());

            }
        });

        FirebaseDatabase.getInstance().getReference().child("offers").
                orderByChild("lender").equalTo(auth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Log.d("mubi",dataSnapshot.toString());
                    for (DataSnapshot datas : dataSnapshot.getChildren()) {
                        Log.d(TAGZ, datas.toString());
                        OffersModel offer = datas.getValue(OffersModel.class);
                        offer.setOffer_uid(datas.getKey());
                        if (offer.getStatus() == 2) {
                            total_loaned_value += Integer.parseInt(offer.getAmount());
                        }
                    }
                    total_loaned.setText("$"+total_loaned_value+".00");

                } else {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAGZ, error.getDetails());

            }
        });

        FirebaseDatabase.getInstance().getReference().child("requests").
    orderByChild("borrower").equalTo(auth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if (dataSnapshot.exists()) {
                for (DataSnapshot datas : dataSnapshot.getChildren()) {
                    Log.d(TAGZ, datas.toString());
                    OffersModel offer = datas.getValue(OffersModel.class);
                    offer.setOffer_uid(datas.getKey());
                    if(offer.getStatus()==2){
                        total_borrowed_value+=Integer.parseInt(offer.getAmount());
                    }
                }
                total_borrowed.setText("$"+total_borrowed_value+".00");
            } else {

            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            Log.d(TAGZ, error.getDetails());

        }
    });

        FirebaseDatabase.getInstance().getReference().child("offers").
    orderByChild("borrower").equalTo(auth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if (dataSnapshot.exists()) {
                Log.d("mubi",dataSnapshot.toString());
                for (DataSnapshot datas : dataSnapshot.getChildren()) {
                    Log.d(TAGZ, datas.toString());
                    OffersModel offer = datas.getValue(OffersModel.class);
                    offer.setOffer_uid(datas.getKey());
                    if (offer.getStatus() == 2) {
                        total_borrowed_value += Integer.parseInt(offer.getAmount());
                    }
                }
                total_borrowed.setText("$"+total_borrowed_value+".00");
//                hideProgress();

            } else {
                hideProgress();
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            Log.d(TAGZ, error.getDetails());
            hideProgress();
        }
    });


        FirebaseDatabase.getInstance().getReference().child("reviews").child(user_id).
                orderByChild("review_type").equalTo("lender").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Log.d("mubi",dataSnapshot.toString());
                    int a=0;
                    for (DataSnapshot datas : dataSnapshot.getChildren()) {
                        ReviewsModel offer = datas.getValue(ReviewsModel.class);
                        a+=offer.getRating();
                        reviewsModelArrayList.add(offer);
                    }
                    if(a!=0) {
                        lender_rating_value = (int) ((int) a / dataSnapshot.getChildrenCount());
                        lender_rating.setText(lender_rating_value + "");
                    } else
                        lender_rating.setText("N/A");
                    adapter.notifyDataSetChanged();
                  //  hideProgress();

                } else {
                    hideProgress();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAGZ, error.getDetails());
                hideProgress();
            }
        });

        FirebaseDatabase.getInstance().getReference().child("reviews").child(user_id).
                orderByChild("review_type").equalTo("borrower").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Log.d("mubi",dataSnapshot.toString());
                    int a=0;
                    for (DataSnapshot datas : dataSnapshot.getChildren()) {
                        ReviewsModel offer = datas.getValue(ReviewsModel.class);
                        a+=offer.getRating();
                        reviewsModelArrayList.add(offer);
                    }
                    if(a!=0) {
                        borrower_rating_value = (int) ((int) a / dataSnapshot.getChildrenCount());
                        borrower_rating.setText(borrower_rating_value + "");
                    } else
                        borrower_rating.setText("N/A");

                    adapter.notifyDataSetChanged();
                    hideProgress();

                } else {
                    hideProgress();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAGZ, error.getDetails());
                hideProgress();
            }
        });

    }
}
