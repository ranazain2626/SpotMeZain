package com.munib.spotme.utils;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Size;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.stripe.android.EphemeralKeyProvider;
import com.stripe.android.EphemeralKeyUpdateListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class ExampleEphemeralKeyProvider implements EphemeralKeyProvider {

    Context context;
    public ExampleEphemeralKeyProvider(Context context)
    {
        this.context=context;
    }

    @Override
    public void createEphemeralKey(
            @NonNull @Size(min = 4) String apiVersion,
            @NonNull final EphemeralKeyUpdateListener keyUpdateListener) {

        RequestQueue requestQueue= Volley.newRequestQueue(context);
        try {
            String URL = "https://us-central1-spotme-39709.cloudfunctions.net/getEphemeralKey";
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("user_id", FirebaseAuth.getInstance().getCurrentUser().getUid());

            final String requestBody = jsonBody.toString();

            StringRequest stringRequest1 = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.i("VOLLEY1", response);
//                    try {
//                        JSONObject object = new JSONObject(response);
//                        JSONObject data=new JSONObject(object.getString("data"));
//                        if(data.getBoolean("Error"))
//                        { hideProgress();
//                            showMessage(data.getString("Message"));
//                        }else{
//
//                          //  String url=data.getJSONObject("url").getString("url");
//                           // Log.d("mubi",url);
//                         //   webView.loadUrl(url);
//                        }
//                    }catch (Exception ex)
//                    {
//                        ex.printStackTrace();
//                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("VOLLEY1", error.toString());
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

            requestQueue.add(stringRequest1);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}