package com.munib.spotme;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
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

import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import static com.munib.spotme.utils.CommonUtils.TAGZ;

public class PaymentSetupActivity extends BaseActivity {

    WebView webView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_setup);

        webView=(WebView) findViewById(R.id.webview);

        webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                if(url.contains("https://example.com"))
                {
                    PaymentSetupActivity.this.finish();

                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                webView.loadUrl("javascript:(function() { " +
                        "document.getElementsByClassName('db-FooterLayout-footer')[0].remove();"+
                        "document.getElementsByClassName('Box-root Padding-top--4 Flex-flex Flex-alignItems--baseline Flex-direction--row')[0].remove();"+
                        "})()");
                hideProgress();
            }

        });
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);

        getAccountLink(auth.getCurrentUser().getUid());
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void getAccountLink(String uid)
    {
        showProgress();
        try {
            RequestQueue requestQueue = Volley.newRequestQueue(PaymentSetupActivity.this);
            String URL = "https://us-central1-spotme-39709.cloudfunctions.net/getAccountLinks";
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("user_id",uid);
            final String requestBody = jsonBody.toString();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.i("VOLLEY", response);
                    try {
                        JSONObject object = new JSONObject(response);
                        JSONObject data=new JSONObject(object.getString("data"));
                        if(data.getBoolean("Error"))
                        { hideProgress();
                            showMessage(data.getString("Message"));
                        }else{

                            String url=data.getJSONObject("url").getString("url");
                            Log.d("mubi",url);
                            webView.loadUrl(url);
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
                    hideProgress();
                    finish();
                    Toast.makeText(PaymentSetupActivity.this,"Technical Issue ! Please try again in few momments",Toast.LENGTH_LONG).show();
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

    public void getLoginLink(String uid)
    {
        showProgress();
        try {
            RequestQueue requestQueue = Volley.newRequestQueue(PaymentSetupActivity.this);
            String URL = "https://us-central1-spotme-39709.cloudfunctions.net/getLoginLink";
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("user_id",uid);
            final String requestBody = jsonBody.toString();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.i("VOLLEY", response);
                    try {
                        JSONObject object = new JSONObject(response);
                        JSONObject data=new JSONObject(object.getString("data"));
                        if(data.getBoolean("Error"))
                        { hideProgress();
                            showMessage(data.getString("Message"));
                        }else{

                            String url=data.getJSONObject("url").getString("url");
                         //   Log.d("mubi",url);
                            webView.loadUrl(url);
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

}