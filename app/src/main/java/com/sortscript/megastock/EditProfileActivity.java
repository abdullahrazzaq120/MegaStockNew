package com.sortscript.megastock;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.HashMap;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity {

    CircularImageView editProfileImage;
    FirebaseAuth mAuth;
    DatabaseReference refCustomers, mRef;
    Button editPhotoBtn, saveImageBtn, goToPersonalInfoBtn, goToAddressInfoBtn, goToChangePasswordBtn;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri mImageUri;
    StorageReference mStorageRef;
    ProgressDialog loader2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        getSupportActionBar().hide();

        mAuth = FirebaseAuth.getInstance();
        loader2 = new ProgressDialog(this, R.style.CustomDialogTheme);
        mStorageRef = FirebaseStorage.getInstance().getReference("Members").child("Customers").child(mAuth.getCurrentUser().getUid());
        refCustomers = FirebaseDatabase.getInstance().getReference().child("Members").child("Customers");
        mRef = FirebaseDatabase.getInstance().getReference().child("Members").child("Customers").child(mAuth.getCurrentUser().getUid());

        editProfileImage = findViewById(R.id.editProfileImageId);
        editPhotoBtn = findViewById(R.id.editPhotoBtnId);
        saveImageBtn = findViewById(R.id.saveImageBtnId);

        goToPersonalInfoBtn = findViewById(R.id.goToPersonalInfoBtnId);
        goToAddressInfoBtn = findViewById(R.id.goToAddressInfoBtnId);
        goToChangePasswordBtn = findViewById(R.id.goToChangePasswordBtnId);

        refCustomers.child(mAuth.getCurrentUser().getUid()).child("UserDetails").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    String image = snapshot.child("CustomerImage").getValue().toString();
                    Glide.with(EditProfileActivity.this).load(image).into(editProfileImage);
                } catch (Exception ignored) {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        editPhotoBtn.setOnClickListener(v -> chooseImage());

        saveImageBtn.setOnClickListener(v -> {
            try {
                loader2.setTitle("Updating...");
                loader2.setCanceledOnTouchOutside(false);
                loader2.show();

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
                                map.put("CustomerImage", imageLink);

                                mRef.child("UserDetails").updateChildren(map)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    loader2.dismiss();
                                                    Toast.makeText(EditProfileActivity.this, "Image Saved!", Toast.LENGTH_SHORT).show();
                                                    saveImageBtn.setEnabled(false);
                                                } else {
                                                    loader2.dismiss();
                                                    Toast.makeText(EditProfileActivity.this, "Failed To Change Image", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            }
                        });
                    }
                });
            } catch (Exception ignored) {

            }
        });

        goToPersonalInfoBtn.setOnClickListener(v -> startActivity(new Intent(EditProfileActivity.this, EditPersonalInfoActivity.class)));
        goToChangePasswordBtn.setOnClickListener(v -> startActivity(new Intent(EditProfileActivity.this, EditChangePasswordActivity.class)));
        goToAddressInfoBtn.setOnClickListener(v -> startActivity(new Intent(EditProfileActivity.this, EditAddressInfoActivity.class)));
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
            Glide.with(this).load(mImageUri).into(editProfileImage);
            saveImageBtn.setEnabled(true);
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

        for (UserInfo user : FirebaseAuth.getInstance().getCurrentUser().getProviderData()) {
            if (user.getProviderId().equals("password")) {
                goToChangePasswordBtn.setVisibility(View.VISIBLE);
            }
        }
    }
}