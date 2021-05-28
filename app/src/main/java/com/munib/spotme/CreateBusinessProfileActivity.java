package com.munib.spotme;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.munib.spotme.base.BaseActivity;
import com.munib.spotme.dataModels.BusinessProfileModel;

import java.io.File;
import java.util.Calendar;

public class CreateBusinessProfileActivity extends BaseActivity {

    TextView create_btn;
    Uri image_url=null;
    Uri downalod_url=null;
    EditText business_name,tax_id,ein_number,first_name,last_name,social_security,business_description,business_phone,business_email;
    ImageView image_upload;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_business);

        business_name=(EditText) findViewById(R.id.edtBusinessName);
        tax_id=(EditText) findViewById(R.id.edtTaxId);
        ein_number=(EditText) findViewById(R.id.edtEinNumber);
        first_name=(EditText) findViewById(R.id.edtFirstName);
        last_name=(EditText) findViewById(R.id.edtLastName);
        social_security=(EditText) findViewById(R.id.edtSocialSecurity);
        business_description=(EditText) findViewById(R.id.edtDesc);
        business_email=(EditText) findViewById(R.id.edtBusinessEmail);
        business_phone=(EditText) findViewById(R.id.edtBusinessPhone);
        image_upload=(ImageView) findViewById(R.id.image_upload);

        image_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagePicker.Companion.with(CreateBusinessProfileActivity.this)
                        .crop()	    			//Crop image(Optional), Check Customization for more option
                        .compress(1024)			//Final image size will be less than 1 MB(Optional)
                        .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                        .start();
            }
        });


        create_btn=(TextView) findViewById(R.id.create_btn);
        create_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(business_name.getText().toString().equals(""))
                    business_name.setError("Please input business name");
                else if(tax_id.getText().toString().equals(""))
                    tax_id.setError("Please input Tax ID");
                else if(ein_number.getText().toString().equals(""))
                    ein_number.setError("Please input EIN Number");
                else if(business_email.getText().toString().equals(""))
                    business_email.setError("Please input business email");
                else if(business_phone.getText().toString().equals(""))
                    business_phone.setError("Please input business phone");
                else if(first_name.getText().toString().equals(""))
                    first_name.setError("Please input first name");
                else if(last_name.getText().toString().equals(""))
                    last_name.setError("Please input last name");
                else if(social_security.getText().toString().equals(""))
                    social_security.setError("Please input Social Security Number");
                else if(business_description.getText().toString().equals(""))
                    business_description.setError("Please input about your business");
                else if(image_url==null)
                    showMessage("Please upload business image");
                else {
                    LayoutInflater factory = LayoutInflater.from(CreateBusinessProfileActivity.this);
                    final View deleteDialogView = factory.inflate(R.layout.dialog_business_settings, null);
                    final AlertDialog deleteDialog = new AlertDialog.Builder(CreateBusinessProfileActivity.this).create();
                    deleteDialog.setView(deleteDialogView);
                    deleteDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

                    EditText shares = (EditText) deleteDialogView.findViewById(R.id.edtShares);
                    EditText share_value = (EditText) deleteDialogView.findViewById(R.id.edtShareValue);
                    deleteDialogView.findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //your business logic
                            if (shares.getText().toString().equals("")) {
                                shares.setError("Please input total shares");
                            } else if (share_value.getText().toString().equals("")) {
                                share_value.setError("Please input each share value");
                            } else {
                                showProgress();
                                uploadFile(image_url,shares.getText().toString(),share_value.getText().toString());
                                deleteDialog.dismiss();
                            }

                        }
                    });

                    deleteDialog.show();
                }
            }
        });
    }
    @Override
    public void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getResources().getColor(R.color.com_facebook_blue));
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            Uri fileUri = data.getData();
            image_upload.setImageURI(fileUri);

            //You can get File object from intent
            File file = ImagePicker.Companion.getFile(data);
            image_url = fileUri;
        }
    }

    private void uploadFile(Uri ui,String shares,String share_value) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl("gs://spotme-39709.appspot.com");
        StorageReference mountainImagesRef = storageRef.child("business_images/" + currentUserData.getUid()+"_" + Calendar.getInstance().getTimeInMillis());

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
                        BusinessProfileModel businessProfileModel = new BusinessProfileModel(business_name.getText().toString(), tax_id.getText().toString(), ein_number.getText().toString(), first_name.getText().toString(), last_name.getText().toString(), social_security.getText().toString(), business_description.getText().toString(), downalod_url.toString(),0,Integer.parseInt(shares),Integer.parseInt(share_value),business_phone.getText().toString(),business_email.getText().toString());
                        database.getReference("business").child(auth.getCurrentUser().getUid()+"_"+business_name.getText().toString()).setValue(businessProfileModel);
                        hideProgress();
                        Toast.makeText(CreateBusinessProfileActivity.this,"Business profile request sent successfully",Toast.LENGTH_LONG).show();
                        finish();

                        //Do what you want with the url
                    }
                });
            }
        });
    }

}
