package com.sortscript.megastock;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class ProfileUserActivity extends AppCompatActivity {

    Spinner registerUserCityEt;
    TextInputEditText registerUserFNameEt, registerUserLNameEt, registerUNameEt, registerCountryEt, registerUserAddressEt,
            registerPostalCodeEt, registerUserAgeEt, registerPhoneEt;
    Button registerBtn, chooseRegisterImageBtn, backLogoutBtn;
    RadioButton rbOne, rbTwo;
    ImageView registerUserImageIv;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri mImageUri;
    DatabaseReference mRef;
    ProgressDialog loader, loader2;
    StorageReference mStorageRef;
    FirebaseAuth mAuth;
    String cityLink, genderLink;
    ScrollView llProfile;
    boolean un;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_user);

        getSupportActionBar().hide();

        un = false;
        mAuth = FirebaseAuth.getInstance();
        mRef = FirebaseDatabase.getInstance().getReference().child("Members").child("Customers");
        mStorageRef = FirebaseStorage.getInstance().getReference("Members").child("Customers").child(mAuth.getCurrentUser().getUid());
        loader = new ProgressDialog(ProfileUserActivity.this);
        loader2 = new ProgressDialog(this, R.style.CustomDialogTheme);

        llProfile = findViewById(R.id.llProfileId);
        backLogoutBtn = findViewById(R.id.backLogoutId);
        registerUNameEt = findViewById(R.id.registerUNameId);
        registerUserFNameEt = findViewById(R.id.registerUserFNameId);
        registerUserLNameEt = findViewById(R.id.registerUserLNameId);
        registerUserCityEt = findViewById(R.id.registerUserCityId);
        registerCountryEt = findViewById(R.id.registerCountryId);
        registerUserAddressEt = findViewById(R.id.registerUserAddressId);
        registerPostalCodeEt = findViewById(R.id.registerPostalCodeId);
        registerUserAgeEt = findViewById(R.id.registerUserAgeId);
        registerPhoneEt = findViewById(R.id.registerPhoneId);
        registerBtn = findViewById(R.id.registerBtnId);
        chooseRegisterImageBtn = findViewById(R.id.chooseRegisterImageBtnId);
        rbOne = findViewById(R.id.rb1);
        rbTwo = findViewById(R.id.rb2);
        registerUserImageIv = findViewById(R.id.registerUserImageId);

        backLogoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                startActivity(new Intent(ProfileUserActivity.this, WhoYouAre.class));
                finish();
            }
        });

        chooseRegisterImageBtn.setOnClickListener(v -> chooseImage());

        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(ProfileUserActivity.this,
                R.array.cities, android.R.layout.simple_list_item_activated_1);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        registerUserCityEt.setAdapter(arrayAdapter);
        registerUserCityEt.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                cityLink = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    uploadProfile();
                } catch (Exception ignored) {

                }
            }
        });
    }

    private void uploadProfile() {
        loader.setTitle("Setting Your Profile");
        loader.setCanceledOnTouchOutside(false);
        loader.show();

        if (rbOne.isChecked()) {
            genderLink = rbOne.getText().toString();
        }
        if (rbTwo.isChecked()) {
            genderLink = rbTwo.getText().toString();
        }

        String uName = registerUNameEt.getText().toString();
        String fName = registerUserFNameEt.getText().toString();
        String lName = registerUserLNameEt.getText().toString();
        String uAddress = registerUserAddressEt.getText().toString();
        String uPhone = registerPhoneEt.getText().toString();
        String uAge = registerUserAgeEt.getText().toString();
        String uPostalCode = registerPostalCodeEt.getText().toString();
        String uCountry = registerCountryEt.getText().toString();

        if (fName.isEmpty()) {
            loader.dismiss();
            registerUserFNameEt.setError("Enter First Name Please");
        } else if (lName.isEmpty()) {
            loader.dismiss();
            registerUserLNameEt.setError("Enter Last Name Please");
        } else if (uName.isEmpty()) {
            loader.dismiss();
            registerUNameEt.setError("Enter User Name Please");
        } else if (un) {
            loader.dismiss();
            registerUNameEt.setError("User Name Already Exists");
        } else if (uAddress.isEmpty()) {
            loader.dismiss();
            registerUserAddressEt.setError("Enter User Address Please");
        } else if (uPhone.isEmpty()) {
            loader.dismiss();
            registerPhoneEt.setError("Enter Contact Information Please");
        } else if (mImageUri == null) {
            Map<String, Object> map = new HashMap<>();
            map.put("CustomerUserName", uName);
            map.put("CustomerFirstName", fName);
            map.put("CustomerLastName", lName);
            map.put("CustomerImage", "");
            map.put("CustomerGender", genderLink);
            map.put("CustomerCountry", uCountry);
            map.put("CustomerCity", cityLink);
            map.put("CustomerAddress", uAddress);
            map.put("CustomerPostalCode", uPostalCode);
            map.put("CustomerAge", uAge);
            map.put("CustomerContactNumber", uPhone);

            mRef.child(mAuth.getCurrentUser().getUid()).child("UserDetails").setValue(map)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                loader.dismiss();
                                startActivity(new Intent(ProfileUserActivity.this, UserMenu.class));
                                Toast.makeText(ProfileUserActivity.this, "Profile Set Successfully", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                loader.dismiss();
                                Toast.makeText(ProfileUserActivity.this, "Profile Set Failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            StorageReference fileReference = mStorageRef.child("UserDetails").child(mAuth.getCurrentUser().getUid() + "." + getFileExtension(mImageUri));
            fileReference.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> task = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                    task.addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String imageLink = uri.toString();

                            Map<String, Object> map = new HashMap<>();
                            map.put("CustomerUserName", uName);
                            map.put("CustomerFirstName", fName);
                            map.put("CustomerLastName", lName);
                            map.put("CustomerImage", imageLink);
                            map.put("CustomerGender", genderLink);
                            map.put("CustomerCountry", uCountry);
                            map.put("CustomerCity", cityLink);
                            map.put("CustomerAddress", uAddress);
                            map.put("CustomerPostalCode", uPostalCode);
                            map.put("CustomerAge", uAge);
                            map.put("CustomerContactNumber", uPhone);

                            mRef.child(mAuth.getCurrentUser().getUid()).child("UserDetails").setValue(map)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                loader.dismiss();
                                                startActivity(new Intent(ProfileUserActivity.this, UserMenu.class));
                                                Toast.makeText(ProfileUserActivity.this, "Profile Set Successfully", Toast.LENGTH_SHORT).show();
                                                finish();
                                            } else {
                                                loader.dismiss();
                                                Toast.makeText(ProfileUserActivity.this, "Profile Set Failed", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }
                    });
                }
            });
        }

    }

    @Override
    protected void onStart() {
        super.onStart();


        llProfile.setVisibility(View.GONE);
        loader2.setCancelable(false);
        loader2.setCanceledOnTouchOutside(false);
        loader2.show();

        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                try {
                    if (snapshot.exists()) {

                        String a = snapshot.child(mAuth.getCurrentUser().getUid()).toString();
                        Log.e("datas", a);
                        if (a.contains("UserDetails")) {
                            loader2.dismiss();

                            Intent i = new Intent(ProfileUserActivity.this, UserMenu.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(i);
                            overridePendingTransition(0, 0);

                        } else {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    loader2.dismiss();
                                    llProfile.setVisibility(View.VISIBLE);
                                }
                            }, 2000);

                        }
                    } else {
                        loader2.dismiss();
                        llProfile.setVisibility(View.VISIBLE);
                    }

                } catch (Exception e) {
                    loader2.dismiss();
                    llProfile.setVisibility(View.VISIBLE);
//                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                loader2.dismiss();
                llProfile.setVisibility(View.VISIBLE);
            }
        });
    }

    private void chooseImage() {
        //choose the image from internal storage

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            mImageUri = data.getData();
            Glide.with(this).load(mImageUri).into(registerUserImageIv);
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }
}