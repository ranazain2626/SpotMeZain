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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mukesh.OtpView;
import com.munib.spotme.base.BaseActivity;
import com.munib.spotme.dataModels.UserModel;

import java.util.concurrent.TimeUnit;

public class OTPLoginActivity extends BaseActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        pref= getSharedPreferences("MyPref", 0); // 0 - for private mode
        editor= pref.edit();
        mAuth=FirebaseAuth.getInstance();

        otpView=findViewById(R.id.otp_view);
        phone_no=pref.getString("phone","");

        multiFactorResolver= (MultiFactorResolver) getIntent().getParcelableExtra("task");
        MultiFactorInfo selectedHint = multiFactorResolver.getHints().get(0);

        PhoneAuthOptions phoneAuthOptions =
                    PhoneAuthOptions.newBuilder()
                            .setMultiFactorHint((PhoneMultiFactorInfo) selectedHint)
                            .setTimeout(30L, TimeUnit.SECONDS)
                            .setMultiFactorSession(multiFactorResolver.getSession())
                            .setCallbacks(callbacks)
                            .requireSmsValidation(true)
                            .build();

            PhoneAuthProvider.verifyPhoneNumber(phoneAuthOptions);

        Button verify = findViewById(R.id.verify);
        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showProgress();
                PhoneAuthCredential credential
                        = PhoneAuthProvider.getCredential(verificationId, otpView.getText().toString());

                MultiFactorAssertion multiFactorAssertion
                        = PhoneMultiFactorGenerator.getAssertion(credential);

                    multiFactorResolver
                            .resolveSignIn(multiFactorAssertion)
                            .addOnCompleteListener(
                                    new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (task.isSuccessful()) {
                                                AuthResult authResult = task.getResult();
                                                SharedPreferences pref;
                                                SharedPreferences.Editor editor;
                                                pref = getSharedPreferences("MyPref", 0); // 0 - for private mode
                                                editor = pref.edit();
                                                editor.putBoolean("verified", true);
                                                editor.apply();



                                                database = FirebaseDatabase.getInstance();
                                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users");
                                                if(mAuth.getCurrentUser()!=null) {
                                                    Log.d("mubi","inside1");
                                                    ref.child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                            Log.d("mubi","inside2");
                                                            if(snapshot.exists())
                                                            {
                                                                currentUserData = snapshot.getValue(UserModel.class);
                                                                currentUserData.setUid(snapshot.getKey());
                                                                if(currentUserData.getBlocked()==1)
                                                                {
                                                                    showMessage("User is blocked! Please access customer support");
                                                                    mAuth.signOut();
                                                                //    finish();
                                                                }else{
                                                                    Toast.makeText(OTPLoginActivity.this, "Phone verified successfully", Toast.LENGTH_LONG).show();
                                                                    Intent i = new Intent(OTPLoginActivity.this, MainActivity.class);
                                                                    startActivity(i);
                                                                    finish();
                                                                }
                                                            }else {
                                                                // If sign in fails, display a message to the user.
                                                                Log.w("TAG", "signInWithEmail:failure", task.getException());
                                                                showMessage(task.getException().toString());
                                                            }
                                                            hideProgress();
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {

                                                        }
                                                    });
                                                }

                                            }else{
                                                hideProgress();
                                                showMessage(task.toString());
                                            }
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
                    showMessage("OTP sent");
                    // ...
                }
            };
}