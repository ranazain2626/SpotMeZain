package com.munib.spotme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.Login;
import com.google.android.gms.tasks.OnCompleteListener;
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
import com.munib.spotme.base.BaseActivity;
import com.munib.spotme.dataModels.BusinessProfileModel;
import com.munib.spotme.dataModels.UserModel;
import com.munib.spotme.utils.CommonUtils;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

public class LoginActivity extends BaseActivity {

    String TAG="SpotMe";

    LinearLayout ll_already_acc;
    TextView txt_sign_in,txtForgotPassword;
    EditText email,password;
    String verificationId;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        email=(EditText) findViewById(R.id.edtEmail);
        password=(EditText) findViewById(R.id.edtPassword);
        txtForgotPassword=(TextView) findViewById(R.id.txtForgotPassword);
        txtForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater factory = LayoutInflater.from(LoginActivity.this);
                final View deleteDialogView = factory.inflate(R.layout.dialog_forgot_password, null);
                final AlertDialog deleteDialog = new AlertDialog.Builder(LoginActivity.this).create();
                deleteDialog.setView(deleteDialogView);
                deleteDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

                EditText email=(EditText) deleteDialogView.findViewById(R.id.edtEmail);
                deleteDialogView.findViewById(R.id.continue_btn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //your business logic
                        if(email.getText().toString().equals(""))
                        {
                            email.setError("Please input email address");
                        }else{
                            mAuth.sendPasswordResetEmail(email.getText().toString())
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                showMessage("Email Sent. Please reset password from email");
                                            }else{
                                                showMessage("No such user found!");
                                            }
                                        }
                                    });
                        }
                    }
                });

                deleteDialog.show();
            }
        });

        txt_sign_in=(TextView) findViewById(R.id.txt_sign_in);
        txt_sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(email.getText().toString().equals(""))
                {
                    email.setError("Please input email address");
                }else if(password.getText().toString().equals(""))
                {
                    password.setError("Please input your password");
                }else {
                    showProgress();

                    mAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                            .addOnCompleteListener(
                                    new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (task.isSuccessful()) {
                                                Log.d("mubi","success");
                                                database = FirebaseDatabase.getInstance();
                                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users");
                                                if(mAuth.getCurrentUser()!=null) {

                                                    ref.child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                            Log.d("mubi",snapshot.toString());
                                                            if(snapshot.exists())
                                                            {
                                                                currentUserData = snapshot.getValue(UserModel.class);
                                                                currentUserData.setUid(snapshot.getKey());
                                                                if(currentUserData.getBlocked()==1)
                                                                {
                                                                    showMessage("User is blocked! Please access customer support");
                                                                    mAuth.signOut();
                                                                }else{
                                                                    SharedPreferences pref;
                                                                    SharedPreferences.Editor editor;
                                                                    pref= getSharedPreferences("MyPref", 0); // 0 - for private mode
                                                                    editor= pref.edit();

                                                                    editor.putString("email",email.getText().toString());
                                                                    editor.putString("phone",currentUserData.getPhone());
                                                                    editor.apply();
                                                                    Intent intent=new Intent(LoginActivity.this,OTPActivity.class);
                                                                    startActivity(intent);
                                                                }
                                                            }else {
                                                                // If sign in fails, display a message to the user.
                                                                // If sign in fails, display a message to the user.
                                                                Log.w(TAG, "signInWithEmail:failure", task.getException());
                                                                showMessage(task.getException().toString());
                                                                //  updateUI(null);
                                                            }
                                                            hideProgress();
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {

                                                        }
                                                    });
                                                }

                                                return;
                                            }
                                            if (task.getException() instanceof FirebaseAuthMultiFactorException) {

                                                MultiFactorResolver multiFactorResolver = ((FirebaseAuthMultiFactorException) task.getException()).getResolver();
                                                Intent intent=new Intent(LoginActivity.this,OTPLoginActivity.class);

                                                intent.putExtra("signup",false);
                                                intent.putExtra("task",multiFactorResolver);
                                                startActivity(intent);


                                            } else {
                                                // If sign in fails, display a message to the user.
                                                Log.w(TAG, "signInWithEmail:failure", task.getException());
                                                showMessage(task.getException().getMessage());
                                            }
                                            hideProgress();
                                        }
                                    });
                }
            }
        });

        ll_already_acc=(LinearLayout) findViewById(R.id.ll_already_acc);
        ll_already_acc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this,SignupActivity.class));
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
       // updateUI(currentUser);
    }
}