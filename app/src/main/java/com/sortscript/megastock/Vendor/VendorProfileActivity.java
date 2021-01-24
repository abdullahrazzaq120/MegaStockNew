package com.sortscript.megastock.Vendor;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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
import com.sortscript.megastock.R;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class VendorProfileActivity extends AppCompatActivity {

    TextInputEditText vendorFNameEt, vendorLNameEt, vendorShopNameEt, vendorAddressEt, vendorContactEt;
    Button vendorUploadLogoBtn, vendorSaveProfileBtn;
    ImageView vendorUploadLogoImage;
    FirebaseAuth mAuth;
    DatabaseReference reference;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri mImageUri;
    ProgressDialog loader, loader2;
    StorageReference mStorageRef;
    LinearLayout llVendor;
    private static final String VENDOR_fNAME = "VendorFName";
    private static final String VENDOR_lNAME = "VendorLName";
    private static final String VENDOR_ADDRESS = "VendorAddress";
    private static final String VENDOR_CONTACT = "VendorContact";
    private static final String VENDOR_IMAGE = "VendorImage";
    private static final String VENDOR_SHOP_NAME = "VendorShopName";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_profile);

        getSupportActionBar().hide();
        llVendor = findViewById(R.id.llVendorProfileId);
        loader = new ProgressDialog(VendorProfileActivity.this);
        loader2 = new ProgressDialog(this, R.style.CustomDialogTheme);
        mAuth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference().child("Vendors");
        mStorageRef = FirebaseStorage.getInstance().getReference("Vendors");

        vendorFNameEt = findViewById(R.id.vendorFNameEtId);
        vendorLNameEt = findViewById(R.id.vendorLNameEtId);
        vendorShopNameEt = findViewById(R.id.vendorShopNameEtId);
        vendorAddressEt = findViewById(R.id.vendorAddressEtId);
        vendorContactEt = findViewById(R.id.vendorContactEtId);
        vendorUploadLogoBtn = findViewById(R.id.vendorUploadLogoBtnId);
        vendorSaveProfileBtn = findViewById(R.id.vendorSaveProfileBtnId);
        vendorUploadLogoImage = findViewById(R.id.vendorUploadLogoImageId);

        vendorUploadLogoBtn.setOnClickListener(view -> chooseImage());

        vendorSaveProfileBtn.setOnClickListener(view -> uploadProfileVendor());

    }

    private void uploadProfileVendor() {

        try {

            loader.setTitle("Setting Your Profile");
            loader.setCanceledOnTouchOutside(false);
            loader.show();

            String fName = vendorFNameEt.getText().toString();
            String lName = vendorLNameEt.getText().toString();
            String shopName = vendorShopNameEt.getText().toString();
            String address = vendorAddressEt.getText().toString();
            String phone = vendorContactEt.getText().toString();

            if (mImageUri == null) {
                Toast.makeText(this, "Logo is Required!", Toast.LENGTH_SHORT).show();
                loader.dismiss();
                chooseImage();
            } else if (fName.isEmpty()) {
                loader.dismiss();
                vendorFNameEt.setError("First Name Required!");
            } else if (lName.isEmpty()) {
                loader.dismiss();
                vendorLNameEt.setError("Last Name Required!");
            } else if (shopName.isEmpty()) {
                loader.dismiss();
                vendorShopNameEt.setError("Shop Name Required!");
            } else if (address.isEmpty()) {
                loader.dismiss();
                vendorAddressEt.setError("Address Required!");
            } else if (phone.isEmpty()) {
                loader.dismiss();
                vendorContactEt.setError("Contact Number Required!");
            } else {
                StorageReference fileReference = mStorageRef.child(mAuth.getCurrentUser().getUid() + "." + getFileExtension(mImageUri));
                fileReference.putFile(mImageUri).addOnSuccessListener(taskSnapshot -> {
                    Task<Uri> task = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                    task.addOnSuccessListener(uri -> {
                        String imageLink = uri.toString();

                        Map<String, Object> map = new HashMap<>();
                        map.put(VENDOR_fNAME, fName);
                        map.put(VENDOR_lNAME, lName);
                        map.put(VENDOR_SHOP_NAME, shopName);
                        map.put(VENDOR_ADDRESS, address);
                        map.put(VENDOR_IMAGE, imageLink);
                        map.put(VENDOR_CONTACT, phone);

                        reference.child(mAuth.getCurrentUser().getUid()).setValue(map)
                                .addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        loader.dismiss();
                                        Intent i = new Intent(VendorProfileActivity.this, VendorMenu.class);
                                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(i);
                                        Toast.makeText(VendorProfileActivity.this, "Profile Set Successfully", Toast.LENGTH_SHORT).show();
                                    } else {
                                        loader.dismiss();
                                        Toast.makeText(VendorProfileActivity.this, "Profile Set Failed", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    });
                });
            }
        } catch (Exception ignored) {

        }
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
            Glide.with(this).load(mImageUri).into(vendorUploadLogoImage);
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    @Override
    protected void onStart() {
        super.onStart();

        try {
            llVendor.setVisibility(View.GONE);
            loader2.setCancelable(false);
            loader2.setCanceledOnTouchOutside(false);
            loader2.show();

            reference.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String fname = snapshot.child(VENDOR_fNAME).getValue().toString();
                    String lname = snapshot.child(VENDOR_lNAME).getValue().toString();
                    String shopname = snapshot.child(VENDOR_SHOP_NAME).getValue().toString();
                    String address = snapshot.child(VENDOR_ADDRESS).getValue().toString();
                    String image = snapshot.child(VENDOR_IMAGE).getValue().toString();
                    String phone = snapshot.child(VENDOR_CONTACT).getValue().toString();

                    if (!fname.isEmpty() && !lname.isEmpty() && !shopname.isEmpty() && !address.isEmpty() && !phone.isEmpty() && !image.isEmpty()) {

                        Intent i = new Intent(VendorProfileActivity.this, VendorMenu.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);
                    } else {
                        new Handler().postDelayed(() -> {
                            loader2.dismiss();
                            llVendor.setVisibility(View.VISIBLE);
                        }, 2000);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        } catch (Exception ignored) {
        }
    }
}