package com.munib.spotme;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.munib.spotme.adapters.NotificationsAdapter;
import com.munib.spotme.adapters.PaymentCardsAdapter;
import com.munib.spotme.base.BaseActivity;
import com.munib.spotme.dataModels.CardModel;
import com.munib.spotme.dataModels.OffersModel;
import com.munib.spotme.utils.RecyclerItemClickListener;
import com.squareup.picasso.Picasso;
import com.stripe.android.ApiResultCallback;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.PaymentIntentResult;
import com.stripe.android.Stripe;
import com.stripe.android.model.ConfirmPaymentIntentParams;
import com.stripe.android.model.PaymentIntent;
import com.stripe.android.model.PaymentMethodCreateParams;
import com.stripe.android.view.CardMultilineWidget;

import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.munib.spotme.utils.CommonUtils.TAGZ;

public class LoanPayActivity extends BaseActivity {

    CardMultilineWidget cardMultilineWidget;
    TextView pay_btn;
    Stripe stripe;
    private String paymentIntentClientSecret,user_id,token;
    public String uid,type,loan_agreement;
    int amount;
    OffersModel universal_data;
    boolean universal=false;
    CardView add_card;
    RecyclerView cards_rv;
    ArrayList<CardModel> cardsArrayList=new ArrayList<>();
    PaymentCardsAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment_activity);

        cards_rv=(RecyclerView)findViewById(R.id.cards_rv);
        cards_rv.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        adapter=new PaymentCardsAdapter(this,cardsArrayList);
        cards_rv.setAdapter(adapter);

        cards_rv.addOnItemTouchListener(
                new RecyclerItemClickListener(LoanPayActivity.this, cards_rv ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        // do whatever
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(LoanPayActivity.this);
                        builder1.setMessage("Submit Payment? SpotMe charges a 5% service fee");
                        builder1.setCancelable(true);

                        builder1.setPositiveButton(
                                "Pay Now",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {

                                        startCheckout(user_id,amount,cardsArrayList.get(position).getId());
                                        dialog.cancel();
                                    }
                                });

                        builder1.setNegativeButton(
                                "Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });

                        AlertDialog alert11 = builder1.create();
                        alert11.show();
                    }

                    @Override
                    public void onItemLongClick(View view, int position) {

                    }

                })
        );

        ImageView back_btn=(ImageView) findViewById(R.id.back_btn);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        uid=getIntent().getStringExtra("offer_id");
        user_id=getIntent().getStringExtra("user_id");
        type=getIntent().getStringExtra("type");
        token=getIntent().getStringExtra("token");
        loan_agreement=getIntent().getStringExtra("loan_agreement");
        amount=Integer.parseInt(getIntent().getStringExtra("amount"));
        try {
            universal = getIntent().getBooleanExtra("universal", false);
            universal_data = (OffersModel) getIntent().getSerializableExtra("universal_obj");
        }catch (Exception ex)
        {

        }

        add_card=findViewById(R.id.add_card);
        add_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoanPayActivity.this,AddCardActivity.class));
            }
        });


        stripe = new Stripe(
                this,
                PaymentConfiguration.getInstance(this).getPublishableKey()
        );

    }

    @Override
    protected void onResume() {
        super.onResume();
        getSavedCards(FirebaseAuth.getInstance().getCurrentUser().getUid());

    }

    public void startCheckout(String user_id, int amount, String card_id)
    {
        showProgress();
        try {
            RequestQueue requestQueue = Volley.newRequestQueue(LoanPayActivity.this);
            String URL = "https://us-central1-spotme-39709.cloudfunctions.net/makePaymentWithCard";
            JSONObject jsonBody = new JSONObject();
            // jsonBody.put("offer_id",offer_uid);
            jsonBody.put("user_id",user_id);
            jsonBody.put("customer_id",currentUserData.getCustomer_id());
            jsonBody.put("method_id",card_id);
            //jsonBody.put("type","requests");
            jsonBody.put("amount",amount);
            final String requestBody = jsonBody.toString();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.i("VOLLEY", response);
                    try {
                        JSONObject object = new JSONObject(response);
                        JSONObject data=new JSONObject(object.getString("data"));
                        if(data.getBoolean("Error"))
                        {
                            hideProgress();
                            showMessage("Something wrong with the payment.Please check your card for funds Or contact your bank");
                        }else{

                            String url=data.getJSONObject("clientSecret").getString("id");
                            if(url!="")
                            {
                                if(!universal) {
                                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(type).child(uid);
                                    try {
                                        ref.child("paymentIntent").setValue(data.getJSONObject("clientSecret"));
                                    } catch (Exception ex) {

                                    }
                                    ref.child("status").setValue(2);
                                    ref.child("loan_agreement").setValue(loan_agreement);
                                    ref.child("agreement_signed_lender").setValue(true);
                                    ref.child("agreement_signed_borrower").setValue(true);

                                    Toast.makeText(LoanPayActivity.this, "Payment Completed", Toast.LENGTH_LONG).show();

                                    if (token != null) {
                                        sendNotification(currentUserData.getName() + " has loaned you $" + amount + ". It will be deposited into your account shortly", token);
                                        FirebaseDatabase.getInstance().getReference("notifications").child(user_id).push().child("notification").setValue(currentUserData.getName() + " has loaned you $" + amount + ". It will be deposited into your account within 2 days");
                                    }
                                }else{
                                    universal_data.setStatus(2);
                                    universal_data.setLoan_agreement(loan_agreement);
                                    universal_data.setAgreement_signed_borrower(true);
                                    universal_data.setAgreement_signed_lender(true);
                                    universal_data.setRequest_message("");
                                    universal_data.setLender(getCurrentUserData().getUid());

                                    database.getReference(type).child(uid).setValue(universal_data);
                                    try {
                                        database.getReference(type).child(uid).child("paymentIntent").setValue(data.getJSONObject("clientSecret"));

                                    } catch (Exception ex) {

                                    }
                                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("universal_requests").child(uid);
                                    ref.child("status").setValue(-1);


                                    Toast.makeText(LoanPayActivity.this, "Payment Completed", Toast.LENGTH_LONG).show();

                                    if (token != null) {
                                        sendNotification(currentUserData.getName() + " has loaned you $" + amount + ". It will be deposited into your account shortly", token);
                                        FirebaseDatabase.getInstance().getReference("notifications").child(user_id).push().child("notification").setValue(currentUserData.getName() + " has loaned you $" + amount + ". It will be deposited into your account within 2 days");
                                    }
                                }
                                Toast.makeText(LoanPayActivity.this,"Payment Successful",Toast.LENGTH_LONG).show();
                                hideProgress();

                                startActivity(new Intent(LoanPayActivity.this,MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));

                            }

                        }
                    }catch (Exception ex)
                    {
                        ex.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("VOLLEY", error.toString());
                    Toast.makeText(LoanPayActivity.this,"Something went wrong! Try again later",Toast.LENGTH_LONG).show();
                    hideProgress();
                }
            }) {
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                @Override
                public byte[] getBody() throws AuthFailureError {
                    try {
                        return requestBody == null ? null : requestBody.getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                        return null;
                    }
                }
            };
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(stringRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getSavedCards(String user_id)
    {
          showProgress();
        try {
            RequestQueue requestQueue = Volley.newRequestQueue(LoanPayActivity.this);
            String URL = "https://us-central1-spotme-39709.cloudfunctions.net/getCards";
            JSONObject jsonBody = new JSONObject();
            // jsonBody.put("offer_id",offer_uid);
            jsonBody.put("user_id",user_id);
            //jsonBody.put("type","requests");
            final String requestBody = jsonBody.toString();
            cardsArrayList.clear();
            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.i("VOLLEY", response);
                    try {
                        JSONObject object = new JSONObject(response);
                        JSONObject data=new JSONObject(object.getString("data"));
                        if(data.getBoolean("Error"))
                        {
                            hideProgress();
                        }else{
                            JSONArray array=data.getJSONObject("response").getJSONArray("data");
                            for(int i=0;i<array.length();i++)
                            {
                                CardModel model=new CardModel(array.getJSONObject(i).getString("id"),array.getJSONObject(i).getJSONObject("card").getString("brand"),"",array.getJSONObject(i).getJSONObject("card").getInt("exp_month")+"",array.getJSONObject(i).getJSONObject("card").getInt("exp_year")+"",array.getJSONObject(i).getJSONObject("card").getString("last4"));
                                cardsArrayList.add(model);
                            }
                            Log.d("mubi",cardsArrayList.size()+"");
                            adapter.notifyDataSetChanged();

                        }
                        hideProgress();
                    }catch (Exception ex)
                    {
                        hideProgress();
                        ex.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    hideProgress();
                    Log.e("VOLLEY", error.toString());
                    Toast.makeText(LoanPayActivity.this,"Something went wrong! Try again later",Toast.LENGTH_LONG).show();
                    hideProgress();
                }
            }) {
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                @Override
                public byte[] getBody() throws AuthFailureError {
                    try {
                        return requestBody == null ? null : requestBody.getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                        return null;
                    }
                }
            };

            requestQueue.add(stringRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @androidx.annotation.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Handle the result of stripe.confirmPayment
        stripe.onPaymentResult(requestCode, data, new PaymentResultCallback(LoanPayActivity.this,uid,type,loan_agreement,token,user_id,amount));

    }

    private final class PaymentResultCallback
            implements ApiResultCallback<PaymentIntentResult> {
        @NonNull
        private final WeakReference<LoanPayActivity> activityRef;
        String uid,type,loan_agreement,userName,token,user_id;
        int amount;

        PaymentResultCallback(@NonNull LoanPayActivity activity, String uid, String type, String loan_agreement,String token,String user_id,int amount) {
            activityRef = new WeakReference<>(activity);
            this.uid=uid;
            this.type=type;
            this.user_id=user_id;
            this.loan_agreement=loan_agreement;
           // this.userName=userName;
            this.token=token;
            this.amount=amount;
        }

        @Override
        public void onSuccess(@NonNull PaymentIntentResult result) {
            final LoanPayActivity activity = activityRef.get();
            if (activity == null) {
      //          activity.hideProgress();
                return;
            }

            PaymentIntent paymentIntent = result.getIntent();
            PaymentIntent.Status status = paymentIntent.getStatus();
            if (status == PaymentIntent.Status.Succeeded) {
                // Payment completed successfully
                Gson gson = new GsonBuilder().setPrettyPrinting().create();



            } else if (status == PaymentIntent.Status.RequiresPaymentMethod) {
                // Payment failed

                activity.showMessage("Payment failed");

//                activity.displayAlert(
//                        "Payment failed",
//                        Objects.requireNonNull(paymentIntent.getLastPaymentError()).getMessage(),
//                        false
//                );
            }

        }

        @Override
        public void onError(@NonNull Exception e) {
            final LoanPayActivity activity = activityRef.get();
           // activity.hideProgress();
            if (activity == null) {
                return;
            }

            // Payment request failed – allow retrying using the same payment method
            Log.d("mubi", e.toString());
        }
    }
}
