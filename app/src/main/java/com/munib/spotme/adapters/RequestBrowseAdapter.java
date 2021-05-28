package com.munib.spotme.adapters;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.preference.PreferenceManager;
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

import com.github.gcacace.signaturepad.views.SignaturePad;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.munib.spotme.BrowseActivity;
import com.munib.spotme.CounterActivity;
import com.munib.spotme.AddCardActivity;
import com.munib.spotme.LoanPayActivity;
import com.munib.spotme.MainActivity;
import com.munib.spotme.R;
import com.munib.spotme.SendMessageActivity;
import com.munib.spotme.UserProfileActivity;
import com.munib.spotme.dataModels.OffersModel;
import com.munib.spotme.dataModels.ProposedMoneyModel;
import com.squareup.picasso.Picasso;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.munib.spotme.utils.CommonUtils.TAGZ;

public class RequestBrowseAdapter extends RecyclerView.Adapter<RequestBrowseAdapter.RestaurantViewHolder>{
    private ArrayList<OffersModel> dataModels;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    private Context mContext;


    public RequestBrowseAdapter(Context context, ArrayList<OffersModel> dataModels) {
        mContext = context;
        this.dataModels = dataModels;
        pref= context.getSharedPreferences("MyPref", 0); // 0 - for private mode
        editor= pref.edit();

    }

    @Override
    public RequestBrowseAdapter.RestaurantViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item_broswe_request, parent, false);
        RestaurantViewHolder viewHolder = new RestaurantViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RequestBrowseAdapter.RestaurantViewHolder holder, int position) {
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
        TextView request_msg;
        TextView price,duration,payment_plan,interest_rate;
        CircleImageView image;
        TextView accept_btn,report_btn,counter_btn;
        CardView cardView;
        FrameLayout msg;
        ExpandableLayout expandableLayout;
        Boolean signed=false;
        String selected_user_token="";
        CardView proposed_payment_card;
        public RestaurantViewHolder(View itemView) {
            super(itemView);
            mContext = itemView.getContext();

            request_msg=itemView.findViewById(R.id.request_msg);
            username=itemView.findViewById(R.id.username);
            price=itemView.findViewById(R.id.txtPrice);
            duration=itemView.findViewById(R.id.duration);
            payment_plan=itemView.findViewById(R.id.paymentPlan);
            interest_rate=itemView.findViewById(R.id.interest_rate);
            msg=itemView.findViewById(R.id.msg);

            report_btn=itemView.findViewById(R.id.report_btn2);
            counter_btn=itemView.findViewById(R.id.counter_btn);
            accept_btn=itemView.findViewById(R.id.accept_btn);
            image=itemView.findViewById(R.id.profile_image);
            proposed_payment_card=itemView.findViewById(R.id.proposed_payment_card);

            cardView=itemView.findViewById(R.id.card);
            expandableLayout=itemView.findViewById(R.id.expandable_layout);
        }

        public void bindGateway(OffersModel restaurant) {

            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent a=new Intent(mContext, UserProfileActivity.class);
                    a.putExtra("user_id",restaurant.getBorrower());
                    ((BrowseActivity)mContext).startActivity(a);
                }
            });

            counter_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
                    Boolean charges_enabled = preferences.getBoolean("charges_enabled", false);
                    if(charges_enabled) {

                        Intent a = new Intent(mContext, CounterActivity.class);
                        a.putExtra("data", restaurant);
                        a.putExtra("universal", true);
                        ((BrowseActivity) mContext).startActivity(a);
                    }else{
                        ((BrowseActivity)mContext).showMessage("Please activate your payment setup before making counter offer");
                    }
                }
            });


            msg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent a=new Intent(mContext, SendMessageActivity.class);
                    a.putExtra("uid1",((BrowseActivity) mContext).auth.getCurrentUser().getUid());
                    a.putExtra("uid2",restaurant.getBorrower());
                    a.putExtra("thread_id","0");
                    ((BrowseActivity) mContext).startActivity(a);
                }
            });

            report_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    LayoutInflater factory = LayoutInflater.from(mContext);
                    final View deleteDialogView = factory.inflate(R.layout.dialog_report_spam, null);
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
                                    DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference().child("universal_requests").child(restaurant.getOffer_uid());
                                    ref1.child("reported").setValue(1);
                                }else{
                                    DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference().child("universal_requests").child(restaurant.getOffer_uid());
                                    ref1.child("reported").setValue(1);
                                }

                                DatabaseReference ref12 = FirebaseDatabase.getInstance().getReference().child("users").child(restaurant.getBorrower());
                                ref12.child("reported").setValue(1);

                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("spam_reported").child(restaurant.getBorrower()).child(restaurant.getOffer_uid());
                                ref.child("info").setValue(issue.getText().toString());
                                ref.child("reported_by").setValue(((MainActivity) mContext).auth.getCurrentUser().getUid());


                                ((BrowseActivity) mContext).showMessage("You have reported successfully");
                                report_btn.setVisibility(View.GONE);
                                deleteDialog.dismiss();
                            }
                        }
                    });
                    deleteDialog.show();
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


                                }
                                ProposedMoneyAdapter adapter1=new ProposedMoneyAdapter(mContext,proposedMoneyArrayList);
                                rv1.setAdapter(adapter1);
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
                        username.setText("(@"+dataSnapshot.child("username").getValue()+") "+dataSnapshot.child("name").getValue());
                        selected_user_token=dataSnapshot.child("device_token").getValue().toString();
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
            request_msg.setText(restaurant.getRequest_message());
            interest_rate.setText(restaurant.getInterestRate()+"%");
            payment_plan.setText("$ "+ restaurant.getAmountAfterInterest());
//
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
//
////                            if(selected_user_token!=null) {
////                                ((MainActivity) mContext).sendNotification(((MainActivity) mContext).currentUserData.getName() + " has declined your request", selected_user_token);
////                                FirebaseDatabase.getInstance().getReference("notifications").child(restaurant.getBorrower()).push().child("notification").setValue(((MainActivity) mContext).currentUserData.getName() + " has declined your loan offer");
////                            }
//                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("universal_requests").child(restaurant.getOffer_uid()).child("status");
//                            ref.setValue(-1);
//                            deleteDialog.dismiss();
//                        }
//                    });
//                    deleteDialog.show();
//                }
//            });

            accept_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
                    Boolean charges_enabled = preferences.getBoolean("charges_enabled", false);
                    if(charges_enabled){

                        LayoutInflater factory = LayoutInflater.from(mContext);
                        final View deleteDialogView = factory.inflate(R.layout.dialog_loan_agreement, null);
                        final AlertDialog deleteDialog = new AlertDialog.Builder(mContext).create();
                        deleteDialog.setView(deleteDialogView);
                        deleteDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

                        WebView webView=(WebView) deleteDialogView.findViewById(R.id.webview);
                        webView.getSettings().setJavaScriptEnabled(true);
                        webView.setWebViewClient(new WebViewClient());

                        LinearLayout sign_layout=(LinearLayout) deleteDialogView.findViewById(R.id.sign_layout);
                        LinearLayout agreement_layout=(LinearLayout) deleteDialogView.findViewById(R.id.agreement_layout);
                        SignaturePad mSignaturePad = (SignaturePad) deleteDialogView.findViewById(R.id.signature_pad);

                        mSignaturePad.setOnSignedListener(new SignaturePad.OnSignedListener() {

                            @Override
                            public void onStartSigning() {
                                //Event triggered when the pad is touched
                            }

                            @Override
                            public void onSigned() {
                                signed=true;
                            }

                            @Override
                            public void onClear() {
                                //Event triggered when the pad is cleared
                            }
                        });


                        // If you deal with HTML then execute loadData instead of loadUrl
                        try {
                            restaurant.setLoan_agreement(restaurant.getLoan_agreement().replace("LENDER_NAME", ((BrowseActivity) mContext).currentUserData.getName()));
                            restaurant.setLoan_agreement(restaurant.getLoan_agreement().replace("LENDER_USERNAME", ((BrowseActivity) mContext).currentUserData.getUsername()));

                            webView.loadData(restaurant.getLoan_agreement(),"text/html", "UTF-8");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                        CheckBox checkbox=(CheckBox) deleteDialogView.findViewById(R.id.checkBox);

                        deleteDialogView.findViewById(R.id.clear_signatures).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                mSignaturePad.clear();
                                signed=false;
                            }
                        });

                        deleteDialogView.findViewById(R.id.sign_agreement_btn).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //your business logic
                                if(!checkbox.isChecked())
                                {
                                    ((BrowseActivity)mContext).showMessage("Please agree to the loan agreement above");
                                }else{
                                    deleteDialogView.findViewById(R.id.sign_agreement_btn).setVisibility(View.GONE);
                                    deleteDialogView.findViewById(R.id.continue_btn).setVisibility(View.VISIBLE);
                                    sign_layout.setVisibility(View.VISIBLE);
                                    agreement_layout.setVisibility(View.GONE);

                                }
                            }
                        });

                        deleteDialogView.findViewById(R.id.continue_btn).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if(!signed)
                                {
                                    ((BrowseActivity)mContext).showMessage("Please input your signatures");
                                }else{

                                    deleteDialogView.findViewById(R.id.sign_agreement_btn).setVisibility(View.GONE);
                                    deleteDialogView.findViewById(R.id.continue_btn).setVisibility(View.GONE);
                                    deleteDialogView.findViewById(R.id.finish_btn).setVisibility(View.VISIBLE);
                                    sign_layout.setVisibility(View.GONE);
                                    agreement_layout.setVisibility(View.VISIBLE);
                                    ((TextView)deleteDialogView.findViewById(R.id.textView26)).setText("Review Signed Agreement");

                                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                                    Bitmap b=Bitmap.createScaledBitmap(mSignaturePad.getTransparentSignatureBitmap(), 150, 120, false);
                                    b.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                                    byte[] byteArray = byteArrayOutputStream.toByteArray();
                                    String imgageBase64 = Base64.encodeToString(byteArray, Base64.DEFAULT);
                                    String image = "data:image/png;base64," + imgageBase64;

                                    restaurant.setLoan_agreement(restaurant.getLoan_agreement().replace("{IMAGE_PLACEHOLDER}", image));

                                    try {
                                        webView.loadData(restaurant.getLoan_agreement(),"text/html", "UTF-8");
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }


                                }
                            }
                        });

                        deleteDialogView.findViewById(R.id.finish_btn).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                deleteDialog.dismiss();
                                ((BrowseActivity) mContext).showProgress();

                                Intent a=new Intent(mContext, LoanPayActivity.class);
                                a.putExtra("amount",restaurant.getAmount());
                                a.putExtra("user_id",restaurant.getBorrower());
                                a.putExtra("user_name",username.getText().toString());
                                a.putExtra("token",selected_user_token);
                                a.putExtra("offer_id",restaurant.getOffer_uid());
                                a.putExtra("type","requests");
                                a.putExtra("universal",true);
                                a.putExtra("universal_obj",restaurant);
                                a.putExtra("loan_agreement",restaurant.getLoan_agreement());
                                ((BrowseActivity) mContext).startActivity(a);
                            }
                        });

                        deleteDialog.show();
                    }else{
                        ((BrowseActivity)mContext).showMessage("Please activate your payment setup before making a request to someone");
                    }
                }
            });

            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // expandableLayout.setVisibility(View.VISIBLE);
                    expandableLayout.expand();
                }
            });
        }
    }
}