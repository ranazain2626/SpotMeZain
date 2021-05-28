package com.munib.spotme;

import androidx.annotation.NonNull;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hbb20.CountryCodePicker;
import com.munib.spotme.base.BaseActivity;
import com.munib.spotme.dataModels.UserModel;
import com.tsongkha.spinnerdatepicker.DatePicker;
import com.tsongkha.spinnerdatepicker.DatePickerDialog;
import com.tsongkha.spinnerdatepicker.SpinnerDatePickerDialogBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static com.munib.spotme.utils.CommonUtils.TAGZ;

public class SignupActivity extends BaseActivity implements DatePickerDialog.OnDateSetListener {
    TextView terms_btn,privacy_btn,register_btn;
    EditText name,phone,email,social_security,address2,city,state,zip,dob,password,confirmPassword,address,username;
    CheckBox agree_terms_chec;
    private FirebaseAuth mAuth;
    SimpleDateFormat formater1 = new SimpleDateFormat("dd-MM-yyy");
    Boolean username_available=false;
    CountryCodePicker countryCodePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        mAuth = FirebaseAuth.getInstance();

        name=(EditText) findViewById(R.id.edtName);
        countryCodePicker=(CountryCodePicker) findViewById(R.id.countryCodePicker);
        phone=(EditText) findViewById(R.id.edtPhone);
        email=(EditText) findViewById(R.id.edtEmail);
//        social_security=(EditText) findViewById(R.id.edtSocialSecurity);
//        employment_id=(EditText) findViewById(R.id.edtEmploymentId);
        dob=(EditText) findViewById(R.id.edtDob);
        agree_terms_chec=(CheckBox) findViewById(R.id.checkAgreeTerms);
        register_btn=(TextView) findViewById(R.id.register_btn);
        password=(EditText) findViewById(R.id.edtPassword);
        confirmPassword=(EditText) findViewById(R.id.edtConfirmPasssword);
        address=(EditText) findViewById(R.id.edtAddress);
        address2=(EditText) findViewById(R.id.edtAddress2);
        city=(EditText) findViewById(R.id.edtCity);
        state=(EditText) findViewById(R.id.edtState);
        zip=(EditText) findViewById(R.id.edtZip);
        username=(EditText) findViewById(R.id.edtUsername);

        username.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                if(!s.toString().equals("")) {
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users");
                    ref.orderByChild("username").equalTo(s.toString().toLowerCase()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                for (DataSnapshot datas : dataSnapshot.getChildren()) {
                                    String keys = datas.getKey();
                                    Log.d(TAGZ, datas.getKey());
                                    username.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_close_24, 0);
                                    username_available = false;
                                }
                            } else {
                                username.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_check_24, 0);
                                username_available = true;
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.d(TAGZ, error.getDetails());

                        }
                    });
                }else{
                    username_available=false;
                    username.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if(s.toString().equals(""))
                {
                    username.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                }

            }
        });
        dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new SpinnerDatePickerDialogBuilder()
                        .context(SignupActivity.this)
                        .callback(SignupActivity.this)
                        .spinnerTheme(R.style.NumberPickerStyle)
                        .showTitle(true)
                        .showDaySpinner(true)
                        .defaultDate(2017, 0, 1)
                        .maxDate(2020, 0, 1)
                        .minDate(1900, 0, 1)
                        .build()
                        .show();
            }
        });

        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(name.getText().toString().equals(""))
                {
                    name.setError("Please input Name");
                }else if(!username_available){
                    showMessage("Username not available");
                } else if(phone.getText().toString().equals(""))
                {
                    showMessage("Please input complete Phone Number");
                }else if(email.getText().toString().equals(""))
                {
                    email.setError("Please input Email Address");
                }
//                else if(address.getText().toString().equals(""))
//                {
//                    address.setError("Please input complete postal address");
//                }
//                else if(city.getText().toString().equals(""))
//                {
//                    address.setError("Please input city");
//                }
//                else if(state.getText().toString().equals(""))
//                {
//                    address.setError("Please input state");
//                }
//                else if(zip.getText().toString().equals(""))
//                {
//                    address.setError("Please input zip code");
//                }
//                else if(social_security.getText().toString().equals(""))
//                {
//                    social_security.setError("Please input Social Security Number");
//                }else if(employment_id.getText().toString().equals(""))
//                {
//                    employment_id.setError("Please input Employment ID");
//                }
//                else if(dob.getText().toString().equals(""))
//                {
//                    dob.setError("Please input your DOB");
//                }
                else if(!agree_terms_chec.isChecked())
                {
                    showMessage("Please accept to terms & conditions and privacy to register");
                }else if(password.getText().toString().equals(""))
                {
                    showMessage("Please enter your password");
                }
                else if(confirmPassword.getText().toString().equals(""))
                {
                    showMessage("Please confirm your password");
                }
                else if(!isValidEmail(email.getText().toString())){
                    showMessage("Please enter a valid email");
                }
                else if(!(password.getText().toString().equals(confirmPassword.getText().toString())))
                {
                    showMessage("Password Doesn't match");
                }else{
                    showProgress();

                    mAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                            .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        SharedPreferences pref;
                                        SharedPreferences.Editor editor;
                                        pref= getSharedPreferences("MyPref", 0); // 0 - for private mode
                                        editor= pref.edit();

                                        editor.putString("email",email.getText().toString());
                                        editor.putString("phone",countryCodePicker.getSelectedCountryCodeWithPlus()+phone.getText());
                                        editor.apply();

                                        ActionCodeSettings actionCodeSettings =
                                                ActionCodeSettings.newBuilder()
                                                        // URL you want to redirect back to. The domain (www.example.com) for this
                                                        // URL must be whitelisted in the Firebase Console.
                                                        .setUrl("https://us-central1-spotme-39709.cloudfunctions.net/loginEmailLink?uid="+mAuth.getCurrentUser().getUid())
                                                        // This must be true
                                                        .setHandleCodeInApp(true)
                                                        .setIOSBundleId("com.example.ios")
                                                        .setAndroidPackageName(
                                                                "com.munib.spotme",
                                                                true, /* installIfNotAvailable */
                                                                "12"    /* minimumVersion */)
                                                        .build();

                                        mAuth.sendSignInLinkToEmail(email.getText().toString(), actionCodeSettings)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Log.d(TAGZ, "createUserWithEmail:success");
                                                            FirebaseUser user = mAuth.getCurrentUser();
                                                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(name.getText().toString()).build();
                                                            user.updateProfile(profileUpdates);
                                                            UserModel userModel=new UserModel(name.getText().toString(),username.getText().toString().toLowerCase(),countryCodePicker.getSelectedCountryCodeWithPlus()+phone.getText().toString(),email.getText().toString(),password.getText().toString(),"","",address.getText().toString(),dob.getText().toString(),user.getUid(),"","",address2.getText().toString(),city.getText().toString(),state.getText().toString(),zip.getText().toString(),0,new Date().getTime()+"");
                                                            database.getReference("users").child(user.getUid()).setValue(userModel);
                                                            makeStripeAccount(user.getUid());
                                                        }else{
                                                            showMessage(task.toString());
                                                        }
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                e.printStackTrace();
                                            }
                                        });

                                       // updateUI(user);
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Log.w(TAGZ, "createUserWithEmail:failure", task.getException());
                                         showMessage(task.getException().getMessage());
                                     //   updateUI(null);
                                    }
                                    hideProgress();

                                }
                            });

                }
            }
        });

        terms_btn=(TextView) findViewById(R.id.terms_btn);
        privacy_btn=(TextView) findViewById(R.id.privacy_btn);
        privacy_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent a=new Intent(SignupActivity.this,WebviewActivity.class);
                a.putExtra("type","Privacy Policy");
                a.putExtra("url","<p class=\"p1\"><span class=\"s1\"><strong>SPOTME PRIVACY POLICY</strong></span></p>\n" +
                        "<p class=\"p2\"><span class=\"s1\"><strong>Introduction</strong></span></p>\n" +
                        "<p class=\"p2\"><span class=\"s1\">Thank you for choosing SpotMe</span></p>\n" +
                        "<p class=\"p2\"><span class=\"s1\">At SpotMe, protecting your private information is our priority and we are committed to being upfront about it. SpotMe respects the privacy needs and concerns of our customers. We appreciate the trust you place in us when you choose to visit our websites, make use of our website/app and services and we take that responsibility seriously.</span></p>\n" +
                        "<p class=\"p2\"><span class=\"s1\">This Privacy Policy explains how we (&ldquo;SpotMe&rdquo;, &ldquo;we&rdquo; or &ldquo;us&rdquo;) collects, uses, and discloses information about you when you access or use our websites, mobile application, and other online products and services (collectively, the &ldquo;Services&rdquo;), and when you contact our customer service team, engage with us on social media, or otherwise interact with us. </span></p>\n" +
                        "<p class=\"p2\"><span class=\"s1\">We may change this Privacy Policy from time to time. If we make changes, we will notify you by revising the date at the top of the policy and, in some cases, we may provide you with additional notice (such as adding a statement to our website homepage or sending you a notification). We encourage you to review the Privacy Policy whenever you access the Services or otherwise interact with us to stay informed about our information practices and the choices available to you.</span></p>\n" +
                        "<p class=\"p3\">&nbsp;</p>\n" +
                        "<p class=\"p2\"><span class=\"s1\"><strong>Privacy Policy for Children</strong></span></p>\n" +
                        "<p class=\"p2\"><span class=\"s1\">We do not intentionally gather Personal Data from users who are under the age of 18 or that are minors in the jurisdiction from which the SpotMe website is accessed. If a user under the age of 18 submits Personal Data to us and we learn that the Personal Data is the information of a user under the age of 18, we will attempt to delete the information as soon as possible. If a parent or guardian becomes aware that his or her child has provided us with information without their consent, he or she should contact us. We will delete such information from our files as soon as reasonably possible. </span></p>\n" +
                        "<p class=\"p2\"><span class=\"s1\"><strong>Collection of Information</strong></span></p>\n" +
                        "<p class=\"p2\"><span class=\"s2\">Information You Provide to Us</span></p>\n" +
                        "<p class=\"p2\"><span class=\"s1\">We may collect certain personal and financial information from you while you use the website, and most of this collection occurs during registration. Some information is required and some are optional, some will remain private and some will be displayed to other users. We always let you know which is which. </span></p>\n" +
                        "<p class=\"p2\"><span class=\"s1\">Additional information may be gathered during your subsequent use of the website, whenever you choose to provide it. We further collect information when you register to use our site, apply for a loan, search for a feature (for example but not limited to, loan amounts, currency, loan duration), your activity levels on boards or other social media functions on the applicable social media sites, the applications you use on social media sites, and when you report a problem with our site. The information you give us may include your SSN, name, address, e-mail address and phone number, login information for social networking sites, financial and credit card information, personal description, current and former places of employment, photographs, and lists of family members.</span></p>\n" +
                        "<p class=\"p2\"><span class=\"s1\">Certain personal information as mentioned above must be supplied during the investor and/or borrower registration and application processes in order to meet our legal obligations, verify your identity, facilitate determinations for credit eligibility, protect against fraud, and complete your transaction. </span></p>\n" +
                        "<p class=\"p2\"><span class=\"s2\">Other Information About Your Use of the Services</span></p>\n" +
                        "<p class=\"p2\"><span class=\"s1\">When you use our Services, we collect the following information about you:</span></p>\n" +
                        "<ol class=\"ol1\">\n" +
                        "<li class=\"li2\"><span class=\"s1\">Usage Information: Whenever you use our Services, we collect usage information, such as the sessions you use, what screens or features you access, and other similar types of usage information.</span></li>\n" +
                        "<li class=\"li2\"><span class=\"s1\">Transactional Information and Information for other sources: When you check your rate or apply for a loan through SpotMe, we may collect certain information from credit bureaus and relevant third-parties to facilitate determinations for credit eligibility. If you obtain a personal loan through SpotMe, we also collect information throughout the life of your personal loan through SpotMe from credit bureaus and other third parties in order to assess risks associated with your personal loan, provide information to investors interested in buying or selling your personal loan, and to provide you with marketing offers. If another company refers you to SpotMe or assists you in any way in the application process, we may also collect your information from that company in order to process your application.</span></li>\n" +
                        "</ol>\n" +
                        "<p class=\"p4\">&nbsp;</p>\n" +
                        "<p class=\"p5\"><span class=\"s1\">The following categories of information are generally collected for loan applicants and borrowers:</span></p>\n" +
                        "<p class=\"p4\">&nbsp;</p>\n" +
                        "<ol class=\"ol2\">\n" +
                        "<li class=\"li2\"><span class=\"s1\">Your identifying and application information (for example, your name, address, email, telephone number, date of birth, social security number, and employment information);</span></li>\n" +
                        "<li class=\"li2\"><span class=\"s1\">Your credit score, credit history and other information related to your creditworthiness, identity and financial transactions;</span></li>\n" +
                        "<li class=\"li2\"><span class=\"s1\">Your bank account information and recent transactions;</span></li>\n" +
                        "<li class=\"li2\"><span class=\"s1\">If you applied for a loan to be used with a merchant or service provider, information related to any dispute you submit regarding the merchant or service provider;</span></li>\n" +
                        "<li class=\"li2\"><span class=\"s1\">If you applied for a loan that is processed through an online finance platform other than SpotMe, the status and terms of your loan or loan offer through that platform;</span></li>\n" +
                        "<li class=\"li2\"><span class=\"s1\">If you applied for a home equity line of credit (HELOC) through SpotMe, information related to your demographics (as required by law) and property, and any other information to evaluate and complete your application.</span></li>\n" +
                        "</ol>\n" +
                        "<p class=\"p6\">&nbsp;</p>\n" +
                        "<p class=\"p5\"><span class=\"s1\">If you register as an investor through SpotMe, we will collect information from credit bureaus and other third parties to verify your identity and bank account information and assess risks related to your account. If another company refers you to SpotMe or assists you in any way in the registration or investing process, we may also collect your information from that company in order to process your registration or request or to update your account information</span></p>\n" +
                        "<ol class=\"ol1\">\n" +
                        "<li class=\"li2\"><span class=\"s1\">Log Information: We collect standard log files when you use our Services, which include the type of web browser you use, app version, access times and dates, pages viewed, your IP address, and the page you visited before navigating to our websites.</span></li>\n" +
                        "<li class=\"li2\"><span class=\"s1\">Device Information: We collect information about the computer or mobile device you use to access our Services, including the hardware model, operating system and version, device identifiers set by your device operating system, and mobile network information (like your connection type, carrier and region).</span></li>\n" +
                        "<li class=\"li2\"><span class=\"s1\">Information we Generate: We generate some information about you based on other information we have collected. For example, like most platforms, we use your IP address to derive the approximate location of your device. We also use your first name to make an educated guess about your gender and use information about your activity to help determine the likelihood of you continuing to use our Services in the future (which we hope will be the case!).</span></li>\n" +
                        "<li class=\"li2\"><span class=\"s1\">Information Collected by Cookies and Similar Tracking Technologies: We use different technologies to collect information, including cookies and web beacons. Please see our Cookie Policy for more details: https://www.SpotMe.com/cookies</span></li>\n" +
                        "</ol>\n" +
                        "<p class=\"p2\"><span class=\"s1\"><strong>How we make use of your information</strong></span></p>\n" +
                        "<p class=\"p2\"><span class=\"s1\">When you register as a borrower, we will use this information to pull a credit report from a credit bureau to determine your creditworthiness as well as to help investors assess your loan request in the context of your overall financial situation. We will also use your required and optional information to facilitate activities and transactions that need to occur during online person-to-person lending, such as:</span></p>\n" +
                        "<ol class=\"ol1\">\n" +
                        "<li class=\"li2\"><span class=\"s1\">Effecting, processing, delivering and performing a contract you have entered into with or through us;</span></li>\n" +
                        "<li class=\"li2\"><span class=\"s1\">processing applications for products and services available through us, including making decisions about whether to agree or approve your application;</span></li>\n" +
                        "<li class=\"li2\"><span class=\"s1\">enabling our financial services partners to implement fund transfers and the receipt of borrower payments;</span></li>\n" +
                        "<li class=\"li2\"><span class=\"s1\">Provide, maintain and improve our Services, and develop new products and service;</span></li>\n" +
                        "<li class=\"li2\"><span class=\"s1\">helping us better understand your financial circumstances and behavior so that we may make decisions about how we manage your Lending Works Account;</span></li>\n" +
                        "<li class=\"li2\"><span class=\"s1\">Send you transactional or relationship messages, such as receipts, account notifications, customer service responses, and other administrative messages;</span></li>\n" +
                        "<li class=\"li2\"><span class=\"s1\">giving you important information about updated and new features and benefits associated with your existing products and services with us and benefits and to notify you about changes to our products and services;</span></li>\n" +
                        "<li class=\"li2\"><span class=\"s1\">Monitor and analyze trends, usage, and activities in connection with our Services;</span></li>\n" +
                        "<li class=\"li2\"><span class=\"s1\">Detect, investigate and prevent fraudulent transactions and other illegal activities and protect the rights and property of SpotMe and others. If you feel your transaction was declined in error, please contact us for assistance at </span><span class=\"s3\">support@Spotmeapp.net</span><span class=\"s1\">;</span></li>\n" +
                        "<li class=\"li2\"><span class=\"s1\">Comply with the law, such as by processing transactional records for tax filings;</span></li>\n" +
                        "<li class=\"li2\"><span class=\"s1\">Personalize your online experience and the advertisements you see on other platforms based on your preferences, interests, and browsing behavior; and</span></li>\n" +
                        "<li class=\"li2\"><span class=\"s1\">Facilitate contests, sweepstakes, and promotions and process and deliver entries and rewards.</span></li>\n" +
                        "</ol>\n" +
                        "<p class=\"p2\"><span class=\"s1\"><strong>How we share your information</strong></span></p>\n" +
                        "<p class=\"p2\"><span class=\"s1\">We do not sell, rent, or otherwise trade personal information collected about you through the Site, and that collected through the Services to others. The information that you provide to us is an important part of our business and we share your personal information only as described below:</span></p>\n" +
                        "<ol class=\"ol1\">\n" +
                        "<li class=\"li2\"><span class=\"s1\">With companies and organizations that perform services for us, including originating banks, credit bureaus, and related financial service providers, payment processors, fraud prevention vendors and other service providers;</span></li>\n" +
                        "<li class=\"li2\"><span class=\"s1\">In response to a request for information if we believe disclosure is in accordance with or required by, any applicable law or legal process, including lawful requests by public authorities to meet national security or law enforcement requirements;</span></li>\n" +
                        "<li class=\"li2\"><span class=\"s1\">If we believe your actions are inconsistent with our user agreements or policies, if we believe you have violated the law, or to protect the rights, property, and safety of SpotMe or others;</span></li>\n" +
                        "<li class=\"li2\"><span class=\"s1\">In connection with, or during negotiations of, any merger, sale of company assets, financing or acquisition of all or a portion of our business by another company;</span></li>\n" +
                        "<li class=\"li2\"><span class=\"s1\">Between and among SpotMe and our current and future parents, affiliates, subsidiaries, and other companies under common control and ownership; and</span></li>\n" +
                        "<li class=\"li2\"><span class=\"s1\">With your consent or at your direction. For instance, you may choose to share actions you take on our Services with third-party social media services via the integrated tools we provide via our Services. We also share aggregated or other information not subject to obligations under the data protection laws of your jurisdiction with third parties. For example, we sometimes share aggregate information with research organizations to help facilitate their research.</span></li>\n" +
                        "</ol>\n" +
                        "<p class=\"p7\"><span class=\"s1\">Information shared with Credit Bureaus and FTC: We carry out credit and identity checks when you apply for a product or service, for you or your business.</span></p>\n" +
                        "<p class=\"p7\"><span class=\"s1\">If you use our products or services, from time to time we may also search for information that the Credit Bureaus have about you, to help us manage those accounts.</span></p>\n" +
                        "<p class=\"p7\"><span class=\"s1\">To do this, we will supply your personal information to the credit bureaus and the FTC and they will give us information about you in return. This will include information about your financial situation and financial history. The Credit Bureaus and FTC will give us publicly available information about you and privately held information which will include your credit history, your current financial situation, financial history information and fraud prevention information.</span></p>\n" +
                        "<p class=\"p7\"><span class=\"s1\">When we use your information and share it with the credit Bureaus the FTC, we do so as a necessary step to assess whether or not we can enter into a contract with you. Where your loan application is approved and you choose to enter into a loan through SpotMe, it will continue to be necessary for us to carry out these checks from time to time to perform the contract we have with you.</span></p>\n" +
                        "<p class=\"p7\"><span class=\"s1\">We will use the information that we receive from the Credit Bureaus and the FTC to:</span></p>\n" +
                        "<ol class=\"ol1\">\n" +
                        "<li class=\"li2\"><span class=\"s1\">Help us understand whether or not we think you can afford to take out the loan that you have requested through us</span></li>\n" +
                        "<li class=\"li2\"><span class=\"s1\">To assess if we believe you will be able to meet your loan obligations</span></li>\n" +
                        "<li class=\"li2\"><span class=\"s1\">Make sure that the offers we are able to provide to you are fair and appropriate to your circumstances and check the information that you have provided to us is accurate</span></li>\n" +
                        "<li class=\"li2\"><span class=\"s1\">Meet our legal obligations to help prevent criminal activity, fraud and money laundering</span></li>\n" +
                        "<li class=\"li2\"><span class=\"s1\">Manage your account(s)</span></li>\n" +
                        "<li class=\"li2\"><span class=\"s1\">Trace and recover debts where we have been unable to make contact with you via any other means that we hold your information (i.e. phone, email, letter).</span></li>\n" +
                        "</ol>\n" +
                        "<p class=\"p3\">&nbsp;</p>\n" +
                        "<p class=\"p2\"><span class=\"s1\">If you are making a joint application, or tell us that you have a spouse or financial associate, we will link your records together, so you should make sure you have made them aware how their personal information will be used by us before you complete the application. In addition, you should both also be aware that the Credit Bureaus will also link your records together and these links will remain on both of your files until an approved request is received by the credit bureaus confirming that the financial relationship no longer exists.</span></p>\n" +
                        "<p class=\"p2\"><span class=\"s1\">We will continue to exchange information about you with credit bureaus and the FTC while you have a relationship with us. We will also inform the credit bureaus once you have finished repaying a loan with us. If you borrow money through the SpotMe platform and do not make all your repayments in full and on time this information will be shared with credit bureaus who will record that information as part of your credit history. It is possible that the credit bureaus will then share this information with other organizations who have a relationship with them.</span></p>\n" +
                        "<p class=\"p2\"><span class=\"s1\">If you give us false or inaccurate information or if we suspect or identify fraud we will record this and will also pass this information to the FTC and other organizations involved in crime and fraud prevention, including law enforcement agencies.</span></p>\n" +
                        "<p class=\"p2\"><span class=\"s1\">We and other organizations may access and use this information in order to prevent fraud, money laundering or other criminal activity. If we, or the FTC, determine that you pose a fraud or money laundering risk, we may refuse to provide our products and services you have requested, or we may stop providing existing services to you.</span></p>\n" +
                        "<p class=\"p3\">&nbsp;</p>\n" +
                        "<p class=\"p2\"><span class=\"s1\">Fraud prevention agencies can hold your personal data for different periods of time, and if you are considered to pose a fraud or money laundering risk, your data can be held for up to six years and may result in other companies or organisations refusing to provide services, financing or employment to you.</span></p>\n" +
                        "<p class=\"p2\"><span class=\"s1\"><strong>Advertising and Analytics Services Provided by Others</strong></span></p>\n" +
                        "<p class=\"p2\"><span class=\"s1\">We allow others to provide analytics services and serve advertisements on our behalf across the web and in mobile applications. These entities use cookies, web beacons, device identifiers and other technologies to collect information about your use of the Services and other websites and online services, including your IP address, device identifiers, web browser, mobile network information, pages viewed, time spent on pages or in apps, links clicked, and conversion information. This information may be used by SpotMe and others to, among other things, analyze and track data, determine the popularity of certain content, deliver advertising and content targeted to your interests on our Services and other websites and online services, and better understand your online activity. For more information about interest-based ads, or to opt-out of having your web browsing information used for behavioral advertising purposes, please visit <a href=\"http://www.aboutads.info/choices\"><span class=\"s4\">www.aboutads.info/choices</span></a>.</span></p>\n" +
                        "<p class=\"p2\"><span class=\"s1\"><strong>Third-Party Services and Sites</strong></span></p>\n" +
                        "<p class=\"p2\"><span class=\"s1\">Our Services, the website may contain links to or the ability for you to access third-party websites, products, and services that are not owned, managed or controlled by SpotMe, and who may have privacy policies with different standards than those contained in this Privacy Policy. This Privacy Policy applies solely to information collected by us. We encourage you to read the privacy policies of any third parties before using their websites, products, or services.</span></p>\n" +
                        "<p class=\"p2\"><span class=\"s1\"><strong>Keeping your data safe</strong></span></p>\n" +
                        "<p class=\"p2\"><span class=\"s1\">As a matter of policy, we are committed to protecting our users&rsquo; personal data. We implement appropriate technical, administrative and organizational measures to help protect the security of your personal data; however, please note that no method of transmission over the Internet, or method of electronic storage, is 100% secure. Although we will do our best to protect the personal data that we gather about you, the transmission of information via the internet is not completely secure and so, we cannot guarantee its absolute security of your data transmitted to the Service; any transmission is at your own risk. We have implemented various policies including encryption, access, and retention policies to guard against unauthorized access and unnecessary retention of personal data in our systems.</span></p>\n" +
                        "<p class=\"p2\"><span class=\"s1\">Your password protects your user account, so we encourage you to use a strong password that is unique to your SpotMe account, never share your password with anyone, limit access to your website and browser.</span></p>\n" +
                        "<p class=\"p2\"><span class=\"s1\"><strong>Data retention and deletion</strong></span></p>\n" +
                        "<p class=\"p2\"><span class=\"s1\">We keep your personal data only as long as necessary to provide you with the SpotMe Service and for legitimate and essential business purposes, such as maintaining the performance of the SpotMe Service, making data-driven business decisions about new features and offerings, complying with our legal obligations, and resolving disputes. We keep some of your personal data for as long as you are a user of the SpotMe Service.</span></p>\n" +
                        "<p class=\"p2\"><span class=\"s1\">If you request, we will delete or anonymize your personal data so that it no longer identifies you, unless we are legally allowed or required to maintain certain personal data, including situations such as the following:</span></p>\n" +
                        "<ol class=\"ol1\">\n" +
                        "<li class=\"li2\"><span class=\"s1\">If there is an unresolved issue relating to your account, such as an outstanding credit on your account or an unresolved claim or dispute we will retain the necessary personal data until the issue is resolved;</span></li>\n" +
                        "<li class=\"li2\"><span class=\"s1\">Where we need to retain the personal data for our legal, tax, audit, and accounting obligations, we will retain the necessary personal data for the period required by applicable law; and/or,</span></li>\n" +
                        "<li class=\"li2\"><span class=\"s1\">Where necessary for our legitimate business interests such as fraud prevention or to maintain the security of our users.</span></li>\n" +
                        "</ol>\n" +
                        "<p class=\"p2\"><span class=\"s1\"><strong>Mobile privacy</strong></span></p>\n" +
                        "<p class=\"p2\"><span class=\"s1\">We may offer you the ability to connect with or use our applications, services, and tools through a mobile device, either through a mobile application or via a mobile-optimized website. The provisions of this Privacy Policy apply to all such mobile access and use of mobile devices. This Privacy Policy will be referenced by all such as mobile applications or mobile optimized websites.</span></p>\n" +
                        "<p class=\"p2\"><span class=\"s1\">When you download or use our mobile applications or access one of our mobile-optimized sites, we may use various technologies to determine location, such as location services of the applicable operating system or browser and sensor data from your device. We automatically receive and record information on our server from your browser or mobile platform, including your location, IP address, browser type, operating information, device and application IDs, and cookie information. We will not use any information collected for advertising purposes and we will not provide this information to any third parties for use in the marketing of any product or service to you.</span></p>\n" +
                        "<p class=\"p2\"><span class=\"s1\"><strong>Purposes for which we seek your consent</strong></span></p>\n" +
                        "<p class=\"p2\"><span class=\"s1\">We may also ask for your consent to process your personal information for a specific purpose that we communicate to you. When you consent to our processing your personal information for a specified purpose, you may withdraw your consent at any time and we will stop the processing of your data for that purpose.</span></p>\n" +
                        "<p class=\"p2\"><span class=\"s1\">Also, we employ other companies and individuals to perform functions on our behalf. They have access to personal information needed only to perform their functions. We require companies with whom we contract for outsourced services to keep data received from us confidential and to use it only for the purposes indicated.</span></p>\n" +
                        "<p class=\"p2\"><span class=\"s1\"><strong>What rights and choices do you have?</strong></span></p>\n" +
                        "<p class=\"p2\"><span class=\"s1\">In compliance with certain restrictions under national law, as a data subject, you have the right to have access to, make updates, erase and restrict processing of your personal data. If you have any questions or objection as to how we collect and process your personal information, please contact our Customer Service. The SpotMe Services also include settings that provide you with options as to how your information is being used.</span></p>\n" +
                        "<p class=\"p2\"><span class=\"s1\">You can get a copy of the personal information collected by us if any. You can choose not to provide certain information, but then you might not be able to take advantage of many features of SpotMe Services.</span></p>\n" +
                        "<p class=\"p2\"><span class=\"s1\">You can add or update certain information by visiting your SpotMe Account When you update information, we may keep a copy of the previous version for our records. </span></p>\n" +
                        "<p class=\"p2\"><span class=\"s1\">If you don't want to receive e-mail or other communications from us, please see the Managing Your Preference section.</span></p>\n" +
                        "<p class=\"p2\"><span class=\"s1\"><strong>Changes to this privacy policy</strong></span></p>\n" +
                        "<p class=\"p2\"><span class=\"s1\">We are continually improving and adding to the features and functionality of this website and the Services we offer to you. As a result of these changes (or changes to the law), we may need to revise or update this Privacy Policy. Accordingly, we reserve the right to update or modify this Policy at any time, and we will display at least one (1) week prior notice of the change on the website. Your continued use of this website after we have posted the revised Privacy Policy constitutes your agreement to be bound by the revised Privacy Policy. However, we will honor the terms that were in effect when we gathered Personal Information from you.</span></p>");
                startActivity(a);

            }
        });
        terms_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent a=new Intent(SignupActivity.this,WebviewActivity.class);
                a.putExtra("type","Terms & Conditions");
                a.putExtra("url","<p class=\"p1\"><span class=\"s1\"><strong>AMERICAN TECHNOLOGIES MARKETING GROUP INC</strong></span></p>\n" +
                        "<p class=\"p1\"><span class=\"s1\"><strong>SPOTME TERMS OF USE</strong></span></p>\n" +
                        "<p class=\"p2\"><span class=\"s1\">This Agreement is a contract between you and American Technologies Marketing Group Inc. and applies to your use of the SpotMe Services (the \"Services\"). By registering for the Services, you must read, agree with and accept all of the terms and conditions contained in this Agreement. You agree that any use by you of the Services shall constitute your acceptance of the Agreement. </span></p>\n" +
                        "<p class=\"p2\"><span class=\"s1\">These Terms applies to your access or usage of the <a href=\"http://www.spotmeapp.net\"><span class=\"s2\">www.spotmeapp.net</span></a> website and the mobile application (hereinafter referred to as &ldquo;Website&rdquo;, or &ldquo;App&rdquo;, or &ldquo;Mobile App&rdquo; or &ldquo;Platform&rdquo;). Please read this Agreement carefully and make sure that you understand it fully before using the Services.</span></p>\n" +
                        "<p class=\"p2\"><span class=\"s1\">In addition to these Terms herein, you may enter into other agreements with us or others that will govern your use of the Service or related services offered by us or others. In the event that there is any contradiction between these Terms of Use and any other agreement you enter into applicable to specific aspects of the Service, the other agreement shall take precedence in relation to the specific aspects of the Service to which it applies.</span></p>\n" +
                        "<p class=\"p2\"><span class=\"s1\"><strong>PRIVACY</strong></span></p>\n" +
                        "<p class=\"p2\"><span class=\"s1\">Please refer to our Privacy Policy for information on how we collect, use and disclose information from our users. You acknowledge and agree that your use of the Services is subject to our Privacy Policy.</span></p>\n" +
                        "<p class=\"p2\"><span class=\"s1\"><strong>ARBITRATION NOTICE</strong></span></p>\n" +
                        "<p class=\"p2\"><span class=\"s1\">Unless you opt-out of arbitration within 30 days of the date you first agree to these terms by following the opt-out procedure specified in the &ldquo;Arbitration&rdquo; section below, and except for certain types of disputes described in the &ldquo;Arbitration&ldquo; section below, you agree that disputes between you and SpotMe will be resolved by binding, individual arbitration and you are waiving your right to a trial by jury or to participate as a plaintiff or class member in any purported class action or representative proceeding.</span></p>\n" +
                        "<p class=\"p2\"><span class=\"s1\"><strong>CHANGES TO THESE TERMS</strong></span></p>\n" +
                        "<p class=\"p2\"><span class=\"s1\">We may modify the Terms at any time, in our sole discretion. If we do so, we&rsquo;ll let you know either by posting the modified Terms on the platform or through other communications. It&rsquo;s important that you review the Terms whenever we modify them because if you continue to use the Services after we have posted modified Terms on the platform, you are indicating to us that you agree to be bound by the modified Terms. </span></p>\n" +
                        "<p class=\"p2\"><span class=\"s1\">If you don&rsquo;t agree to be bound by the modified Terms, then you may not use the Services anymore. Because our Services are evolving over time we may change or discontinue all or any part of the Services, at any time and without notice, at our sole discretion.</span></p>\n" +
                        "<p class=\"p2\"><span class=\"s1\"><strong>ELIGIBILITY</strong></span></p>\n" +
                        "<p class=\"p2\"><span class=\"s1\">Use of the Website is available only to persons who are U.S. residents with a valid social security number and can form legally binding contracts. If you are a minor i.e. under the age of 18 years, you shall not register as a User of SpotMe website and shall not transact on or use the platform. SpotMe reserves the right to terminate your membership and/or refuse to provide you with access to the platform if it is brought to SpotMe's notice or if it is discovered that you are under the age of 18 years.</span></p>\n" +
                        "<p class=\"p2\"><span class=\"s1\"><strong>REGISTRATION AND ACCOUNT SECURITY</strong></span></p>\n" +
                        "<p class=\"p2\"><span class=\"s1\">To use our Services and access certain portions of the Website, you will need to register and obtain an account, username and password. When you register, the information you provide to us during the registration process will help us in offering content, service, and management of your account. You are solely responsible for maintaining the confidentiality of your account, username, and password and for all activities associated with or occurring under your Account. </span></p>\n" +
                        "<p class=\"p2\"><span class=\"s1\">You represent and warrant that your Account information will be accurate at all times. You agree that if You provide any information that is untrue, inaccurate, not current or incomplete or We have reasonable grounds to suspect that such information is untrue, inaccurate, not current or incomplete, or not in accordance with these Terms, we shall have the right to indefinitely suspend or terminate or block access of your use the Website and refuse to provide You with access to the Website. </span></p>\n" +
                        "<p class=\"p2\"><span class=\"s1\">You must notify us (a) immediately of any unauthorized use of your account and any other breach of security, and (b) ensure that you exit from your account at the end of each use. </span></p>\n" +
                        "<p class=\"p2\"><span class=\"s1\">We cannot and will not be responsible for any loss or damage arising from your failure to comply with the foregoing requirements or as a result of the use of your account, either with or without your knowledge, prior to your notifying us of unauthorized access to your account. You may not transfer your account to any other person and you may not use anyone else's account at any time without the permission of the account holder.</span></p>\n" +
                        "<p class=\"p2\"><span class=\"s1\"><strong>INTELLECTUAL PROPERTY RIGHTS</strong></span></p>\n" +
                        "<p class=\"p2\"><span class=\"s1\">This platform is controlled and operated by us and all material on this site, including images, illustrations, audio clips, and video clips, are protected by copyrights, trademarks, and other intellectual property rights.</span></p>\n" +
                        "<p class=\"p2\"><span class=\"s1\">Material on Website and App is solely for your personal, non-commercial use. You must not copy, reproduce, republish, upload, post, transmit or distribute such material in any way, including by email or other electronic means and whether directly or indirectly and you must not assist any other person to do so. Any use of the Site or the Site Content other than as specifically authorized herein, without our prior written permission, is strictly prohibited and will terminate the license granted herein. Such unauthorized use may also violate applicable laws including without limitation copyright and trademark laws and applicable communications regulations and statutes. Unless explicitly stated herein, nothing in these Terms shall be construed as conferring any license to intellectual property rights, whether by estoppel, implication or otherwise. This license is revocable by us at any time without notice and with or without cause.</span></p>\n" +
                        "<p class=\"p2\"><span class=\"s1\">Without the prior written consent of the owner, modification of the materials, use of the materials on any other website or networked computer environment or use of the materials for any purpose other than personal, non-commercial use is a violation of the copyrights, trademarks and other proprietary rights, and is prohibited. Any use for which you receive any remuneration, whether in money or otherwise, is a commercial use for the purposes of this clause.</span></p>\n" +
                        "<p class=\"p2\"><span class=\"s1\"><strong>USER REPRESENTATION AND PLATFORM USE</strong></span></p>\n" +
                        "<p class=\"p2\"><span class=\"s1\">You hereby represent, warrant and agree that no materials of any kind submitted through your account or otherwise posted or shared by you through the service will be in violation of the rights of any third party, including but not limited to the copyright, trademark., publicity, privacy or other personal or proprietary rights.</span></p>\n" +
                        "<p class=\"p2\"><span class=\"s1\">Although we are committed to providing a safe user experience, we do not guarantee that the platform, or any content in it, will be safe, error-free or uninterrupted, or that it will be free from bugs or viruses. From time to time, access to the service may be interrupted, suspended or restricted, including because if a fault, error or unforeseen circumstances or due to scheduled maintenance. We shall not be liable to you for any loss or damage that you may suffer as a result of the service being unavailable at any time for any reason.</span></p>\n" +
                        "<p class=\"p2\"><span class=\"s1\">You agree, undertake and confirm that your use of the platform shall be strictly in accordance with the following binding guidelines:</span></p>\n" +
                        "<ol class=\"ol1\">\n" +
                        "<li class=\"li2\"><span class=\"s1\">You shall not host, display, upload, modify, publish, transmit, update or share any information which:</span></li>\n" +
                        "</ol>\n" +
                        "<ol class=\"ol2\">\n" +
                        "<li class=\"li2\"><span class=\"s1\">belongs to another person and to which you do not have any right to make use of or promotes an illegal or unauthorized copy of another person's copyrighted work such as providing pirated computer programs or links to them, providing information to circumvent manufacture-installed copy-protect devices;</span></li>\n" +
                        "<li class=\"li2\"><span class=\"s1\">is grossly harmful, harassing, blasphemous, defamatory, obscene, pornographic, paedophilic, libellous, invasive of another's privacy, hateful, or racially, ethnically objectionable, disparaging, relating or encouraging money laundering or gambling, or otherwise unlawful in any manner whatever; or,</span></li>\n" +
                        "<li class=\"li2\"><span class=\"s1\">is patently offensive to the online community, such as sexually explicit content, or content that promotes obscenity, paedophilia, racism, bigotry, hatred or physical harm of any kind against any group or individual or provides material that exploits people in a sexual, violent or otherwise inappropriate manner or solicits personal information from anyone;</span></li>\n" +
                        "<li class=\"li2\"><span class=\"s1\">Involves the transmission of \"junk mail\", \"chain letters\", or unsolicited mass mailing or \"spamming\" or contains any trojan horses, worms, time bombs, cancel bots, easter eggs or other computer programming routines that may damage, detrimentally interfere with, diminish value of, surreptitiously intercept or expropriate any system, data or personal information;</span></li>\n" +
                        "<li class=\"li2\"><span class=\"s1\">promotes illegal activities or conduct that is abusive, threatening, obscene, defamatory or libelous;</span></li>\n" +
                        "<li class=\"li2\"><span class=\"s1\">Provides instructional information about illegal activities such as making or buying illegal weapons, violating someone's privacy, or providing or creating computer viruses; contains video, photographs, or images of another person (with a minor or an adult);</span></li>\n" +
                        "<li class=\"li2\"><span class=\"s1\">tries to gain unauthorized access or exceeds the scope of authorized access to the Website or to profiles, blogs, communities, account information, bulletins, or other areas of the Website or solicits passwords or personal identifying information for commercial or unlawful purposes from other users.</span></li>\n" +
                        "<li class=\"li2\"><span class=\"s1\">Solicits gambling or engages in any gambling activity which we, in our sole discretion, believes is or could be construed as being illegal;</span></li>\n" +
                        "<li class=\"li2\"><span class=\"s1\">Interferes with another user's use and enjoyment of the website or any other individual's user and enjoyment of similar services;</span></li>\n" +
                        "<li class=\"li2\"><span class=\"s1\">Refers to any website or URL that, in our sole discretion, contains material that is inappropriate for the Website or any other website, contains content that would be prohibited or violates the letter or spirit of these Terms of Use.</span></li>\n" +
                        "</ol>\n" +
                        "<ol class=\"ol1\">\n" +
                        "<li class=\"li2\"><span class=\"s1\">You shall not use any \"deep-link\", \"page-scrape\", \"robot\", \"spider\" or other automatic device, program, algorithm or methodology, or any similar or equivalent manual process, to access, acquire, copy or monitor any portion of the Website or any Content, or in any way reproduce or circumvent the navigational structure or presentation of the Website or any Content, to obtain or attempt to obtain any materials, documents or information through any means not purposely made available through the Website. We reserve Our right to bar any such activity.</span></li>\n" +
                        "<li class=\"li2\"><span class=\"s1\">You shall not attempt to gain unauthorized access to any portion or feature of the Website, or any other systems or networks connected to the Website or to any server, computer, network, or to any of the services offered on or through the Website, by hacking, password \"mining\" or any other illegitimate means.</span></li>\n" +
                        "<li class=\"li2\"><span class=\"s1\">You shall not probe, scan or test the vulnerability of the Website or any network connected to the Website nor breach the security or authentication measures on the Website or any network connected to the Website. You may not reverse look-up, trace or seek to trace any information on any other User of or visitor to Website, or any other customer, including any account on the Website not owned by You, to its source, or exploit the Website or any service or information made available or offered by or through the Website, in any way where the purpose is to reveal any information, including but not limited to personal identification or information, other than Your own information, as provided for by the Website.</span></li>\n" +
                        "<li class=\"li2\"><span class=\"s1\">You agree not to use any device, software or routine to interfere or attempt to interfere with the proper working of the Website or any transaction being conducted on the Website, or with any other person's use of the Website.</span></li>\n" +
                        "<li class=\"li2\"><span class=\"s1\">Your correspondence or business dealings with, or participation in promotions of, advertisers found on or through the Website, including payment and delivery of related products or services, and any other terms, conditions, warranties or representations associated with such dealings, are solely between you and such advertiser. We shall not be responsible or liable for any loss or damage of any sort incurred as the result of any such dealings or as the result of the presence of such advertisers on the Website.</span></li>\n" +
                        "<li class=\"li2\"><span class=\"s1\">It is possible those other users (including unauthorized users or \"hackers\") may post or transmit offensive or obscene materials on the Website and that You may be involuntarily exposed to such offensive and obscene materials. It also is possible for others to obtain personal information about You due to your use of the Website, and that the recipient may use such information to harass or injure You. We do not approve of such unauthorized uses, but by using the Website You acknowledge and agree that We are not responsible for the use of any personal information that You publicly disclose or share with others on the Website. Please carefully select the type of information that You publicly disclose or share with others on the Website.</span></li>\n" +
                        "</ol>\n" +
                        "<p class=\"p2\"><span class=\"s1\"><strong>HOW WE COMMUNICATE: CONSENT TO ELECTRONIC TRANSACTION, COMMUNICATION AND DISCLOSURES</strong></span></p>\n" +
                        "<p class=\"p2\"><span class=\"s1\">To the fullest extent permitted by law, these Terms and Conditions and any other agreements, notices or other communications from SpotMe to you regarding our services being offered (\"Communications\") may be provided to you electronically, and you consent and agree to receive Communications in an electronic form. Electronic Communications may be posted on the pages within the SpotMe website and/or delivered to your email address. You can print a paper copy of or download any electronic Communication and retain it for your records. </span></p>\n" +
                        "<p class=\"p2\"><span class=\"s1\">Also, you expressly consent to be contacted by us, our agents, representatives, affiliates, or anyone calling on our behalf for any and all purposes, at any telephone number, or physical or electronic address you provide or at which you may be reached. You agree we may contact you in any way, including SMS messages (including text messages), calls using prerecorded messages or artificial voice, and calls and messages delivered using auto telephone dialing system or an automatic texting system. Automated messages may be played when the telephone is answered, whether by you or someone else. In the event that an agent or representative calls, he or she may also leave a message on your answering machine, voice mail, or send one via text.</span></p>\n" +
                        "<p class=\"p2\"><span class=\"s1\">You consent to receive SMS messages (including text messages), calls and messages (including prerecorded and artificial voice and auto-dialed) from us, our agents, representatives, affiliates or anyone calling on our behalf at the specific number(s) you have provided to us, or numbers we can reasonably associate with your account with information or questions about your application, loan and/or account. You certify, warrant and represent that the telephone numbers that you have provided to us are your numbers and not someone else's. You represent that you are permitted to receive calls at each of the telephone numbers you have provided to us. You agree to alert us whenever you stop using a particular telephone number.</span></p>\n" +
                        "<p class=\"p2\"><span class=\"s1\">All Communications in electronic format will be considered to be &ldquo;in writing,&rdquo; and to have been received no later than five (5) Business Days after posting or dissemination, whether or not you have received or retrieved the Communication. SpotMe reserves the right to provide Communications in paper format. Your consent to receive Communications electronically is valid until you revoke your consent by notifying us. If you revoke your consent to receive Communications electronically, we may terminate your right to use the SpotMe website or SpotMe Services, and you accept sole liability for any consequence resulting from suspension or termination of the SpotMe Services, to the extent permitted by law.</span></p>\n" +
                        "<p class=\"p2\"><span class=\"s1\">By agreeing to these Terms, you agree to receive electronically all documents, communications, notices, contracts, and agreements (including any IRS Form 1099) arising from or relating to your use of the website and Service, including any loans you may request or receive, your registration as a borrower or investor on our platform, any loans you may fund, notes you have purchased, your use of this Service, and the servicing of your loan, if funded, as either a borrower or investor member of SpotMe (each, a \"Disclosure\"), from us, whether we are acting in the capacity as trustee or otherwise.</span></p>\n" +
                        "<p class=\"p2\"><span class=\"s3\">Opting-out and Withdrawal of Consent:</span><span class=\"s1\"> You may withdraw your consent to receive Communications electronically by contacting us in the manner described below. If you withdraw your consent, from that time forward; we may terminate your right to use the SpotMe website or SpotMe Services, and you accept sole liability for any consequence resulting from suspension or termination of the SpotMe Services, to the extent permitted by law. The withdrawal of your consent will not affect the legal validity and enforceability of any pending loans obtained through the SpotMe platform, or any electronic Communications provided or business transacted between us prior to the time you withdraw your consent. With respect to pending loans on which you are a borrower or investor, we will send you any further Communications by mail or other non-electronic means. </span></p>\n" +
                        "<p class=\"p2\"><span class=\"s1\"><strong>SPOTME AS A TRANSACTION AND COMMUNICATION PLATFORM</strong></span></p>\n" +
                        "<p class=\"p2\"><span class=\"s1\">The Website is a platform that Users utilize to meet and interact with one another or with the third party for their transactions including services of granting, lending and taking of loans.</span></p>\n" +
                        "<p class=\"p2\"><span class=\"s1\">SpotMe does not have any control or does not determine or advise or in any way involve itself in the offering or acceptance of such commercial/contractual terms between users.</span></p>\n" +
                        "<p class=\"p2\"><span class=\"s1\">SpotMe is not responsible for any non-performance or breach of any contract entered into between users of this service. SpotMe cannot and does not guarantee that the concerned user(s) will perform any transaction concluded on the Website.</span></p>\n" +
                        "<p class=\"p2\"><span class=\"s1\">SpotMe, through the Services, may allow user(s) to apply for the loan, subject to the fulfilment of the eligibility criteria laid down by SpotMe. You understand that SpotMe may collect, authenticate, track your location, verify and confirm the User Data, documents and details as may be required to sanction the Loan.</span></p>\n" +
                        "<p class=\"p2\"><span class=\"s1\">During the application process, you may be required to share and upload the User Data on the Website or the Mobile Application Form. User Data may include personal information including but not limited to your name, social security number, e-mail address, gender, date of birth, mobile number, passwords, photograph, mobile phone information including contact numbers, SMS and login-in credentials of Third Party Platforms, financial information such as bank documents, bank account no., data from Credit Information Companies, data required for Know Your Customer compliances, requirement and other relevant details (&ldquo;Personal Information&rdquo;). You agree that the Personal Information shall always be accurate, correct and complete. </span></p>\n" +
                        "<p class=\"p2\"><span class=\"s1\">As part of the Services, you authorize us to import your details and Personal Information dispersed over Third Party Platforms. You understand and acknowledge that we may periodically request for updates on such Personal Information and we may receive such updated information from Third Party Platforms.</span></p>\n" +
                        "<p class=\"p2\"><span class=\"s1\">All transactions undertaken on your behalf by SpotMe will be on the basis of your express instructions/consent and will be strictly on a non-discretionary basis. You also authorise SpotMe to get your credit information report from one or more Credit Information Companies as decided by the Company from time to time.</span></p>\n" +
                        "<p class=\"p2\"><span class=\"s1\">You understand and acknowledge that you shall be solely responsible for all the activities that occur under your User Account while availing the Services. You undertake that SpotMe shall not be responsible and liable for any claims, damages, disputes arising out of the use or misuse of the Services. By usage of the Services, you shall be solely responsible for maintaining the confidentiality of the User Account and for all other related activities under your User Account. SpotMe reserves the right to accept or reject your registration for the Services without obligation of explanation.</span></p>\n" +
                        "<p class=\"p2\"><span class=\"s1\">You understand and acknowledge that you are solely responsible for the capability of the electronic devices and the internet connection, you chose to run the Platform. The Platform&rsquo;s operation or the Services on your electronic device is subject to availability of hardware, software specifications, internet connection and other features and specifications, required from time to time.</span></p>\n" +
                        "<p class=\"p2\"><span class=\"s1\">SpotMe does not make any representation or warranty as to the item-specifics (such as legal title, creditworthiness, identity, etc) of any of its Users. You are advised to independently verify the bona fides of any particular User that You choose to deal with on the Website and use Your best judgment in that behalf.</span></p>\n" +
                        "<p class=\"p2\"><span class=\"s1\">At no time shall SpotMe hold any right, title or interest over the products nor shall SpotMe have any obligations or liabilities in respect of such contract entered into between users.</span></p>\n" +
                        "<p class=\"p2\"><span class=\"s1\">You release and indemnify SpotMe and/or any of its officers and representatives from any cost, damage, liability or other consequence of any of the actions of the Users of the Website and specifically waive any claims that you may have in this behalf under any applicable law.</span></p>\n" +
                        "<p class=\"p2\"><span class=\"s1\"><strong>INDEMNIFICATION AND DISCLAIMER OF WARRANTY</strong></span></p>\n" +
                        "<p class=\"p3\">&nbsp;</p>\n" +
                        "<p class=\"p2\"><span class=\"s1\">In addition to the indemnities set forth elsewhere in these Terms, you agree that you shall indemnify and hold harmless SpotMe, its owner, licensee, affiliates, subsidiaries, group companies (as applicable) and their respective officers, directors, agents, and employees, from any claim or demand, or actions including reasonable attorneys' fees, made by any third party or penalty imposed due to or arising out of your breach of these Terms, Privacy Policy and other Policies, or your violation of any law, rules or regulations or the rights (including infringement of intellectual property rights) of a third party.</span></p>\n" +
                        "<p class=\"p2\"><span class=\"s1\">You further agree that you will cooperate fully in the defense of any such claims in the event that we decide to get involved in such matter. We reserve the right, at our own expense, to assume the exclusive defense and control of any matter otherwise subject to indemnification by you, and you shall not, in any event, settle any such claim or matter without the written consent of SpotMe. You further agree to indemnify and hold harmless SpotMe from any claim arising from a third party's use of information or materials of any kind that you post to the Site.</span></p>\n" +
                        "<p class=\"p2\"><span class=\"s1\">The Website and all content and services provided on the Website are provided on an \"as is\" and \"as available\" basis. SpotMe expressly disclaims all warranties of any kind, whether express or implied, including, but not limited to, the implied warranties of merchantability, fitness for a particular purpose, title, non-infringement, and security and accuracy, as well as all warranties arising by usage of trade, course of dealing, or course of performance.</span></p>\n" +
                        "<p class=\"p2\"><span class=\"s1\">We do not guarantee the accuracy of any User Content or Third Party Content. Although we provide rules for User conduct and postings, we do not control and are not responsible for what Users post on the Site and are not responsible for any offensive, inappropriate, obscene, unlawful or otherwise objectionable content you may encounter on the Site or in connection with any User Content or Third Party Content. We will not be held responsible for the conduct, whether online or offline, of any User of the Site or Service. We cannot guarantee and does not promise any specific results from use of the Site and/or the Service to obtain a loan.</span></p>\n" +
                        "<p class=\"p2\"><span class=\"s1\">SpotMe makes no warranty, and expressly disclaims any obligation, that:</span></p>\n" +
                        "<ol class=\"ol2\">\n" +
                        "<li class=\"li2\"><span class=\"s1\">the content will be up-to-date, complete, comprehensive, accurate or applicable to your circumstances;</span></li>\n" +
                        "<li class=\"li2\"><span class=\"s1\">the Website will meet your requirements or will be available on an uninterrupted, timely, secure, or error-free basis;</span></li>\n" +
                        "<li class=\"li2\"><span class=\"s1\">the results that may be obtained from the use of the Website or any services offered through the website will be accurate or reliable; or</span></li>\n" +
                        "<li class=\"li2\"><span class=\"s1\">the quality of any products, services, information, or other material obtained by you through the website will meet your expectations.</span></li>\n" +
                        "</ol>\n" +
                        "<p class=\"p2\"><span class=\"s1\"><strong>LIMITATION OF LIABILITY</strong></span></p>\n" +
                        "<p class=\"p2\"><span class=\"s1\">IN NO EVENT SHALL SPOTME BE LIABLE FOR ANY SPECIAL, INCIDENTAL, INDIRECT OR CONSEQUENTIAL DAMAGES OF ANY KIND IN CONNECTION WITH THESE TERMS OF USE, EVEN IF USER HAS BEEN INFORMED IN ADVANCE OF THE POSSIBILITY OF SUCH DAMAGES.</span></p>\n" +
                        "<p class=\"p2\"><span class=\"s1\">SpotMe (including its officers, directors, employees, representatives, affiliates, and providers) will not be responsible or liable for</span></p>\n" +
                        "<ol class=\"ol2\">\n" +
                        "<li class=\"li2\"><span class=\"s1\">any injury, death, loss, claim, an act of God, accident, delay, or any direct, special, exemplary, punitive, indirect, incidental or consequential damages of any kind (including without limitation lost profits or lost savings), whether based in contract, tort, strict liability or otherwise, that arises out of or is in any way connected with (a) any failure or delay (including without limitation the use of or inability to use any component of the Website), or (b) any use of the Website or content, or (c) the performance or non-performance by us or any provider, even if we have been advised of the possibility of damages to such parties or any other party, or</span></li>\n" +
                        "<li class=\"li2\"><span class=\"s1\">any damages to or viruses that may infect your computer equipment or other property as the result of your access to the Website or your downloading of any content from the Website.</span></li>\n" +
                        "</ol>\n" +
                        "<p class=\"p2\"><span class=\"s1\">SpotMe shall not liable for any defamatory, offensive or illegal conduct of any user. Your sole remedy for dissatisfaction with this site is to stop using the Site. If your use of materials from the Site results in the need for servicing, repair or correction of equipment or data, you assume any costs thereof. If the foregoing limitation is found to be invalid, you agree that SpotMe&rsquo;s total liability for all damages, losses, or causes of action of any kind or nature shall be limited to the greatest extent permitted by applicable law.</span></p>\n" +
                        "<p class=\"p2\"><span class=\"s1\">The website may provide links to other third party websites. However, since SpotMe has no control over such third-party websites, you acknowledge and agree that SpotMe is not responsible for the availability of such third party websites, and does not endorse and is not responsible or liable for any content, advertising, products or other materials on or available from such third party websites. You further acknowledge and agree that SpotMe shall not be responsible or liable, directly or indirectly, for any damage or loss caused or alleged to be caused by or in connection with use of or reliance on any such content, goods or services available on or through any such third party websites.</span></p>\n" +
                        "<p class=\"p2\"><span class=\"s1\"><strong>LINKS TO OTHER SITES OR RESOURCES</strong></span></p>\n" +
                        "<p class=\"p2\"><span class=\"s1\">The Services and App may contain links to third-party websites or resources. We provide these links only as a convenience and are not responsible for the content, products or services on or available from those websites or resources or links displayed on such websites. You acknowledge sole responsibility for and assume all risk arising from, your use of any third-party websites or resources.</span></p>\n" +
                        "<p class=\"p2\"><span class=\"s1\"><strong>GOVERNING LAW AND DISPUTE RESOLUTION</strong></span></p>\n" +
                        "<p class=\"p2\"><span class=\"s3\">Governing Law</span></p>\n" +
                        "<p class=\"p2\"><span class=\"s1\">These Terms and any action related thereto will be governed by the laws of the State of Colorado without regard to its conflict of laws provisions.</span></p>\n" +
                        "<p class=\"p2\"><span class=\"s3\">Agreement to Arbitrate</span></p>\n" +
                        "<p class=\"p2\"><span class=\"s1\">You and SpotMe agree that any dispute, claim or controversy arising out of or relating to these Terms or the breach, termination, enforcement, interpretation or validity thereof or the use of the Services, Products or Content (collectively, &ldquo;Disputes&rdquo;) will be settled by binding arbitration, except that each party retains the right: (i) to bring an individual action in small claims court and (ii) to seek injunctive or other equitable relief in a court of competent jurisdiction to prevent the actual or threatened infringement, misappropriation or violation of a party&rsquo;s copyrights, trademarks, trade secrets, patents or other intellectual property rights (the action described in the foregoing clause (ii), an &ldquo;IP Protection Action&rdquo;). Without limiting the preceding sentence, you will also have the right to litigate any other Dispute if you provide SpotMe with written notice of your desire to do so by email at support@Spotmeapp.net within thirty (30) days following the date you first agree to these Terms (such notice, an &ldquo;Arbitration Opt-out Notice&rdquo;). If you don&rsquo;t provide SpotMe with an Arbitration Opt-out Notice within the thirty (30) day period, you will be deemed to have knowingly and intentionally waived your right to litigate any Dispute except as expressly set forth in clauses (i) and (ii) above. The exclusive jurisdiction and venue of any IP Protection Action or, if you timely provide SpotMe with an Arbitration Opt-out Notice, will be the state and federal courts located in Colorado and each of the parties hereto waives any objection to jurisdiction and venue in such courts. Unless you timely provide SpotMe with an Arbitration Opt-out Notice, you acknowledge and agree that you and SpotMe are each waiving the right to a trial by jury or to participate as a plaintiff or class member in any purported class action or representative proceeding. Further, unless both you and SpotMe otherwise agree in writing, the arbitrator may not consolidate more than one person&rsquo;s claims, and may not otherwise preside over any form of any class or representative proceeding. If this specific paragraph is held unenforceable, then the entirety of this &ldquo;Dispute Resolution&rdquo; section will be deemed void. Except as provided in the preceding sentence, this &ldquo;Dispute Resolution&rdquo; section will survive any termination of these Terms</span></p>\n" +
                        "<p class=\"p2\"><span class=\"s3\">Arbitration Rules</span></p>\n" +
                        "<p class=\"p2\"><span class=\"s1\">The arbitration will be administered by the American Arbitration Association (&ldquo;AAA&rdquo;) in accordance with the Commercial Arbitration Rules and the Supplementary Procedures for Consumer-Related Disputes (the &ldquo;AAA Rules&rdquo;) then in effect, except as modified by this &ldquo;Dispute Resolution&rdquo; section. (The AAA Rules are available at www.adr.org/arb_med or by calling the AAA at 1-800-778-7879.) The Federal Arbitration Act will govern the interpretation and enforcement of this Section.</span></p>\n" +
                        "<p class=\"p2\"><span class=\"s3\">Arbitration Process</span></p>\n" +
                        "<p class=\"p2\"><span class=\"s1\">A party who desires to initiate arbitration must provide the other party with a written Demand for Arbitration as specified in the AAA Rules. The arbitrator will be either a retired judge or an attorney licensed to practice law and will be selected by the parties from the AAA&rsquo;s roster of arbitrators. If the parties are unable to agree upon an arbitrator within seven (7) days of delivery of the Demand for Arbitration, then the AAA will appoint the arbitrator in accordance with the AAA Rules.</span></p>\n" +
                        "<p class=\"p2\"><span class=\"s3\">Arbitration Location and Procedure</span></p>\n" +
                        "<p class=\"p2\"><span class=\"s1\">Unless you and SpotMe otherwise agree, the arbitration will be conducted in the county where you reside. If your claim does not exceed $10,000, then the arbitration will be conducted solely on the basis of the documents that you and SpotMe submit to the arbitrator, unless you request a hearing or the arbitrator determines that a hearing is necessary. If your claim exceeds $10,000, your right to a hearing will be determined by the AAA Rules. Subject to the AAA Rules, the arbitrator will have the discretion to direct a reasonable exchange of information by the parties, consistent with the expedited nature of the arbitration.</span></p>\n" +
                        "<p class=\"p2\"><span class=\"s3\">Arbitrator&rsquo;s Decision</span></p>\n" +
                        "<p class=\"p2\"><span class=\"s1\">The arbitrator will render an award within the time frame specified in the AAA Rules. The arbitrator&rsquo;s decision will include the essential findings and conclusions upon which the arbitrator based the award. Judgment on the arbitration award may be entered in any court having jurisdiction thereof. The arbitrator&rsquo;s award of damages must be consistent with the terms of the &ldquo;Limitation of Liability&rdquo; section above as to the types and amounts of damages for which a party may be held liable. The arbitrator may award declaratory or injunctive relief only in favor of the claimant and only to the extent necessary to provide relief warranted by the claimant&rsquo;s individual claim. If you prevail in arbitration you will be entitled to an award of attorneys&rsquo; fees and expenses, to the extent provided under applicable law. SpotMe will not seek, and hereby waives all rights it may have under applicable law to recover, attorneys&rsquo; fees and expenses if it prevails in arbitration.</span></p>\n" +
                        "<p class=\"p2\"><span class=\"s3\">Fees</span></p>\n" +
                        "<p class=\"p2\"><span class=\"s1\">We will pay all filing and administration fees charged by the Administrator and arbitrator fees up to $1,000, and we will consider your request to pay any additional arbitration costs. If an arbitrator issues an award in our favor, you will not be required to reimburse us for any fees we have previously paid to the administrator or for which we are responsible. If you receive an award from the arbitrator, we will reimburse you for any fees paid by you to the administrator or arbitrator. Each party shall bear its own attorney's, expert's and witness fees, which shall not be considered costs of arbitration; however, if a statute gives you the right to recover these fees, or fees paid to the administrator or arbitrator, then these statutory rights will apply in arbitration.</span></p>\n" +
                        "<p class=\"p2\"><span class=\"s3\">Changes</span></p>\n" +
                        "<p class=\"p2\"><span class=\"s1\">Notwithstanding the provisions as regards how we can make changes to this Terms, if SpotMe changes this &ldquo;Dispute Resolution&rdquo; section after the date you first accepted these Terms (or accepted any subsequent changes to these Terms), you may reject any such change by sending us written notice within 30 days of the date such change became effective, as indicated in the &ldquo;Last Updated&rdquo; date above or in the date of SpotMe&rsquo;s email to you notifying you of such change. By rejecting any change, you are agreeing that you will arbitrate any Dispute between you and SpotMe in accordance with the provisions of this &ldquo;Dispute Resolution&rdquo; section as of the date you first accepted these Terms (or accepted any subsequent changes to these Terms)</span></p>\n" +
                        "<p class=\"p3\">&nbsp;</p>\n" +
                        "<p class=\"p2\"><span class=\"s1\"><strong>GENERAL TERMS</strong></span></p>\n" +
                        "<p class=\"p2\"><span class=\"s3\">Entire Agreement:</span><span class=\"s1\"> These Terms constitute the entire and exclusive understanding and agreement between SpotMe and you regarding the Services, Products and Content, and these Terms supersede and replace any and all prior oral or written understandings or agreements between SpotMe and you regarding the Services, Products and Content. </span></p>\n" +
                        "<p class=\"p2\"><span class=\"s3\">Severability:</span><span class=\"s1\"> If any provision of these Terms is held invalid or unenforceable (either by an arbitrator appointed pursuant to the terms of the &ldquo;Arbitration&rdquo; section above or by a court of competent jurisdiction, but only if you timely opt-out of arbitration by sending us an Arbitration Opt-out Notice in accordance with the terms set forth above), that provision will be enforced to the maximum extent permissible and the other provisions of these Terms will remain in full force and effect. </span></p>\n" +
                        "<p class=\"p2\"><span class=\"s3\">Assignment:</span><span class=\"s1\"> You may not assign or transfer these Terms, by operation of law or otherwise, without SpotMe&rsquo;s prior written consent. Any attempt by you to assign or transfer these Terms, without such consent, will be null and of no effect. SpotMe may freely assign or transfer these Terms without restriction. Subject to the foregoing, these Terms will bind and inure to the benefit of the parties, their successors and permitted assigns. </span></p>\n" +
                        "<p class=\"p2\"><span class=\"s3\">Notices:</span><span class=\"s1\"> Any notices or other communications provided by SpotMe under these Terms, including those regarding modifications to these Terms, will be given: (i) by SpotMe via email; or (ii) by posting to the Services. For notices made by e-mail, the date of receipt will be deemed the date on which such notice is transmitted. </span></p>\n" +
                        "<p class=\"p2\"><span class=\"s3\">No Waiver:</span><span class=\"s1\"> SpotMe&rsquo;s failure to enforce any right or provision of these Terms will not be considered a waiver of such right or provision. The waiver of any such right or provision will be effective only if in writing and signed by a duly authorized representative of SpotMe. Except as expressly set forth in these Terms, the exercise by either party of any of its remedies under these Terms will be without prejudice to its other remedies under these Terms or otherwise.</span></p>\n" +
                        "<p class=\"p2\"><span class=\"s1\"><strong>CONTACT INFORMATION</strong></span></p>\n" +
                        "<p class=\"p4\"><span class=\"s1\">If you have any questions about these Terms or the Services or Products, please contact SpotMe at </span><span class=\"s2\">support@Spotmeapp.net</span><span class=\"s1\">.</span></p>");
                startActivity(a);
            }
        });
    }

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        Calendar calendar = new GregorianCalendar(year, monthOfYear, dayOfMonth);
        dob.setText(formater1.format(calendar.getTime()));
    }

    public void makeStripeAccount(String uid)
    {
        showProgress();
        try {
            RequestQueue requestQueue = Volley.newRequestQueue(SignupActivity.this);
            String URL = "https://us-central1-spotme-39709.cloudfunctions.net/createAccount";
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("user_id",uid);
            final String requestBody = jsonBody.toString();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.i("VOLLEY", response);
                    try {
                        JSONObject object = new JSONObject(response);
                        JSONObject data = new JSONObject(object.getString("data"));
                        if (data.getBoolean("Error")) {
                       //     showMessage(data.getString("Message"));
                        } else {
//                            Intent intent=new Intent(SignupActivity.this,OTPActivity.class);
//                            intent.putExtra("phone",phone.getText().toString());
//                            startActivity(intent);

                        //    startActivity(new Intent(SignupActivity.this, MainActivity.class));
                        }
                        showMessage("Email sent! Please verify the link sent to your email");
                     //   finish();

                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    hideProgress();
                }
            }, error -> {
                startActivity(new Intent(SignupActivity.this, SplashActivity.class));
                Log.e("VOLLEY", error.toString());
                hideProgress();
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