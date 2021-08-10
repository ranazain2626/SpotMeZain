package com.munib.spotme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.gcacace.signaturepad.views.SignaturePad;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.munib.spotme.adapters.ProposedMoneyAdapter;
import com.munib.spotme.adapters.SearchSpotMeUsersAdapter;
import com.munib.spotme.base.BaseActivity;
import com.munib.spotme.dataModels.OffersModel;
import com.munib.spotme.dataModels.ProposedMoneyModel;
import com.munib.spotme.dataModels.UserModel;
import com.munib.spotme.utils.RecyclerItemClickListener;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import me.ibrahimsn.lib.OnItemSelectedListener;

import static com.munib.spotme.utils.CommonUtils.TAGZ;

public class LendMoneyActivity extends BaseActivity {

    EditText username,interest_rate,amount,selected_username;
    UserModel selectedUser;
    TextView lend_btn,agreement_btn;
    RecyclerView rv;
    SearchSpotMeUsersAdapter adapter;
    ArrayList<UserModel> arrayList;
    Spinner duration;
    Boolean selected=false,signed=false;
    CardView layout_amount_interest;
    TextView amountAfterLoan;
    CardView proposed_payment_card;
    String url="",selected_user_token="";
    int selected_duration;
    double amountAfterLoan_value=0;
    ArrayList<ProposedMoneyModel> proposedMoneyArrayList=new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lend_money);

        ImageView back_btn=(ImageView) findViewById(R.id.back_btn);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        arrayList=new ArrayList<>();
        username=(EditText) findViewById(R.id.edtUsername);
        rv=(RecyclerView) findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter=new SearchSpotMeUsersAdapter(this,arrayList);
        rv.setAdapter(adapter);

        layout_amount_interest=(CardView) findViewById(R.id.layout_amount_interest);
        amountAfterLoan=(EditText) findViewById(R.id.amountAfterLoan);
        proposed_payment_card=(CardView) findViewById(R.id.proposed_payment_card);
        proposed_payment_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater factory = LayoutInflater.from(LendMoneyActivity.this);
                final View deleteDialogView = factory.inflate(R.layout.dialog_proposed_payments, null);
                final AlertDialog deleteDialog = new AlertDialog.Builder(LendMoneyActivity.this).create();
                deleteDialog.setView(deleteDialogView);
                deleteDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

                RecyclerView rv1=(RecyclerView) deleteDialogView.findViewById(R.id.rv);
                TextView ok_btn=(TextView) deleteDialogView.findViewById(R.id.ok_btn);
                ok_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        deleteDialog.dismiss();
                    }
                });
                rv1.setLayoutManager(new LinearLayoutManager(LendMoneyActivity.this));

                ProposedMoneyAdapter adapter1=new ProposedMoneyAdapter(LendMoneyActivity.this,proposedMoneyArrayList);
                rv1.setAdapter(adapter1);

                deleteDialog.show();
            }
        });

        duration=(Spinner) findViewById(R.id.edtDuration);
        //payment_plan=(EditText) findViewById(R.id.edtPaymentPlan);
        interest_rate=(EditText) findViewById(R.id.edtInterestRate);
        amount=(EditText) findViewById(R.id.edtAmount);

        interest_rate.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if(s.length() != 0) {
                    calculateInterestAmount();
                    amount.setEnabled(false);
                    //amount;
                }else{
                    amount.setEnabled(true);
                }
            }
        });

        amount.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if(s.length() != 0)
                    calculateInterestAmount();
            }
        });

        lend_btn=(TextView) findViewById(R.id.lend_btn);
        selected_username=(EditText) findViewById(R.id.selectedUsername);
        selected_username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                username.setVisibility(View.VISIBLE);
                selected_username.setVisibility(View.GONE);
                rv.setVisibility(View.VISIBLE);
            }
        });

        List<String> categories = new ArrayList<String>();
        categories.add("Please select duration");
        categories.add("1 Month");
        categories.add("2 Months");
        categories.add("3 Months");
        categories.add("4 Months");
        categories.add("5 Months");
        categories.add("6 Months");
        categories.add("7 Months");
        categories.add("8 Months");
        categories.add("9 Months");
        categories.add("10 Months");
        categories.add("11 Months");
        categories.add("1 Year");

        duration.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                if(pos!=0) {
                    selected_duration=pos;
                    proposed_payment_card.setVisibility(View.VISIBLE);
                    for(int i=0;i<selected_duration;i++)
                    {
                        Calendar cal = Calendar.getInstance();
                        cal.add(Calendar.MONTH, (i+1));
                        double a=amountAfterLoan_value/selected_duration;
                        double roundOff = Math.round(a*100)/100;
                        SimpleDateFormat month_date = new SimpleDateFormat("MMMM");
                        SimpleDateFormat sdf = new SimpleDateFormat("EE MMM dd HH:mm:ss ZZZ yyyy",
                            Locale.ENGLISH);
                        String month_name = month_date.format(cal.getTime());
                        proposedMoneyArrayList.add(new ProposedMoneyModel((i+1),month_name,roundOff,sdf.format(cal.getTime()),0,0));
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        duration.setAdapter(dataAdapter);

        lend_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(selected_username.getText().toString().equals(""))
                {
                    username.setError("Please select the user");
                }else if(amount.getText().toString().equals(""))
                {
                    amount.setError("Please enter the amount");
                }else if(interest_rate.getText().toString().equals(""))
                {
                    interest_rate.setError("Please enter interest rate");
                }else if(proposedMoneyArrayList.size()==0){
                    showMessage("Please select duration");
                }else{

                    LayoutInflater factory = LayoutInflater.from(LendMoneyActivity.this);
                    final View deleteDialogView = factory.inflate(R.layout.dialog_loan_agreement, null);
                    final AlertDialog deleteDialog = new AlertDialog.Builder(LendMoneyActivity.this).create();
                    deleteDialog.setView(deleteDialogView);
                    deleteDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

                    WebView webView=(WebView) deleteDialogView.findViewById(R.id.webview);
                    webView.getSettings().setJavaScriptEnabled(true);
                    webView.setWebViewClient(new WebViewClient());

                    LinearLayout sign_layout=(LinearLayout) deleteDialogView.findViewById(R.id.sign_layout);
                    LinearLayout agreement_layout=(LinearLayout) deleteDialogView.findViewById(R.id.agreement_layout);
                    SignaturePad mSignaturePad = (SignaturePad) deleteDialogView.findViewById(R.id.signature_pad);
                    mSignaturePad.setOnSignedListener(new SignaturePad.OnSignedListener() {

                        @Override
                        public void onStartSigning() {
                            //Event triggered when the pad is touched
                        }

                        @Override
                        public void onSigned() {
                            //Event triggered when the pad is signed
                            signed=true;
                        }

                        @Override
                        public void onClear() {
                            //Event triggered when the pad is cleared
                        }
                    });

                    url=
                            "<p><strong>SPOTME LOAN AGREEMENT</strong></p>\n" +

                                    "<p>THIS LOAN AGREEMENT (the &ldquo;Agreement&rdquo;), is made and entered into by and between <u><b>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+"(@"+currentUserData.getUsername()+") "+currentUserData.getName()+"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</b></u> (hereinafter, known as &ldquo;Lender&rdquo;) and  <u><b>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+"(@"+selectedUser.getUsername()+") "+selectedUser.getName()+"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</b></u>, (hereinafter, known as &ldquo;Borrower&rdquo;). Both the Lender and the Borrower shall collectively be referred to herein as &ldquo;the Parties&rdquo;. This Agreement governs the process by which you may make a request or requests for a loan from other users of SpotMe through the website spotme.net or the spotme app, including any subdomains thereof, or other application channels offered by us (collectively, the \"Site\"). If you make a loan request, and if that request results in a loan that is approved and funded by us, then your loan will be governed by the terms of the Loan Agreement.</p>\n" +
                                    "<ol>\n" +
                                    "<li><strong>LOAN AND TERMS OF REPAYMENT</strong></li>\n" +
                                    "</ol>\n" +
                                    "<ol>\n" +
                                    "<li>Promise to pay: FOR VALUE RECEIVED, the undersigned; the borrower, hereby promises to pay to the Lender, or registered assigns on the Maturity Date (as hereafter defined), such principal amount as from time to time may be advanced hereunder. &nbsp;Annexed hereto and made a part hereof is a schedule (the &ldquo;Loan and Repayment Schedule&rdquo;) on which shall be shown all loans of principal made by the Lender to the Borrower and all repayments of principal made by the Borrower to the Lender hereunder.</li>\n" +
                                    "<li>Loan Amount: The Lender promises to loan <u><b>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;$"+amount.getText().toString()+" USD&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</b></u>to the Borrower and the Borrower promises to repay this principal amount to the Lender according to the terms of this agreement.</li>\n" +
                                    "<li>Interest<strong>:</strong> &nbsp;The Borrower shall also pay interest (calculated on the basis of a 360-day year of twelve 30-day months) on such principal amount or the portion thereof from time to time outstanding hereunder at a rate of <u><b>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+interest_rate.getText().toString()+"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</b></u> percent (<u><b>&nbsp;&nbsp;&nbsp;"+interest_rate.getText().toString()+"&nbsp;&nbsp;&nbsp;</b></u>%) per annum; but in no event shall the interest exceed the maximum rate of nonusurious interest permitted by law to be paid by the Lender (and to the extent permitted by law, interest on any overdue principal or interest thereon).</li>\n" +
                                    "</ol>\n" +
                                    "<ol start=\"2\">\n" +
                                    "<li><strong>SECURITY</strong></li>\n" +
                                    "</ol>\n" +
                                    "<p>There shall be no security put forth by the Borrower in this agreement.<span class=\"Apple-converted-space\">&nbsp;</span></p>\n" +
                                    "<ol start=\"3\">\n" +
                                    "<li><strong>PAYMENTS</strong></li>\n" +
                                    "</ol>\n" +
                                    "<p>Payments shall be made by (a) check made payable to the Lender, (b) an assignment of certain assets, or (c) by a combination of the foregoing.<span class=\"Apple-converted-space\">&nbsp; </span>All payments hereon shall be applied first, to costs and expenses and other amounts owing to the Lender under this Note; second, to accrued interest then payable; and third, to the principal.<span class=\"Apple-converted-space\">&nbsp; </span>The Lender shall have full recourse against the undersigned.</p>\n" +
                                    "<ol start=\"4\">\n" +
                                    "<li><strong>PREPAYMENT</strong></li>\n" +
                                    "</ol>\n" +
                                    "<p>The Borrower shall be entitled to prepay the Loan in whole or in part, at any time and from time to time; provided, however, that the Borrower shall give notice to the Lender of any such prepayment; and provided, further, that any partial prepayment of the Loan shall be in an amount not less than 20% of the total payment due. Any such prepayment shall be: (a) permanent and irrevocable; (b) accompanied by all accrued interest through the date of such prepayment; (c) made without premium or penalty; and (d) applied on the inverse order of the maturity of the installment thereof unless the Lender and the Borrower agree to apply such prepayments in some other order.</p>\n" +
                                    "<p>If Borrower prepays this Note in part, Borrower agrees to continue to make regularly scheduled payments until all amounts due under this Note are paid. You may accept late payments or partial payments, even though marked \"paid in full\", without losing any rights under this Note.</p>\n" +
                                    "<ol start=\"5\">\n" +
                                    "<li><strong>LOAN FEES AND CHARGES</strong></li>\n" +
                                    "</ol>\n" +
                                    "<p>Borrower shall pay to SPotMe, at loan closing all loan fees payable by Borrower in connection with the Loan. Borrower is obligated to pay a Loan Fee, at the time of each loan disbursement, in the amount of 2% of amounts actually loaned to Borrower through the platform. The fee will be deducted from Borrower's loan proceeds, so the loan proceeds delivered to Borrower will be less than the face amount of Borrower's loan request.</p>\n" +
                                    "<ol start=\"6\">\n" +
                                    "<li><strong>USE OF FUNDS</strong></li>\n" +
                                    "</ol>\n" +
                                    "<p>The Borrower covenants that it shall apply the proceeds of the Loan solely for the purposes described in the Application. The undersigned acknowledges and agrees that by signing on behalf of the Borrower below he or she shall be personally liable for the repayment of the Loan if (1) any of the information submitted to the Lender or the Department of Economic Development in connection with the Loan is false or misleading, or (2) the proceeds of the Loan are applied for any purposes other than those described in the Application.</p>\n" +
                                    "<ol start=\"7\">\n" +
                                    "<li><strong>EVENTS OF DEFAULT</strong></li>\n" +
                                    "</ol>\n" +
                                    "<p>The entire unpaid principal of this agreement, and the interest then accrued thereon, shall become and be immediately due and payable upon the written demand of the Lender, without any other notice or demand of any kind or any presentment or protest, if any one of the following events (hereafter an &ldquo;Event of Default&rdquo;) shall occur and be continuing at the time of such demand, whether voluntarily or involuntarily, or without limitation, occurring or brought about by operation of law or pursuant to or in compliance with any judgment, decree or order of any court or any order, rules, or regulation of any administrative or governmental body.</p>\n" +
                                    "<ol>\n" +
                                    "<li>Nonpayment of loan: if the Borrower shall fail to make payment when due of the principal on the agreement, or interest accrued thereon, and if the default shall remain unremedied for 15 days.</li>\n" +
                                    "<li>Incorrect representation: The Lender determines that any material representation, warranty or certification contained in, or made in connection with the Application, the execution and delivery of this Agreement, or in any document related hereto, including any disbursement request, shall prove to have been incorrect.</li>\n" +
                                    "<li>Default in covenant: The Borrower shall default in the performance of any other term, covenant, or agreement contained in this Agreement, and such default shall continue unremedied for fifteen (15) days after either: (i) it becomes known to an executive officer of the Borrower; or (ii) written notice thereof shall have been given to the Borrower by the Lender.<span class=\"Apple-converted-space\">&nbsp;</span></li>\n" +
                                    "<li>Insolvency: In the event that the Borrower become insolvent or shall cease to pay its debts as they mature or shall voluntarily file, or have filed against it, a petition seeking reorganization of, or the appointment of a receiver, trustee, or liquidation for it or a substantial portion of its assets, or to effect a plan or other arrangement with creditors, or shall be adjudicated bankrupt, or shall make a voluntary assignment for the benefit of creditors.</li>\n" +
                                    "<li>Lender&rsquo;s right upon default: Upon the occurrence of an Event of Default, Lender may exercise all remedies available under applicable law and this Note, including without limitation, accelerate all amounts owed on this agreement and demand that Borrower immediately pay such amounts.<span class=\"Apple-converted-space\">&nbsp; </span>Lender may report information about Borrower's account to credit bureaus. Should there be more than one Borrower, Lender may report that loan account to the credit bureaus in the names of all Borrowers. Late payments, missed payments, or other defaults on an account may be reflected in Borrower's credit report. Borrower agrees to pay all costs of collecting any delinquent payments, including reasonable attorneys' fees, as permitted by applicable law.</li>\n" +
                                    "</ol>\n" +
                                    "<ol start=\"8\">\n" +
                                    "<li><strong>WAIVERS</strong></li>\n" +
                                    "</ol>\n" +
                                    "<p>No failure or delay on the part of the Lender in exercising any right, power, or remedy hereunder shall operate as a waiver thereof, nor shall any single or partial exercise or any such right, power, or remedy preclude any other or further exercise thereof or the exercise of any other right, power, or remedy hereunder. No modification or waiver of any provision of this Agreement or of this Note, nor any consent to any departure by the Borrower therefrom, shall in any event be effective unless the same shall be in writing, and then such waiver or consent shall be effective only in the specific instance and for the specific purpose for which given. No notice to or demand on the Borrower in any case shall entitle the Borrower to any other or further notice or demand in similar or other circumstances.</p>\n" +
                                    "<p>The Borrower hereby waives presentment, protest, demand for payment, notice of dishonor and all other notices or demands in connection with the delivery, acceptance, performance, default or endorsement of this Note.<span class=\"Apple-converted-space\">&nbsp; </span>No waiver by the Lender of any default shall be effective unless in writing nor shall it operate as a waiver of any other default or of the same default on a future occasion.</p>\n" +
                                    "<ol start=\"9\">\n" +
                                    "<li><strong>ELECTRONIC TRANSACTION NOTICE</strong></li>\n" +
                                    "</ol>\n" +
                                    "<p>THIS AGREEMENT IS FULLY SUBJECT TO BORROWER&rsquo;S CONSENT TO ELECTRONIC TRANSACTIONS AND DISCLOSURES, WHICH CONSENT IS SET FORTH IN THE TERMS OF USE FOR THE SITE. BORROWER EXPRESSLY AGREES THAT THE NOTE IS A \"TRANSFERABLE RECORD\" FOR ALL PURPOSES UNDER THE ELECTRONIC SIGNATURES IN GLOBAL AND NATIONAL COMMERCE ACT AND THE UNIFORM ELECTRONIC TRANSACTIONS ACT.<span class=\"Apple-converted-space\">&nbsp;</span></p>\n" +
                                    "<ol start=\"10\">\n" +
                                    "<li><strong>SUCCESSORS AND ASSIGNS</strong></li>\n" +
                                    "</ol>\n" +
                                    "<p>This agreement shall be binding upon the Borrower and its successors and assigns.</p>\n" +
                                    "<ol start=\"11\">\n" +
                                    "<li><strong> COUNTERPARTS</strong></li>\n" +
                                    "</ol>\n" +
                                    "<p>This Agreement may be executed in any number of counterparts, each of which shall be deemed an original, but all of which together shall constitute one and the same instrument.</p>\n" +
                                    "<ol start=\"12\">\n" +
                                    "<li><strong>BINDING EFFECT</strong></li>\n" +
                                    "</ol>\n" +
                                    "<p>This agreement will pass to the benefit of and shall be binding upon the executors, heir, administrators, successors and permitted assigns of the Borrower and Lender</p>\n" +
                                    "<ol start=\"13\">\n" +
                                    "<li><strong>COST OF COLLECTION</strong></li>\n" +
                                    "</ol>\n" +
                                    "<p>The Borrower agrees to pay all costs of collection of this agreement, including, without limitation, reasonable attorneys&rsquo; fees and costs, in the event it is not paid when due. <span class=\"Apple-converted-space\">&nbsp;</span></p>\n" +
                                    "<ol start=\"14\">\n" +
                                    "<li><strong>SEVERABILITY</strong></li>\n" +
                                    "</ol>\n" +
                                    "<p>The terms, paragraphs and clauses contained herein this agreement are intended to be read and construed independently of each other. In the event that any one or more provisions of this Agreement shall be held to be illegal, invalid or otherwise unenforceable, the same shall not affect any other provision of this Agreement and the remaining provisions of this Agreement shall remain in full force and effect.</p>\n" +
                                    "<ol start=\"15\">\n" +
                                    "<li><strong>AMENDMENTS</strong></li>\n" +
                                    "</ol>\n" +
                                    "<p>This Agreement may be amended and modified only by a writing executed by the Borrower and the Lender herein.</p>\n" +
                                    "<ol start=\"16\">\n" +
                                    "<li><strong>APPLICABLE LAW</strong></li>\n" +
                                    "</ol>\n" +
                                    "<p>This Agreement shall be interpreted and construed in accordance with, and all actions hereunder shall be governed by, the laws of the State of Colorado, without giving effect to principles thereof relating to conflicts of law.</p>"
                                +"<div class='parent'>\n\n\n" +
                                "  <div style='display:inline-block'><center><img src='{IMAGE_PLACEHOLDER}'></br><u>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+currentUserData.getName()+"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</u></center></div>\n" +
                                "  <div style='display:inline-block'><center><img src='{IMAGE_PLACEHOLDER1}'></br><u>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+selectedUser.getName()+"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</u></center></div>\n" +
                                "</div>";

//                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//                    mSignaturePad.getSignatureBitmap().compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
//                    byte[] byteArray = byteArrayOutputStream.toByteArray();
//                    String imgageBase64 = Base64.encodeToString(byteArray, Base64.DEFAULT);
//                    String image = "data:image/png;base64," + imgageBase64;
//
//// Use image for the img src parameter in your html and load to webview
//                    url = url.replace("{IMAGE_PLACEHOLDER}", image);

                    // If you deal with HTML then execute loadData instead of loadUrl
                    try {
                        webView.loadData(url,"text/html", "UTF-8");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                    CheckBox checkbox=(CheckBox) deleteDialogView.findViewById(R.id.checkBox);

                    deleteDialogView.findViewById(R.id.clear_signatures).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mSignaturePad.clear();
                            signed=false;
                        }
                    });
                    deleteDialogView.findViewById(R.id.sign_agreement_btn).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //your business logic
                            if(!checkbox.isChecked())
                            {
                                showMessage("Please agree to the loan agreement above");
                            }else{
                                deleteDialogView.findViewById(R.id.sign_agreement_btn).setVisibility(View.GONE);
                                deleteDialogView.findViewById(R.id.continue_btn).setVisibility(View.VISIBLE);
                                sign_layout.setVisibility(View.VISIBLE);
                                agreement_layout.setVisibility(View.GONE);

                            }
                        }
                    });

                    deleteDialogView.findViewById(R.id.continue_btn).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(!signed)
                            {
                                showMessage("Please input your signatures");
                            }else{

                                deleteDialogView.findViewById(R.id.sign_agreement_btn).setVisibility(View.GONE);
                                deleteDialogView.findViewById(R.id.continue_btn).setVisibility(View.GONE);
                                deleteDialogView.findViewById(R.id.finish_btn).setVisibility(View.VISIBLE);
                                sign_layout.setVisibility(View.GONE);
                                agreement_layout.setVisibility(View.VISIBLE);
                                ((TextView)deleteDialogView.findViewById(R.id.textView26)).setText("Review Signed Agreement");

                                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                                Bitmap b=Bitmap.createScaledBitmap(mSignaturePad.getTransparentSignatureBitmap(), 150, 120, false);
                   b.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                    byte[] byteArray = byteArrayOutputStream.toByteArray();
                    String imgageBase64 = Base64.encodeToString(byteArray, Base64.DEFAULT);
                    String image = "data:image/png;base64," + imgageBase64;

// Use image for the img src parameter in your html and load to webview
                    url = url.replace("{IMAGE_PLACEHOLDER}", image);

                                try {
                                    webView.loadData(url,"text/html", "UTF-8");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }


                            }
                        }
                    });

                    deleteDialogView.findViewById(R.id.finish_btn).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            deleteDialog.dismiss();
                            showProgress();

                                new Timer().schedule(new TimerTask() {
                                    @Override
                                    public void run() {

                                        long timez=Calendar.getInstance().getTimeInMillis();
                                        for(int i=0;i<proposedMoneyArrayList.size();i++)
                                        {
                                            ProposedMoneyModel model=proposedMoneyArrayList.get(i);
                                            database.getReference("payments").child(currentUserData.getUid()+"_"+selectedUser.getUid()+"_"+timez).child(model.getIndex()+"").setValue(model);
                                        }

                                        OffersModel userModel=new OffersModel("lend",amount.getText().toString(),amountAfterLoan_value+"",interest_rate.getText().toString(),duration.getSelectedItem().toString(),0,new Date().getTime()+"",true,false,url,currentUserData.getUid(),selectedUser.getUid());
                                        database.getReference("offers").child(currentUserData.getUid()+"_"+selectedUser.getUid()+"_"+ timez).setValue(userModel);
                                        runOnUiThread(new Runnable() {
                                            public void run() {
                                                if(selected_user_token!=null) {
                                                    sendNotification(currentUserData.getName() + " has sent you an offer to lend $" + amount.getText().toString(), selected_user_token);
                                                    database.getReference("notifications").child(selectedUser.getUid()).push().child("notification").setValue(currentUserData.getName() + " has sent you an offer to lend $" + amount.getText().toString());

                                                }

                                                Toast.makeText(LendMoneyActivity.this,"Request Sent Successfully!",Toast.LENGTH_LONG).show();
                                                hideProgress();
                                            }
                                        });

                                        finish();
                                    }
                                },2000);
                        }
                    });

                    deleteDialog.show();

//                    Intent a=new Intent(LendMoneyActivity.this,LoanAgreementActivity.class);
//                    a.putExtra("lender","(@"+currentUser.getUsername()+") "+currentUser.getName());
//                    a.putExtra("borrower","(@"+selectedUser.getUsername()+") "+selectedUser.getName());
//                    a.putExtra("amount",amount.getText().toString());
//                    a.putExtra("rate",interest_rate.getText().toString());
//                    startActivity(a);
                }
            }
        });

        rv.addOnItemTouchListener(
                new RecyclerItemClickListener(LendMoneyActivity.this, rv ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        // do whatever
                        selectedUser=arrayList.get(position);
                        selected_username.setVisibility(View.VISIBLE);
                        selected_username.setText(arrayList.get(position).getUsername());
                        selected_user_token=arrayList.get(position).getDevice_token();
                        username.setVisibility(View.GONE);
                        username.setText(arrayList.get(position).getUsername());
                        rv.setVisibility(View.GONE);
                    }

                    @Override
                    public void onItemLongClick(View view, int position) {

                    }

                })
        );

        username.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                    if (!s.toString().equals("")) {
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users");
                        ref.orderByChild("username").startAt(s.toString()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    arrayList.clear();
                                    for (DataSnapshot datas : dataSnapshot.getChildren()) {
                                        Log.d(TAGZ, datas.toString());
                                        UserModel user = datas.getValue(UserModel.class);
                                        user.setUid(datas.getKey());
                                        if(!user.getUid().equals(auth.getCurrentUser().getUid()))
                                            arrayList.add(user);
                                    }
                                    adapter.notifyDataSetChanged();
                                } else {
                                    arrayList.clear();
                                    adapter.notifyDataSetChanged();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Log.d(TAGZ, error.getDetails());

                            }
                        });
                    } else {

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

                }

            }
        });
    }

    public void calculateInterestAmount()
    {
        if(amount.getText().toString().equals(""))
        {
            layout_amount_interest.setVisibility(View.GONE);
        }else if(interest_rate.getText().toString().equals(""))
        {
            layout_amount_interest.setVisibility(View.GONE);
        }else{
            layout_amount_interest.setVisibility(View.VISIBLE);
            double a=((Integer.parseInt(amount.getText().toString())*Integer.parseInt(interest_rate.getText().toString()))/100)+Integer.parseInt(amount.getText().toString());
            amountAfterLoan_value=a;
            amountAfterLoan.setText("$ "+a);
        }
    }
}