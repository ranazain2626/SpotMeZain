package com.munib.spotme;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.stripe.android.Stripe;

import java.io.IOException;

public class AgreementActivity extends AppCompatActivity {
    TextView type;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loan_agreement);
        String url=getIntent().getStringExtra("url");

        ImageView back_btn=(ImageView) findViewById(R.id.back_btn);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        WebView webView=(WebView) findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());

        // If you deal with HTML then execute loadData instead of loadUrl
        try {
            webView.loadData(url,"text/html", "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
