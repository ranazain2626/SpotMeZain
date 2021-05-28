package com.munib.spotme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.munib.spotme.base.BaseActivity;
import com.stripe.android.view.PaymentMethodsActivityStarter;

public class SplashActivity extends BaseActivity {

    private FirebaseAuth mAuth;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        pref= getSharedPreferences("MyPref", 0); // 0 - for private mode
        editor= pref.edit();

        String email = pref.getString("email", "");
        Boolean verified=pref.getBoolean("verified",false);


        // editor.putBoolean("details_submitted",details_submitted);

        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                        // Get deep link from result (may be null if no link is found)
                        Uri deepLink = null;
                        if (pendingDynamicLinkData != null) {
                            deepLink = pendingDynamicLinkData.getLink();
                            //  Intent intent = getIntent();
                            //  String emailLink = intent.getData().toString();
                            Log.d("links",deepLink+" -- "+email);

                            if (auth.isSignInWithEmailLink(deepLink.toString())) {
                                // Retrieve this from wherever you stored it
                                // The client SDK will parse the code from the link for you.
                                auth.signInWithEmailLink(email, deepLink.toString())
                                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(SplashActivity.this, "Successfully signed in with email link!",Toast.LENGTH_LONG).show();
                                                    AuthResult result = task.getResult();

                                                    Intent i = new Intent(SplashActivity.this, OTPActivity.class);
                                                    i.putExtra("phone", "+923426122499");
                                                    startActivity(i);
                                                    finish();

                                                } else {
                                                    Toast.makeText(SplashActivity.this, "Error signing in with email link",Toast.LENGTH_LONG).show();

                                                    Log.e("mubi", "Error signing in with email link", task.getException());
                                                }
                                            }
                                        });
                            }
                        }
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("mubi", "getDynamicLink:onFailure", e);
                    }
                });

        mAuth = FirebaseAuth.getInstance();
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {

                FirebaseUser currentUser = mAuth.getCurrentUser();
                if(currentUser!=null)
                {
                    if(currentUser.isEmailVerified()) {
                        if(verified)
                        {
                            if(pref.getBoolean("pin_enabled",false))
                            {

                                Intent i = new Intent(SplashActivity.this, VerifyPinActivity.class);
                                startActivity(i);
                                finish();
                            }else{
                                Intent i = new Intent(SplashActivity.this, MainActivity.class);
                                startActivity(i);
                                finish();
                            }

                        }else{
                            Intent i = new Intent(SplashActivity.this, OTPActivity.class);
                            startActivity(i);
                            finish();
                        }
                    }else{

                        showMessage("Please verify the link sent to your email & try again");
                    }

                }else{
                    mAuth.signOut();
                    Intent i = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(i);

                    finish();
                }

            }
        }, 3000);
    }

}