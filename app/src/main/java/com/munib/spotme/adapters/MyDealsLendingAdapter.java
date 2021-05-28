package com.munib.spotme.adapters;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.gcacace.signaturepad.views.SignaturePad;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.munib.spotme.AgreementActivity;
import com.munib.spotme.MainActivity;
import com.munib.spotme.R;
import com.munib.spotme.SendMessageActivity;
import com.munib.spotme.UserProfileActivity;
import com.munib.spotme.dataModels.OffersModel;
import com.munib.spotme.dataModels.ProposedMoneyModel;
import com.squareup.picasso.Picasso;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import de.hdodenhof.circleimageview.CircleImageView;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;

import static com.munib.spotme.utils.CommonUtils.TAGZ;

public class MyDealsLendingAdapter extends RecyclerView.Adapter<MyDealsLendingAdapter.RestaurantViewHolder>{
    private ArrayList<OffersModel> dataModels;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    private Context mContext;

    public MyDealsLendingAdapter(Context context, ArrayList<OffersModel> dataModels) {
        mContext = context;
        this.dataModels = dataModels;
        pref= context.getSharedPreferences("MyPref", 0); // 0 - for private mode
        editor= pref.edit();
    }

    @Override
    public MyDealsLendingAdapter.RestaurantViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item_lended, parent, false);
        RestaurantViewHolder viewHolder = new RestaurantViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyDealsLendingAdapter.RestaurantViewHolder holder, int position) {
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
        TextView agreement_btn,rate_lender;
        ExpandableLayout expandableLayout;
        Boolean signed=false;
        FrameLayout msg;
        LinearLayout next_payment_layout,no_payment_layout;
        TextView proposed_payment_card,give_extension_btn,report_btn;
        TextView nextPayment_amount,nextPayment_month,nextPayment_dueDate,nextPayment_index;
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

            report_btn=itemView.findViewById(R.id.report_btn);
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
            rate_lender=itemView.findViewById(R.id.rate_lender);
            give_extension_btn=itemView.findViewById(R.id.give_extension_btn);
         //   card=(CardView) itemView.findViewById(R.id.card);
        }

        public void bindGateway(OffersModel restaurant) {

            rate_lender.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((MainActivity) mContext).showProgress();
                    DatabaseReference ref0 = FirebaseDatabase.getInstance().getReference().child("reviews").child(restaurant.getBorrower());
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
                                            DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference().child("reviews").child(restaurant.getBorrower());

                                            Map mParent = new HashMap();
                                            mParent.put("rating", ratingBar.getRating());
                                            mParent.put("review", review.getText().toString());
                                            mParent.put("review_by", restaurant.getLender());
                                            mParent.put("review_type","borrower");
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
//
//            if(restaurant.getReported()==0)
//            {
                report_btn.setVisibility(View.VISIBLE);

                report_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        LayoutInflater factory = LayoutInflater.from(mContext);
                        final View deleteDialogView = factory.inflate(R.layout.dialog_report_user, null);
                        final AlertDialog deleteDialog = new AlertDialog.Builder(mContext).create();
                        deleteDialog.setView(deleteDialogView);
                        deleteDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

                        TextView cancel = (TextView) deleteDialogView.findViewById(R.id.cancel);
                        TextView request = (TextView) deleteDialogView.findViewById(R.id.request);
                        EditText issue = (EditText) deleteDialogView.findViewById(R.id.issue);

                        cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                deleteDialog.dismiss();
                            }
                        });

                        request.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                if(issue.getText().toString().equals(""))
                                {
                                    issue.setError("Please enter the details here");
                                }else{

                                    if(restaurant.getType().equals("borrow")) {
                                        DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference().child("requests").child(restaurant.getOffer_uid());
                                        ref1.child("reported").setValue(1);
                                    }else{
                                        DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference().child("offers").child(restaurant.getOffer_uid());
                                        ref1.child("reported").setValue(1);
                                    }

                                    DatabaseReference ref12 = FirebaseDatabase.getInstance().getReference().child("users").child(restaurant.getBorrower());
                                    ref12.child("reported").setValue(1);

                                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("user_reported").child(restaurant.getBorrower()).child(restaurant.getOffer_uid());
                                    ref.child("info").setValue(issue.getText().toString());
                                    ref.child("reported_by").setValue(((MainActivity) mContext).auth.getCurrentUser().getUid());

                                    ((MainActivity) mContext).showMessage("You have reported user successfully");
                                    report_btn.setVisibility(View.GONE);
                                    deleteDialog.dismiss();
                                }
                            }
                        });
                        deleteDialog.show();
                    }
                });

//            }else{
//                report_btn.setVisibility(View.GONE);
//            }

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

                    if(proposedMoneyArrayList1.size()>0) {
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

                        Calendar c = Calendar.getInstance();
                        c.setTime(date);
                        c.add(Calendar.DATE, 14);

                        nextPayment_dueDate.setText("Due Date : " + month_date.format(date));

                        if(proposedMoneyArrayList1.get(0).getExtension_requested()==1) {
                            give_extension_btn.setVisibility(View.VISIBLE);
                            give_extension_btn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    LayoutInflater factory = LayoutInflater.from(mContext);
                                    final View deleteDialogView = factory.inflate(R.layout.dialog_grant_extension, null);
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
                                                ((MainActivity) mContext).sendNotification(((MainActivity) mContext).currentUserData.getName() + " has accepted your extension request for the current loan installment", selected_user_token);
                                                FirebaseDatabase.getInstance().getReference("notifications").child(restaurant.getBorrower()).push().child("notification").setValue(((MainActivity) mContext).currentUserData.getName() + " has accepted your extension request for the current loan installment");
                                            }
                                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("payments").child(restaurant.getOffer_uid()).child(proposedMoneyArrayList1.get(0).getUid());
                                            ref.child("extension_requested").setValue(2);
                                            ref.child("due_date").setValue(sdf.format(c.getTime()));
                                            ((MainActivity) mContext).showMessage("You have accepted extension request successfully");
                                            give_extension_btn.setVisibility(View.GONE);
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
                    a.putExtra("user_id",restaurant.getBorrower());
                    ((MainActivity)mContext).startActivity(a);
                }
            });

            msg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent a=new Intent(mContext, SendMessageActivity.class);
                    a.putExtra("uid1",((MainActivity) mContext).auth.getCurrentUser().getUid());
                    a.putExtra("uid2",restaurant.getBorrower());
                    a.putExtra("thread_id","0");
                    ((MainActivity) mContext).startActivity(a);
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