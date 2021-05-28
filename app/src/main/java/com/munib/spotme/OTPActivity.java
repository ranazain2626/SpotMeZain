package com.munib.spotme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthMultiFactorException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.MultiFactorAssertion;
import com.google.firebase.auth.MultiFactorInfo;
import com.google.firebase.auth.MultiFactorResolver;
import com.google.firebase.auth.MultiFactorSession;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.PhoneMultiFactorGenerator;
import com.google.firebase.auth.PhoneMultiFactorInfo;
import com.mukesh.OtpView;
import com.munib.spotme.base.BaseActivity;

import java.util.concurrent.TimeUnit;

public class OTPActivity extends BaseActivity {

    private FirebaseAuth mAuth;
    public PhoneAuthCredential credential;
    String verificationId;
    String phone_no;
    PhoneAuthProvider.ForceResendingToken forceResendingToken;
    PhoneAuthOptions phoneAuthOptions;
    OtpView otpView;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    MultiFactorResolver multiFactorResolver;
    TextView otp_msg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        pref= getSharedPreferences("MyPref", 0); // 0 - for private mode
        editor= pref.edit();

        otpView=findViewById(R.id.otp_view);
        otp_msg=findViewById(R.id.phone);
        phone_no=pref.getString("phone","");

        mAuth = FirebaseAuth.getInstance();
        mAuth.getCurrentUser().getMultiFactor().getSession()
                    .addOnCompleteListener(
                            new OnCompleteListener<MultiFactorSession>() {
                                @Override
                                public void onComplete(@NonNull Task<MultiFactorSession> task) {
                                    if (task.isSuccessful()) {

                                        MultiFactorSession multiFactorSession = task.getResult();
                                        phoneAuthOptions =
                                                PhoneAuthOptions.newBuilder()
                                                        .setPhoneNumber(phone_no)
                                                        .setTimeout(30L, TimeUnit.SECONDS)
                                                        .setMultiFactorSession(multiFactorSession)
                                                        .setCallbacks(callbacks)
                                                        .build();

                                        PhoneAuthProvider.verifyPhoneNumber(phoneAuthOptions);

                                    }
                                }
                            });

        Button verify = findViewById(R.id.verify);
        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showProgress();
                PhoneAuthCredential credential
                        = PhoneAuthProvider.getCredential(verificationId, otpView.getText().toString());

                MultiFactorAssertion multiFactorAssertion
                        = PhoneMultiFactorGenerator.getAssertion(credential);

                    FirebaseAuth.getInstance()
                            .getCurrentUser()
                            .getMultiFactor()
                            .enroll(multiFactorAssertion, phone_no)
                            .addOnCompleteListener(
                                    new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                SharedPreferences pref;
                                                SharedPreferences.Editor editor;
                                                pref = getSharedPreferences("MyPref", 0); // 0 - for private mode
                                                editor = pref.edit();
                                                editor.putBoolean("verified", true);
                                                editor.apply();

                                                Toast.makeText(OTPActivity.this, "Phone verified successfully", Toast.LENGTH_LONG).show();
                                                Intent i = new Intent(OTPActivity.this, MainActivity.class);
                                                startActivity(i);
                                                finish();
                                            } else {
                                                showMessage(task.toString());
                                            }
                                            hideProgress();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //showMessage(e.getMessage());
                            e.printStackTrace();
                            hideProgress();
                        }
                    });
            }
        });
    }

    PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks =
            new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                @Override
                public void onVerificationCompleted(PhoneAuthCredential credentials) {
                    // This callback will be invoked in two situations:
                    // 1) Instant verification. In some cases, the phone number can be
                    //    instantly verified without needing to send or enter a verification
                    //    code. You can disable this feature by calling
                    //    PhoneAuthOptions.builder#requireSmsValidation(true) when building
                    //    the options to pass to PhoneAuthProvider#verifyPhoneNumber().
                    // 2) Auto-retrieval. On some devices, Google Play services can
                    //    automatically detect the incoming verification SMS and perform
                    //    verification without user action.
                    credential = credentials;
                }
                @Override
                public void onVerificationFailed(FirebaseException e) {
                    // This callback is invoked in response to invalid requests for
                    // verification, like an incorrect phone number.
                    if (e instanceof FirebaseAuthInvalidCredentialsException) {
                        showMessage(e.getMessage());
                    } else if (e instanceof FirebaseTooManyRequestsException) {
                        // The SMS quota for the project has been exceeded
                        // ...
                        showMessage(e.getMessage());
                    }else{
                        showMessage(e.getMessage());
                    }
                    // Show a message and update the UI
                    // ...
                }
                @Override
                public void onCodeSent(
                        String verificationIds, PhoneAuthProvider.ForceResendingToken token) {
                    // The SMS verification code has been sent to the provided phone number.
                    // We now need to ask the user to enter the code and then construct a
                    // credential by combining the code with a verification ID.
                    // Save the verification ID and resending token for later use.
                    verificationId = verificationIds;
                    forceResendingToken = token;

                    otp_msg.setText("Please enter the OTP sent to "+phone_no);

                      // ...
                }
            };
}