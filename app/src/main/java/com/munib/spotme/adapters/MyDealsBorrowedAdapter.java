package com.munib.spotme.adapters;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.munib.spotme.AgreementActivity;
import com.munib.spotme.MainActivity;
import com.munib.spotme.LoanPayActivity;
import com.munib.spotme.PayInstallmentsActivity;
import com.munib.spotme.R;
import com.munib.spotme.SendMessageActivity;
import com.munib.spotme.UserProfileActivity;
import com.munib.spotme.dataModels.OffersModel;
import com.munib.spotme.dataModels.ProposedMoneyModel;
import com.squareup.picasso.Picasso;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import de.hdodenhof.circleimageview.CircleImageView;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;

import static com.munib.spotme.utils.CommonUtils.TAGZ;

public class MyDealsBorrowedAdapter extends RecyclerView.Adapter<MyDealsBorrowedAdapter.RestaurantViewHolder>{
    private ArrayList<OffersModel> dataModels;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    private Context mContext;

    public MyDealsBorrowedAdapter(Context context, ArrayList<OffersModel> dataModels) {
        mContext = context;
        this.dataModels = dataModels;
        pref= context.getSharedPreferences("MyPref", 0); // 0 - for private mode
        editor= pref.edit();
    }

    @Override
    public MyDealsBorrowedAdapter.RestaurantViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item_borrowed, parent, false);
        RestaurantViewHolder viewHolder = new RestaurantViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyDealsBorrowedAdapter.RestaurantViewHolder holder, int position) {
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
        TextView username,upcoming_status;
        TextView price,duration,payment_plan,interest_rate;
        CircleImageView image;
        TextView report_btn,agreement_btn,extension_btn,rate_lender;
        ExpandableLayout expandableLayout;
        FrameLayout msg;
        LinearLayout next_payment_layout,no_payment_layout;
        TextView proposed_payment_card;
        TextView nextPayment_amount,nextPayment_month,nextPayment_dueDate,nextPayment_index;
        TextView pay_now_btn,request_extention_btn;
        Boolean signed=false;
        String selected_user_token="";

        public RestaurantViewHolder(View itemView) {
            super(itemView);
            mContext = itemView.getContext();

            upcoming_status=itemView.findViewById(R.id.upcoming_status);
            username=itemView.findViewById(R.id.username);
            price=itemView.findViewById(R.id.txtPrice);
            duration=itemView.findViewById(R.id.duration);
            payment_plan=itemView.findViewById(R.id.paymentPlan);
            interest_rate=itemView.findViewById(R.id.interest_rate);

            extension_btn=itemView.findViewById(R.id.request_extension);
            report_btn=itemView.findViewById(R.id.report_btn);
            rate_lender=itemView.findViewById(R.id.rate_lender);
            image=itemView.findViewById(R.id.profile_image);

            agreement_btn=itemView.findViewById(R.id.agreement_btn);
            expandableLayout=itemView.findViewById(R.id.expandable_layout);
            msg=itemView.findViewById(R.id.msg);

            next_payment_layout=(LinearLayout) itemView.findViewById(R.id.next_payment_layout);
            no_payment_layout=(LinearLayout) itemView.findViewById(R.id.no_payment_layout);
            proposed_payment_card=(TextView) itemView.findViewById(R.id.payments_btn);
            nextPayment_amount=(TextView) itemView.findViewById(R.id.amount);
            nextPayment_month=(TextView) itemView.findViewById(R.id.month);
            nextPayment_dueDate=(TextView) itemView.findViewById(R.id.due_date);
            nextPayment_index=(TextView) itemView.findViewById(R.id.index);

            pay_now_btn=(TextView) itemView.findViewById(R.id.pay_now_btn);
            request_extention_btn=(TextView) itemView.findViewById(R.id.request_extention_btn);

        }

        public void bindGateway(OffersModel restaurant) {

            rate_lender.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    ((MainActivity) mContext).showProgress();
                    DatabaseReference ref0 = FirebaseDatabase.getInstance().getReference().child("reviews").child(restaurant.getLender());
                    ref0.orderByChild("review_by").equalTo(((MainActivity) mContext).auth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (!dataSnapshot.exists()) {
                    LayoutInflater factory = LayoutInflater.from(mContext);
                    final View deleteDialogView = factory.inflate(R.layout.dialog_rate, null);
                    final AlertDialog deleteDialog = new AlertDialog.Builder(mContext).create();
                    deleteDialog.setView(deleteDialogView);
                    deleteDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

                    MaterialRatingBar ratingBar=deleteDialogView.findViewById(R.id.rating);
                    EditText review=deleteDialogView.findViewById(R.id.review);
                    TextView submit_btn=deleteDialogView.findViewById(R.id.submit_btn);

                    submit_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(review.getText().toString().equals(""))
                            {
                                review.setError("Please enter at least 5 characters");
                            }else{
                                DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference().child("reviews").child(restaurant.getLender());

                                Map mParent = new HashMap();
                                mParent.put("rating", ratingBar.getRating());
                                mParent.put("review", review.getText().toString());
                                mParent.put("review_by", restaurant.getBorrower());
                                mParent.put("review_type","lender");
                                ref1.push().setValue(mParent);


                                deleteDialog.dismiss();

                                ((MainActivity)mContext).showMessage("Review submitted successfully");
                            }
                        }
                    });
                    deleteDialog.show();
                            } else {
                                Toast.makeText(mContext,"Oops! You’ve already left a review",Toast.LENGTH_LONG).show();
                            }
                            ((MainActivity) mContext).hideProgress();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.d(TAGZ, error.getDetails());
                            ((MainActivity) mContext).hideProgress();
                            Toast.makeText(mContext,"Technical issue! Please try agin",Toast.LENGTH_LONG).show();
                        }
                    });
                }
            });

            ArrayList<ProposedMoneyModel> proposedMoneyArrayList1=new ArrayList<>();
            DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference().child("payments");
            ref1.child(restaurant.getOffer_uid()).orderByChild("status").equalTo(0).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot datas : dataSnapshot.getChildren()) {
                            ProposedMoneyModel offer = datas.getValue(ProposedMoneyModel.class);
                            offer.setUid(datas.getKey());
                            proposedMoneyArrayList1.add(offer);
                        }
                    } else {
                        next_payment_layout.setVisibility(View.GONE);
                        no_payment_layout.setVisibility(View.VISIBLE);
                    }

                    if (proposedMoneyArrayList1.size() > 0){
                        nextPayment_amount.setText("$" + proposedMoneyArrayList1.get(0).getAmount()+"0");
                    nextPayment_index.setText(proposedMoneyArrayList1.get(0).getIndex() + "");
                    nextPayment_month.setText(proposedMoneyArrayList1.get(0).getMonth());

                    SimpleDateFormat sdf = new SimpleDateFormat("EE MMM dd HH:mm:ss ZZZ yyyy",
                            Locale.ENGLISH);
                    Date date = null;
                    try {
                        date = sdf.parse(proposedMoneyArrayList1.get(0).getDue_date());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    SimpleDateFormat month_date = new SimpleDateFormat("dd-MMM-yyyy");

                    Calendar c1 = Calendar.getInstance();
                    if(c1.getTime().after(date))
                    {
                        upcoming_status.setText("Past Due");
                        upcoming_status.setTextColor(mContext.getResources().getColor(android.R.color.holo_red_dark));
                    }

                    nextPayment_dueDate.setText("Due Date : " + month_date.format(date));

                    pay_now_btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent a=new Intent(mContext, PayInstallmentsActivity.class);
                                a.putExtra("amount",(int)proposedMoneyArrayList1.get(0).getAmount());
                                a.putExtra("payment_id",restaurant.getOffer_uid());
                                a.putExtra("index",proposedMoneyArrayList1.get(0).getIndex());
                                a.putExtra("user_id",restaurant.getLender());
                                a.putExtra("token",selected_user_token);

                                ((MainActivity) mContext).startActivity(a);
                            }
                        });

                    if(proposedMoneyArrayList1.get(0).getExtension_requested()==0) {

                        Calendar current_time = Calendar.getInstance();
                        Calendar c = Calendar.getInstance();
                        c.setTime(date);
                        c.add(Calendar.DATE, -7);
                        //   Log.d("mubi-d",c.getTime().toString());

                        if (current_time.getTime().after(c.getTime())) {
                            request_extention_btn.setVisibility(View.VISIBLE);
                        }


                        request_extention_btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
//                                if(selected_user_token!=null) {
//                                    ((MainActivity) mContext).sendNotification(((MainActivity) mContext).currentUserData.getName() + " has declined your loan offer", selected_user_token);
//                                    FirebaseDatabase.getInstance().getReference("notifications").child(restaurant.getLender()).push().child("notification").setValue(((MainActivity) mContext).currentUserData.getName() + " has declined your loan offer");
//                                }
                                LayoutInflater factory = LayoutInflater.from(mContext);
                                final View deleteDialogView = factory.inflate(R.layout.dialog_request_extension, null);
                                final AlertDialog deleteDialog = new AlertDialog.Builder(mContext).create();
                                deleteDialog.setView(deleteDialogView);
                                deleteDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

                                TextView cancel = (TextView) deleteDialogView.findViewById(R.id.cancel);
                                TextView request = (TextView) deleteDialogView.findViewById(R.id.request);

                                cancel.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        deleteDialog.dismiss();
                                    }
                                });
                                request.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                        if (selected_user_token != null) {
                                            ((MainActivity) mContext).sendNotification(((MainActivity) mContext).currentUserData.getName() + " has requested extension for the current loan installment", selected_user_token);
                                            FirebaseDatabase.getInstance().getReference("notifications").child(restaurant.getLender()).push().child("notification").setValue(((MainActivity) mContext).currentUserData.getName() + " has requested extension for the current loan installment");
                                        }
                                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("payments").child(restaurant.getOffer_uid()).child(proposedMoneyArrayList1.get(0).getUid()).child("extension_requested");
                                        ref.setValue(1);
                                        ((MainActivity) mContext).showMessage("Request sent. You will be notified if lender accepts");
                                        request_extention_btn.setVisibility(View.GONE);
                                        deleteDialog.dismiss();
                                    }
                                });
                                deleteDialog.show();
                                }
                            });
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.d(TAGZ, error.getDetails());

                }
            });

            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent a=new Intent(mContext, UserProfileActivity.class);
                    a.putExtra("user_id",restaurant.getLender());
                    ((MainActivity)mContext).startActivity(a);
                }
            });



            proposed_payment_card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    LayoutInflater factory = LayoutInflater.from(mContext);
                    final View deleteDialogView = factory.inflate(R.layout.dialog_proposed_payments, null);
                    final AlertDialog deleteDialog = new AlertDialog.Builder(mContext).create();
                    deleteDialog.setView(deleteDialogView);
                    deleteDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

                    RecyclerView rv1=(RecyclerView) deleteDialogView.findViewById(R.id.rv);
                    rv1.setLayoutManager(new LinearLayoutManager(mContext));

                    TextView ok=deleteDialogView.findViewById(R.id.ok_btn);
                    ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            deleteDialog.dismiss();
                        }
                    });

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


            msg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent a=new Intent(mContext, SendMessageActivity.class);
                    a.putExtra("uid1",((MainActivity) mContext).auth.getCurrentUser().getUid());
                    a.putExtra("uid2",restaurant.getLender());
                    a.putExtra("thread_id","0");
                    ((MainActivity) mContext).startActivity(a);
                }
            });

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

            price.setText("$ "+ restaurant.getAmount()+".00");
            duration.setText(restaurant.getDuration());
            interest_rate.setText(restaurant.getInterestRate()+"%");
            double value=Double.parseDouble(restaurant.getAmountAfterInterest());
            int int_val=Integer.parseInt(((int)value)+"");
            payment_plan.setText("$ "+ int_val+".00");

//            deceline_btn.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    LayoutInflater factory = LayoutInflater.from(mContext);
//                    final View deleteDialogView = factory.inflate(R.layout.dialog_decline_offer, null);
//                    final AlertDialog deleteDialog = new AlertDialog.Builder(mContext).create();
//                    deleteDialog.setView(deleteDialogView);
//                    deleteDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
//
//                    TextView cancel=(TextView) deleteDialogView.findViewById(R.id.cancel);
//                    TextView decline=(TextView) deleteDialogView.findViewById(R.id.decline);
//
//                    cancel.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            deleteDialog.dismiss();
//                        }
//                    });
//                    decline.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("offers").child(restaurant.getOffer_uid()).child("status");
//                            ref.setValue(-1);
//                            deleteDialog.dismiss();
//                        }
//                    });
//                    deleteDialog.show();
//                }
//            });

            agreement_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // expandableLayout.setVisibility(View.VISIBLE);
                    Intent a=new Intent(mContext, AgreementActivity.class);
                    a.putExtra("url",restaurant.getLoan_agreement());
                    ((MainActivity) mContext).startActivity(a);
                }
            });
        }
    }
}