package com.munib.spotme.fragments;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.munib.spotme.BrowseActivity;
import com.munib.spotme.CreateBusinessProfileActivity;
import com.munib.spotme.DashboardLoginLinkActivity;
import com.munib.spotme.LendMoneyActivity;
import com.munib.spotme.LoginActivity;
import com.munib.spotme.MainActivity;
import com.munib.spotme.MessagesActivity;
import com.munib.spotme.PaymentSetupActivity;
import com.munib.spotme.R;
import com.munib.spotme.RequestMoneyActivity;
import com.munib.spotme.SendMessageActivity;
import com.munib.spotme.SignupActivity;
import com.munib.spotme.SplashActivity;
import com.munib.spotme.adapters.MyDealsLendingAdapter;
import com.munib.spotme.adapters.NotificationsAdapter;
import com.munib.spotme.base.BaseFragment;
import com.munib.spotme.dataModels.BusinessProfileModel;
import com.munib.spotme.dataModels.NotificationsModel;
import com.munib.spotme.dataModels.OffersModel;
import com.munib.spotme.dataModels.ThreadModel;
import com.munib.spotme.utils.ExampleEphemeralKeyProvider;
import com.squareup.picasso.Picasso;
import com.stripe.android.CustomerSession;
import com.stripe.android.EphemeralKeyProvider;
import com.stripe.android.EphemeralKeyUpdateListener;
import com.stripe.android.PaymentSession;
import com.stripe.android.PaymentSessionConfig;
import com.stripe.android.PaymentSessionData;
import com.stripe.android.model.Address;
import com.stripe.android.model.PaymentMethod;
import com.stripe.android.model.ShippingInformation;
import com.stripe.android.model.ShippingMethod;
import com.stripe.android.view.PaymentMethodsActivityStarter;
import com.stripe.android.view.ShippingInfoWidget;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.ContentValues.TAG;
import static com.munib.spotme.utils.CommonUtils.TAGZ;

public class HomeFragment extends BaseFragment {

    CardView request_card,lend_card,payment_setup,browse_card;
    TextView payment_setup_status;
    FrameLayout btn_messages;
    TextView total_loaned,total_borrowed,name;
    int total_borrowed_value=0,total_loaned_value=0;
    RelativeLayout edit_image;
    Uri image_url=null;
    CircleImageView profile_image;
    FrameLayout btn_logout;
    Uri downalod_url=null;
    RecyclerView rv1;
    NotificationsAdapter adapter;
    RequestQueue requestQueue;
    TextView notif_text;
    FrameLayout notif_dot;
    ArrayList<NotificationsModel> notificationsModels;
    private PaymentSession paymentSession;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);

        notif_text=(TextView) root.findViewById(R.id.notif_text);
        try {
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Picasso.get().load(currentUserData.getImage_url()).into(profile_image);
                            }catch (Exception ex)
                            {

                            }
                        }
                    });
                }
            },500);
        }catch (Exception ex)
        {
        }

        browse_card=(CardView) root.findViewById(R.id.browse_card);
        browse_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(getActivity(), BrowseActivity.class));
            }
        });

        payment_setup_status=(TextView) root.findViewById(R.id.payment_status);

        notif_dot=(FrameLayout) root.findViewById(R.id.notif_dot);
        payment_setup=(CardView) root.findViewById(R.id.payment_setup_pending);
        total_loaned=(TextView) root.findViewById(R.id.total_loaned);
        total_borrowed=(TextView) root.findViewById(R.id.total_borrowed);
        name=(TextView) root.findViewById(R.id.name);
        name.setText("Hi "+auth.getCurrentUser().getDisplayName());
        edit_image=root.findViewById(R.id.edit_image);
        profile_image=root.findViewById(R.id.profile_image);
        edit_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagePicker.Companion.with(HomeFragment.this)
                        .crop()	    			//Crop image(Optional), Check Customization for more option
                        .compress(1024)			//Final image size will be less than 1 MB(Optional)
                        .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                        .start();
            }
        });

        btn_logout=(FrameLayout) root.findViewById(R.id.btn_logout);
        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                getActivity().finish();
                startActivity(new Intent(getActivity(), LoginActivity.class));
            }
        });
        btn_messages=root.findViewById(R.id.btn_messages);
        btn_messages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                new PaymentMethodsActivityStarter(getActivity()).startForResult(new PaymentMethodsActivityStarter.Args.Builder()
//                        .setShouldShowGooglePay(true)
//                        .build());
                startActivity(new Intent(getActivity(), MessagesActivity.class));
            }
        });
        request_card=(CardView) root.findViewById(R.id.request_card);
        request_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                Boolean charges_enabled = preferences.getBoolean("charges_enabled", false);
                if(charges_enabled){
                    startActivity(new Intent(getActivity(), RequestMoneyActivity.class));
                }else{
                    showMessage("Please activate your payment setup before making a request to someone");
                }
            }
        });

        lend_card=(CardView) root.findViewById(R.id.lend_card);
        lend_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                Boolean charges_enabled = preferences.getBoolean("charges_enabled", false);
                if(charges_enabled){
                    startActivity(new Intent(getActivity(), LendMoneyActivity.class));
                }else{
                    showMessage("Please activate your payment setup before making a request to someone");
                }
            }
        });

        payment_setup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
              //  Boolean charges_enabled = preferences.getBoolean("charges_enabled", false);
                Boolean details_submitted = preferences.getBoolean("details_submitted", false);
                if(details_submitted){
                    startActivity(new Intent(getActivity(), DashboardLoginLinkActivity.class));
                }else{
                    startActivity(new Intent(getActivity(), PaymentSetupActivity.class));
                }

            }
        });

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }
                        // Get new FCM registration token
                        String token = task.getResult();

                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users").child(auth.getCurrentUser().getUid());
                        try {
                            ref.child("device_token").setValue(token);
                        }catch (Exception ex)
                        {

                        }

                    }
                });

        getStripInfo();


        rv1=root.findViewById(R.id.rv1);
        rv1.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,true));
        notificationsModels=new ArrayList<>();

        return root;
    }

    public void getStripInfo()
    {
        showProgress();
        requestQueue= Volley.newRequestQueue(getActivity());
        try {

            String URL = "https://us-central1-spotme-39709.cloudfunctions.net/getStripeAccountInfo";
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("user_id", auth.getCurrentUser().getUid());
            final String requestBody = jsonBody.toString();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.i("VOLLEY", response);
                    try {
                        JSONObject object = new JSONObject(response);
                        JSONObject data=new JSONObject(object.getString("data"));
                        if(data.getBoolean("Error"))
                        {
                            showMessage(data.getString("Message"));
                        }else{
                            Boolean charges_enabled=data.getJSONObject("stripe_account").getBoolean("charges_enabled");
                            Boolean details_submitted=data.getJSONObject("stripe_account").getBoolean("details_submitted");
                            Boolean payouts_enabled=data.getJSONObject("stripe_account").getBoolean("payouts_enabled");


                            if(details_submitted && charges_enabled)
                            {
                                payment_setup.setVisibility(View.GONE);
                            }else if(details_submitted && (!charges_enabled))
                            {
                                payment_setup_status.setText("Payments Missing Information / Pending Verification");
                                payment_setup.setVisibility(View.VISIBLE);
                            }
                            else
                            {
                                payment_setup.setVisibility(View.VISIBLE);
                            }

                            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putBoolean("charges_enabled",charges_enabled);
                            editor.putBoolean("details_submitted",details_submitted);
                            editor.putBoolean("payouts_enabled",payouts_enabled);

                            editor.apply();
                            //startActivity(new Intent(SignupActivity.this, MainActivity.class));
                        }
                    }catch (Exception ex)
                    {
                        ex.printStackTrace();
                    }
                    hideProgress();
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

//        CustomerSession.initCustomerSession(
//                getActivity(),
//                new ExampleEphemeralKeyProvider(getActivity())
//        );
//
//        paymentSession = new PaymentSession(
//                this,
//                createPaymentSessionConfig()
//        );
//        setupPaymentSession();
    }

    private void setupPaymentSession() {
        paymentSession.init(
                new PaymentSession.PaymentSessionListener() {

                    @Override
                    public void onPaymentSessionDataChanged(@NonNull PaymentSessionData data) {
                        if (data.getUseGooglePay()) {
                            // customer intends to pay with Google Pay
                            //paymentSession.presentPaymentMethodSelection(data.ge);
                        } else {
                            final PaymentMethod paymentMethod = data.getPaymentMethod();
                            if (paymentMethod != null) {
                                paymentSession.presentPaymentMethodSelection(paymentMethod.id);
                            }
                        }

                        // Update your UI here with other data
                        if (data.isPaymentReadyToCharge()) {
                            // Use the data to complete your charge - see below.
                        }
                    }


                    @Override
                    public void onError(int errorCode, @NotNull String errorMessage) {
                        Log.d("mubi",errorMessage);
                    }

                    @Override
                    public void onCommunicatingStateChanged(boolean isCommunicating) {
                        if (isCommunicating) {
                            // update UI to indicate that network communication is in progress
                        } else {
                            // update UI to indicate that network communication has completed
                        }
                    }
                }
        );

    }


    @NonNull
    private ShippingInformation getDefaultShippingInfo() {
        // optionally specify default shipping address
        return new ShippingInformation();
    }

    @NonNull
    private PaymentSessionConfig createPaymentSessionConfig() {
        return new PaymentSessionConfig.Builder()

                // hide the phone field on the shipping information form
                .setHiddenShippingInfoFields(
                        ShippingInfoWidget.CustomizableShippingField.Phone
                )

                // specify an address to pre-populate the shipping information form
                .setPrepopulatedShippingInfo(new ShippingInformation(
                        new Address.Builder()
                                .setLine1("123 Market St")
                                .setCity("San Francisco")
                                .setState("CA")
                                .setPostalCode("94107")
                                .setCountry("US")
                                .build(),
                        "Jenny Rosen",
                        "4158675309"
                ))

                // collect shipping information
                .setShippingInfoRequired(true)

                // collect shipping method
                .setShippingMethodsRequired(true)

                // specify the payment method types that the customer can use;
                // defaults to PaymentMethod.Type.Card
                .setPaymentMethodTypes(
                        Arrays.asList(PaymentMethod.Type.Card)
                )

                // only allow US and Canada shipping addresses
                .setAllowedShippingCountryCodes(new HashSet<>(
                        Arrays.asList("US", "CA")
                ))

//                // specify a layout to display under the payment collection form
//                .setAddPaymentMethodFooter(R.layout.add_payment_method_footer)

                // specify the shipping information validation delegate
                .setShippingInformationValidator(new AppShippingInformationValidator())

                // specify the shipping methods factory delegate
                .setShippingMethodsFactory(new AppShippingMethodsFactory())

                // if `true`, will show "Google Pay" as an option on the
                // Payment Methods selection screen
                .setShouldShowGooglePay(true)

                .build();
    }

    private static class AppShippingInformationValidator
            implements PaymentSessionConfig.ShippingInformationValidator {

        @Override
        public boolean isValid(
                @NonNull ShippingInformation shippingInformation
        ) {
            final Address address = shippingInformation.getAddress();
            return address != null && Locale.US.getCountry() == address.getCountry();
        }

        @NonNull
        public String getErrorMessage(
                @NonNull ShippingInformation shippingInformation
        ) {
            return "A US address is required";
        }
    }

    private static class AppShippingMethodsFactory
            implements PaymentSessionConfig.ShippingMethodsFactory {

        @Override
        public List<ShippingMethod> create(
                @NonNull ShippingInformation shippingInformation
        ) {
            return Arrays.asList(
                    new ShippingMethod(
                            "UPS Ground",
                            "ups-ground",
                            0,
                            "USD",
                            "Arrives in 3-5 days"
                    ),
                    new ShippingMethod(
                            "FedEx",
                            "fedex",
                            599,
                            "USD",
                            "Arrives tomorrow"
                    )
            );
        }
    }


    @Override
    protected void injectView() {

    }

    @Override
    public void onResume() {
        super.onResume();

        if(auth.getCurrentUser()!=null) {

            try {
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        try{
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Picasso.get().load(currentUserData.getImage_url()).into(profile_image);
                                } catch (Exception ex) {

                                }
                            }
                        });
                        }catch (Exception ex)
                        {

                        }

                    }
                },2000);
            }catch (Exception ex)
            {
            }

            DatabaseReference ref00 = FirebaseDatabase.getInstance().getReference().child("users");
            ref00.child("-789ade78").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        try{
                       int enabled=Integer.parseInt(dataSnapshot.child("enabled").getValue().toString());
                       if(enabled==0)
                       {
                           getActivity().finish();
                       }
                        }catch (Exception ex)
                        {

                        }
                    } else {

                    }
                    // hideProgress();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.d(TAGZ, error.getDetails());
                    // hideProgress();
                }
            });

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users");
            ref.child(auth.getCurrentUser().getUid()).child("conversations").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot datas : dataSnapshot.getChildren()) {
                            Log.d(TAGZ, datas.toString());
                            ThreadModel offer = datas.getValue(ThreadModel.class);
                            offer.setThread_id(datas.getKey());
                            if(offer.getNotification_count()>0)
                            {
                                notif_dot.setVisibility(View.VISIBLE);
                                break;
                            }
                            //    arrayList.add(offer);
                        }
                    } else {

                    }
                   // hideProgress();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.d(TAGZ, error.getDetails());
                   // hideProgress();
                }
            });
            FirebaseDatabase.getInstance().getReference().child("notifications").child(auth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    notificationsModels.clear();
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot datas : dataSnapshot.getChildren()) {
                            Log.d(TAGZ, datas.toString());
                            NotificationsModel offer = datas.getValue(NotificationsModel.class);
                            offer.setNotification_id(datas.getKey());
                            offer.setUser_id(auth.getCurrentUser().getUid());
                            notificationsModels.add(offer);
                        }
                        adapter=new NotificationsAdapter(getActivity(),notificationsModels);
                        rv1.setAdapter(adapter);
                       // total_loaned.setText("$"+total_loaned_value+".00");
                        notif_text.setVisibility(View.GONE);
                    } else {
                        rv1.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.d(TAGZ, error.getDetails());

                }
            });



            total_borrowed_value=0;
            total_loaned_value=0;
            FirebaseDatabase.getInstance().getReference().child("requests").
            orderByChild("lender").equalTo(auth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot datas : dataSnapshot.getChildren()) {
                            Log.d(TAGZ, datas.toString());
                            OffersModel offer = datas.getValue(OffersModel.class);
                            offer.setOffer_uid(datas.getKey());
                            if(offer.getStatus()==2){
                                total_loaned_value+=Integer.parseInt(offer.getAmount());
                            }
                        }
                        total_loaned.setText("$"+total_loaned_value+".00");
                    } else {

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.d(TAGZ, error.getDetails());

                }
            });

           FirebaseDatabase.getInstance().getReference().child("offers").
            orderByChild("lender").equalTo(auth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Log.d("mubi",dataSnapshot.toString());
                        for (DataSnapshot datas : dataSnapshot.getChildren()) {
                            Log.d(TAGZ, datas.toString());
                            OffersModel offer = datas.getValue(OffersModel.class);
                            offer.setOffer_uid(datas.getKey());
                            if (offer.getStatus() == 2) {
                                total_loaned_value += Integer.parseInt(offer.getAmount());
                            }
                        }
                            total_loaned.setText("$"+total_loaned_value+".00");

                    } else {

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.d(TAGZ, error.getDetails());

                }
            });
        }


        FirebaseDatabase.getInstance().getReference().child("requests").
                orderByChild("borrower").equalTo(auth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot datas : dataSnapshot.getChildren()) {
                        Log.d(TAGZ, datas.toString());
                        OffersModel offer = datas.getValue(OffersModel.class);
                        offer.setOffer_uid(datas.getKey());
                        if(offer.getStatus()==2){
                            total_borrowed_value+=Integer.parseInt(offer.getAmount());
                        }
                    }
                    total_borrowed.setText("$"+total_borrowed_value+".00");
                } else {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAGZ, error.getDetails());

            }
        });

        FirebaseDatabase.getInstance().getReference().child("offers").
                orderByChild("borrower").equalTo(auth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Log.d("mubi",dataSnapshot.toString());
                    for (DataSnapshot datas : dataSnapshot.getChildren()) {
                        Log.d(TAGZ, datas.toString());
                        OffersModel offer = datas.getValue(OffersModel.class);
                        offer.setOffer_uid(datas.getKey());
                        if (offer.getStatus() == 2) {
                            total_borrowed_value += Integer.parseInt(offer.getAmount());
                        }
                    }
                    total_borrowed.setText("$"+total_borrowed_value+".00");

                } else {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAGZ, error.getDetails());

            }
        });
    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode == Activity.RESULT_OK) {
//            Uri fileUri = data.getData();
//            profile_image.setImageURI(fileUri);
//
//            //You can get File object from intent
//            File file = ImagePicker.Companion.getFile(data);
//            image_url = fileUri;
//            showProgress();
//            uploadFile(image_url);
//        }
//    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            Uri fileUri = data.getData();
            profile_image.setImageURI(fileUri);

            //You can get File object from intent
            File file = ImagePicker.Companion.getFile(data);
            image_url = fileUri;
            showProgress();
            uploadFile(image_url);
        }
        if (data != null) {
            paymentSession.handlePaymentData(requestCode, resultCode, data);
        }
    }

    private void uploadFile(Uri ui) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl("gs://spotme-39709.appspot.com");
        StorageReference mountainImagesRef = storageRef.child("user_images/" + currentUserData.getUid()+"_" + Calendar.getInstance().getTimeInMillis());

        UploadTask uploadTask = mountainImagesRef.putFile(ui);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                showMessage("Image not uploaded. Please try again!");
                hideProgress();
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                mountainImagesRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        downalod_url = uri;
                        Log.d("mubi",downalod_url.toString());
                        HashMap<String, Object> result = new HashMap<>();
                        result.put("image_url", downalod_url.toString());
                        database.getReference("users").child(auth.getCurrentUser().getUid()).updateChildren(result);
                        hideProgress();
                        Toast.makeText(getActivity(),"Profile Image updated!",Toast.LENGTH_LONG).show();

                        //Do what you want with the url
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        hideProgress();
                        showMessage("Image not uploaded. Please try again!");
                    }
                });
            }
        });
    }

}