package com.munib.spotme.fragments;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.munib.spotme.DashboardLoginLinkActivity;
import com.munib.spotme.LoginActivity;
import com.munib.spotme.PaymentSetupActivity;
import com.munib.spotme.R;
import com.munib.spotme.SetupPinActivity;
import com.munib.spotme.adapters.ReviewAdapter;
import com.munib.spotme.base.BaseFragment;
import com.munib.spotme.dataModels.ReviewsModel;
import com.munib.spotme.dataModels.UserModel;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.munib.spotme.utils.CommonUtils.TAGZ;

public class ProfileFragment extends BaseFragment {

    TextView lender_rating,borrower_rating,name,username,email,phone,lender_ratings_txt,borrower_rating_txt,total_loaned,total_borrowed,total_shares,total_invested;
    RecyclerView rv;
    ArrayList<ReviewsModel> reviewsModelArrayList;
    LinearLayout payouts_btn,edit_profile_btn,logout_btn,pin_btn;
    ReviewAdapter adapter;
    ImageView user_image;
    int lender_rating_value=0,borrower_rating_value=0;
    TextView no_data_found;
    Uri image_url=null;
    Uri downalod_url=null;
    Switch aSwitch;
    SharedPreferences pref;
    SharedPreferences.Editor editor;

    @Override
    protected void injectView() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_user_profile, container, false);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        reviewsModelArrayList=new ArrayList<>();
        adapter=new ReviewAdapter(requireActivity(),reviewsModelArrayList);

        no_data_found=(TextView) view.findViewById(R.id.no_data_found);
        lender_rating=(TextView) view.findViewById(R.id.lender_rating);
        borrower_rating=(TextView) view.findViewById(R.id.borrower_rating);
        lender_ratings_txt=(TextView) view.findViewById(R.id.lender_rating_txt);
        borrower_rating_txt=(TextView) view.findViewById(R.id.borrower_rating_txt);
        aSwitch=(Switch) view.findViewById(R.id.aSwitch);



        name=(TextView) view.findViewById(R.id.name);
        username=(TextView) view.findViewById(R.id.username);
        phone=(TextView) view.findViewById(R.id.phone);
//        email=(TextView) view.findViewById(R.id.email);
//        phone=(TextView) view.findViewById(R.id.phone);
        user_image=(ImageView) view.findViewById(R.id.image);
        user_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagePicker.Companion.with(ProfileFragment.this)
                        .crop()	    			//Crop image(Optional), Check Customization for more option
                        .compress(1024)			//Final image size will be less than 1 MB(Optional)
                        .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                        .start();
            }
        });

        rv=(RecyclerView) view.findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(requireActivity()));
        rv.setAdapter(adapter);

        payouts_btn=view.findViewById(R.id.payouts_btn);
        edit_profile_btn=view.findViewById(R.id.edit_profile_btn);
        logout_btn=view.findViewById(R.id.logout_btn);
        pin_btn=view.findViewById(R.id.pin_btn);


        pref= getActivity().getSharedPreferences("MyPref", 0); // 0 - for private mode
        editor= pref.edit();



        pin_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(aSwitch.isChecked())
                {
                    aSwitch.setChecked(false);
                }else{
                    aSwitch.setChecked(true);
                }
            }
        });

        payouts_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), DashboardLoginLinkActivity.class));
            }
        });

//        edit_profile_btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//            }
//        });

        logout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences pref;
                SharedPreferences.Editor editor;
                pref= getActivity().getSharedPreferences("MyPref", 0); // 0 - for private mode
                editor= pref.edit();
                editor.clear();
                editor.apply();

                FirebaseAuth.getInstance().signOut();
                getActivity().finish();
                startActivity(new Intent(getActivity(), LoginActivity.class));
            }
        });

        showProgress();
        if(auth.getCurrentUser()!=null){
            FirebaseDatabase.getInstance().getReference().child("users").
                    child(auth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Log.d(TAGZ, dataSnapshot.toString());
                        UserModel offer = dataSnapshot.getValue(UserModel.class);
                        offer.setUid(dataSnapshot.getKey());

                        name.setText(offer.getName());
                        username.setText("@"+offer.getUsername());
//                        email.setText(offer.getEmail());
//                        phone.setText(offer.getPhone());
                        if(!offer.getImage_url().equals(""))
                            Picasso.get().load(offer.getImage_url()).into(user_image);
                    } else {

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.d(TAGZ, error.getDetails());

                }
            });


            FirebaseDatabase.getInstance().getReference().child("reviews").child(auth.getCurrentUser().getUid()).
                    orderByChild("review_type").equalTo("lender").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        no_data_found.setVisibility(View.GONE);
                        Log.d("mubi",dataSnapshot.toString());
                        int a=0;
                        for (DataSnapshot datas : dataSnapshot.getChildren()) {
                            ReviewsModel offer = datas.getValue(ReviewsModel.class);
                            a+=offer.getRating();
                            reviewsModelArrayList.add(offer);
                        }
                        if(a!=0) {
                            lender_rating_value = (int) ((int) a / dataSnapshot.getChildrenCount());
                            lender_rating.setText(lender_rating_value + "");
                        } else
                            lender_rating.setText("N/A");
                        adapter.notifyDataSetChanged();
                        //  hideProgress();

                    } else {
                        hideProgress();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.d(TAGZ, error.getDetails());
                    hideProgress();
                }
            });

            FirebaseDatabase.getInstance().getReference().child("reviews").child(auth.getCurrentUser().getUid()).
                    orderByChild("review_type").equalTo("borrower").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        no_data_found.setVisibility(View.GONE);
                        Log.d("mubi",dataSnapshot.toString());
                        int a=0;
                        for (DataSnapshot datas : dataSnapshot.getChildren()) {
                            ReviewsModel offer = datas.getValue(ReviewsModel.class);
                            a+=offer.getRating();
                            reviewsModelArrayList.add(offer);
                        }
                        if(a!=0) {
                            borrower_rating_value = (int) ((int) a / dataSnapshot.getChildrenCount());
                            borrower_rating.setText(borrower_rating_value + "");
                        } else
                            borrower_rating.setText("N/A");
                        adapter.notifyDataSetChanged();
                        hideProgress();

                    } else {
                        hideProgress();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.d(TAGZ, error.getDetails());
                    hideProgress();
                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        aSwitch.setOnCheckedChangeListener(null);
        if(pref.getBoolean("pin_enabled", false)){
            aSwitch.setChecked(true);
        }else{
            aSwitch.setChecked(false);
        }
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b)
                {
                    startActivity(new Intent(getActivity(), SetupPinActivity.class));
                }else{
                    editor.putBoolean("pin_enabled",false);
                    editor.apply();
                }
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            Uri fileUri = data.getData();
            user_image.setImageURI(fileUri);

            //You can get File object from intent
            File file = ImagePicker.Companion.getFile(data);
            image_url = fileUri;
            showProgress();
            uploadFile(image_url);
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
