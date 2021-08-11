package com.munib.spotme.adapters;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.munib.spotme.BrowseRequestMoneyActivity;
import com.munib.spotme.MainActivity;
import com.munib.spotme.R;
import com.munib.spotme.UserProfileActivity;
import com.munib.spotme.dataModels.OffersModel;
import com.munib.spotme.dataModels.ProposedMoneyModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.munib.spotme.utils.CommonUtils.TAGZ;

public class MyDealsPendingBorrowAdapter extends RecyclerView.Adapter<MyDealsPendingBorrowAdapter.RestaurantViewHolder>{
    private ArrayList<OffersModel> dataModels;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    private Context mContext;

    public MyDealsPendingBorrowAdapter(Context context, ArrayList<OffersModel> dataModels) {
        mContext = context;
        this.dataModels = dataModels;
        pref= context.getSharedPreferences("MyPref", 0); // 0 - for private mode
        editor= pref.edit();

    }

    @Override
    public MyDealsPendingBorrowAdapter.RestaurantViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item_pending_borrow, parent, false);
        RestaurantViewHolder viewHolder = new RestaurantViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyDealsPendingBorrowAdapter.RestaurantViewHolder holder, int position) {
        holder.bindGateway(dataModels.get(position));
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        return dataModels.size();
    }

    public class RestaurantViewHolder extends RecyclerView.ViewHolder {
        TextView username;
        TextView price,duration,payment_plan,interest_rate,type;
        CircleImageView image;
        TextView deceline_btn,payments_btn;
        Boolean signed=false;
        String selected_user_token="";

        public RestaurantViewHolder(View itemView) {
            super(itemView);
            mContext = itemView.getContext();

            username=itemView.findViewById(R.id.username);
            price=itemView.findViewById(R.id.txtPrice);
            duration=itemView.findViewById(R.id.duration);
            payment_plan=itemView.findViewById(R.id.paymentPlan);
            interest_rate=itemView.findViewById(R.id.interest_rate);
            type=itemView.findViewById(R.id.type);
            image=itemView.findViewById(R.id.profile_image);

            deceline_btn=itemView.findViewById(R.id.decline_button);
            payments_btn=itemView.findViewById(R.id.payments_btn);

        }

        public void bindGateway(OffersModel restaurant) {

            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent a=new Intent(mContext, UserProfileActivity.class);
                    a.putExtra("user_id",restaurant.getLender());
                    ((MainActivity)mContext).startActivity(a);
                }
            });

            payments_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    LayoutInflater factory = LayoutInflater.from(mContext);
                    final View deleteDialogView = factory.inflate(R.layout.dialog_proposed_payments, null);
                    BottomSheetDialog deleteDialog = new BottomSheetDialog(mContext,R.style.BottomSheetDialog);
                    deleteDialog.setContentView(deleteDialogView);
                    deleteDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

                    ImageView ok=deleteDialogView.findViewById(R.id.ok_btn);
                    ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            deleteDialog.dismiss();
                        }
                    });
                    RecyclerView rv1=(RecyclerView) deleteDialogView.findViewById(R.id.rv);
                    rv1.setLayoutManager(new LinearLayoutManager(mContext));

                    ArrayList<ProposedMoneyModel> proposedMoneyArrayList=new ArrayList<>();
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("payments");
                    ref.child(restaurant.getOffer_uid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                for (DataSnapshot datas : dataSnapshot.getChildren()) {
                                    ProposedMoneyModel offer = datas.getValue(ProposedMoneyModel.class);
                                    proposedMoneyArrayList.add(offer);

                                    ProposedMoneyAdapter adapter1=new ProposedMoneyAdapter(mContext,proposedMoneyArrayList);
                                    rv1.setAdapter(adapter1);
                                }
                            } else {

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.d(TAGZ, error.getDetails());

                        }
                    });


                    deleteDialog.show();
                }
            });

            price.setText("$ "+ restaurant.getAmount()+".00");
            duration.setText(restaurant.getDuration());
            interest_rate.setText(restaurant.getInterestRate()+"%");
            payment_plan.setText("$ "+ restaurant.getAmountAfterInterest());
            if(restaurant.getType().equals("lend")) {
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users");
                ref.child(restaurant.getBorrower()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            selected_user_token=dataSnapshot.child("device_token").getValue().toString();
                            username.setText("(@"+dataSnapshot.child("username").getValue()+") "+dataSnapshot.child("name").getValue());
                            try {
                                Picasso.get().load(dataSnapshot.child("image_url").getValue().toString()).into(image);
                            }catch (Exception ex)
                            {

                            }
                        } else {

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d(TAGZ, error.getDetails());

                    }
                });
                type.setText("Loan Offer");
            }else {
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users");
                ref.child(restaurant.getLender()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            selected_user_token=dataSnapshot.child("device_token").getValue().toString();

                            username.setText("(@"+dataSnapshot.child("username").getValue()+") "+dataSnapshot.child("name").getValue());
                            try {
                                Picasso.get().load(dataSnapshot.child("image_url").getValue().toString()).into(image);
                            }catch (Exception ex)
                            {

                            }
                        } else {

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d(TAGZ, error.getDetails());

                    }
                });
                type.setText("Borrow Request");
                type.setTextColor(mContext.getResources().getColor(android.R.color.holo_green_dark));
            }

            deceline_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    LayoutInflater factory = LayoutInflater.from(mContext);
                    final View deleteDialogView = factory.inflate(R.layout.dialog_withdraw_offer, null);
                    final AlertDialog deleteDialog = new AlertDialog.Builder(mContext).create();
                    deleteDialog.setView(deleteDialogView);
                    deleteDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

                    TextView cancel=(TextView) deleteDialogView.findViewById(R.id.cancel);
                    TextView decline=(TextView) deleteDialogView.findViewById(R.id.decline);

                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            deleteDialog.dismiss();
                        }
                    });
                    decline.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(selected_user_token!=null) {
                                ((MainActivity) mContext).sendNotification(  ((MainActivity) mContext).currentUserData.getName() + " has withdrawn the offer of $"+restaurant.getAmountAfterInterest(), selected_user_token);
                                FirebaseDatabase.getInstance().getReference("notifications").child(restaurant.getLender()).push().child("notification").setValue(((MainActivity) mContext).currentUserData.getName() + "  has withdrawn the offer of $"+restaurant.getAmountAfterInterest());
                            }
                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("requests").child(restaurant.getOffer_uid()).child("status");
                            ref.setValue(-1);
                            deleteDialog.dismiss();
                        }
                    });
                    deleteDialog.show();
                }
            });

        }
    }
}