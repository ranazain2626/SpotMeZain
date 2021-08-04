package com.munib.spotme;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.mukesh.OtpView;
import com.munib.spotme.base.BaseActivity;

public class VerifyPinActivity extends BaseActivity {

    OtpView otpView;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    TextView forgot_pin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_pin);

        pref= getSharedPreferences("MyPref", 0); // 0 - for private mode
        editor= pref.edit();

        otpView=findViewById(R.id.otp_view2);
        forgot_pin=findViewById(R.id.forgot_pin);

        Button save = findViewById(R.id.a);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(otpView.getText().toString().equals(pref.getString("login_pin","")))
                {
                    Intent i = new Intent(VerifyPinActivity.this, MainActivity.class);
                    startActivity(i);
                    finish();
                }else{
                    showMessage("Invalid PIN !");
                }
            }
        });

        forgot_pin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences pref;
                SharedPreferences.Editor editor;
                pref= getSharedPreferences("MyPref", 0); // 0 - for private mode
                editor= pref.edit();
                editor.clear();
                editor.apply();

                FirebaseAuth.getInstance().signOut();
                finish();
                startActivity(new Intent(VerifyPinActivity.this, LoginActivity.class));
            }
        });

    }
}