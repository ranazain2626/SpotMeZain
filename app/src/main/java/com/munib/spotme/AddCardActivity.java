package com.munib.spotme;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.munib.spotme.base.BaseActivity;
import com.munib.spotme.dataModels.OffersModel;
import com.stripe.android.ApiResultCallback;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.SetupIntentResult;
import com.stripe.android.Stripe;
import com.stripe.android.model.ConfirmSetupIntentParams;
import com.stripe.android.model.PaymentMethod;
import com.stripe.android.model.PaymentMethodCreateParams;
import com.stripe.android.model.SetupIntent;
import com.stripe.android.view.CardMultilineWidget;

import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;

public class AddCardActivity extends BaseActivity {

    CardMultilineWidget cardMultilineWidget;
    TextView add_card;
    Stripe stripe;
    private String paymentIntentClientSecret,user_id,token;
    public String uid,type,loan_agreement;
    int amount;
    OffersModel universal_data;
    boolean universal=false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_card_activity);

        cardMultilineWidget=(CardMultilineWidget) findViewById(R.id.cardMultiLineWidget);
        add_card=(TextView) findViewById(R.id.add_card);

        add_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(AddCardActivity.this);
                Boolean charges_enabled = preferences.getBoolean("charges_enabled", false);
                if(charges_enabled) {
                    if(cardMultilineWidget.getCardParams()!=null) {
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(AddCardActivity.this);
                        builder1.setMessage("Do you really want to add this card?");
                        builder1.setCancelable(true);

                        builder1.setPositiveButton(
                                "Add",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {

                                        startCardAdd(currentUserData.getUid());

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
                    else
                        showMessage("Please input valid card information");
                }else{
                    showMessage("Please complete your payment setup before making a payment");
                }
            }
        });

        stripe = new Stripe(
                this,
                PaymentConfiguration.getInstance(this).getPublishableKey()
        );

    }

    public void startCardAdd(String user_id)
    {
        showProgress();
        try {
            RequestQueue requestQueue = Volley.newRequestQueue(AddCardActivity.this);
            String URL = "https://us-central1-spotme-39709.cloudfunctions.net/saveNewCard";
            JSONObject jsonBody = new JSONObject();
            // jsonBody.put("offer_id",offer_uid);
            jsonBody.put("user_id",user_id);
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
                        }else{

                            String url=data.getString("clientSecret");
                            Log.d("mubi",url);
                            paymentIntentClientSecret=url;

                            PaymentMethodCreateParams.Card card = cardMultilineWidget.getPaymentMethodCard();

                            PaymentMethod.BillingDetails billingDetails =
                                    new PaymentMethod.BillingDetails.Builder()
                                            .build();
                            // Create SetupIntent confirm parameters with the above
                            PaymentMethodCreateParams paymentMethodParams = PaymentMethodCreateParams
                                    .create(card, billingDetails);
                            ConfirmSetupIntentParams confirmParams = ConfirmSetupIntentParams
                                    .create(paymentMethodParams, paymentIntentClientSecret);
                            stripe.confirmSetupIntent(AddCardActivity.this, confirmParams);

                        }

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
                    Toast.makeText(AddCardActivity.this,"Something went wrong! Try again later",Toast.LENGTH_LONG).show();
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
       // stripe.onPaymentResult(requestCode, data, new PaymentResultCallback(LoanPayActivity1.this,uid,type,loan_agreement,token,user_id,amount));
        WeakReference<Activity> weakActivity = new WeakReference<>(this);

        // Handle the result of stripe.confirmSetupIntent
        stripe.onSetupResult(requestCode, data, new ApiResultCallback<SetupIntentResult>() {
            @Override
            public void onSuccess(@NonNull SetupIntentResult result) {
                SetupIntent setupIntent = result.getIntent();
                SetupIntent.Status status = setupIntent.getStatus();
                if (status == SetupIntent.Status.Succeeded) {
                   Log.d("success",result.toString());
                   Toast.makeText(AddCardActivity.this,"Card Added Successfully!",Toast.LENGTH_LONG).show();
                   finish();
                } else if (status == SetupIntent.Status.RequiresPaymentMethod) {
                    // Setup failed – allow retrying
                    Log.d("failure",result.toString());
                    showMessage(result.toString());
                }

                hideProgress();
            }

            @Override
            public void onError(@NonNull Exception e) {
                // Setup request failed
            }
        });
    }
}
