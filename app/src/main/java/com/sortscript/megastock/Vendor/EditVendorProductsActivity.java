package com.sortscript.megastock.Vendor;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Task;
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

public class EditVendorProductsActivity extends AppCompatActivity {

    EditText updateProductTitleEt, updateProductPackingEt, updateProductDescriptionEt, updateProductCostPriceEt, updateProductSalePriceEt,
            updateProductCompanyBrandEt, updateProductStockEt, updateUnitEt;
    Spinner updateProductCategorySpinner;
    ImageView updateALogoImage;
    Button updateALogoBtn, updatePublishBtn;
    DatabaseReference reference;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri mImageUri;
    ProgressDialog loader;
    StorageReference mStorageRef;
    String categoryLink, unitLink;
    String pos;
    String pIdLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_vendor_products);

        pos = getIntent().getStringExtra("position");
        loader = new ProgressDialog(EditVendorProductsActivity.this, R.style.CustomDialogTheme);
        reference = FirebaseDatabase.getInstance().getReference().child("Products");
        mStorageRef = FirebaseStorage.getInstance().getReference("Products");
        updateProductTitleEt = findViewById(R.id.updateProductTitleEtId);
        updateProductPackingEt = findViewById(R.id.updateProductPackingEtId);
        updateProductDescriptionEt = findViewById(R.id.updateProductDescriptionEtId);
        updateProductCostPriceEt = findViewById(R.id.updateProductCostPriceEtId);
        updateProductSalePriceEt = findViewById(R.id.updateProductSalePriceEtId);
        updateProductCompanyBrandEt = findViewById(R.id.updateProductCompanyBrandEtId);
        updateProductTitleEt = findViewById(R.id.updateProductTitleEtId);
        updateProductStockEt = findViewById(R.id.updateProductStockEtId);
        updateUnitEt = findViewById(R.id.updateUnitEtId);
        updateProductCategorySpinner = findViewById(R.id.updateProductCategorySpinnerId);
        updateALogoImage = findViewById(R.id.updateALogoImageId);
        updateALogoBtn = findViewById(R.id.updateALogoBtnId);
        updatePublishBtn = findViewById(R.id.updatePublishBtnId);

        reference.child(pos).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String image = snapshot.child("productImage").getValue().toString();
                String title = snapshot.child("productTitle").getValue().toString();
                String packing = snapshot.child("productPacking").getValue().toString();
                String description = snapshot.child("productDescription").getValue().toString();
                String unit = snapshot.child("productUnit").getValue().toString();
                String costPrice = snapshot.child("productCostPrice").getValue().toString();
                String salePrice = snapshot.child("productSalePrice").getValue().toString();
                String companyBrand = snapshot.child("productCompanyBrand").getValue().toString();
                String stock = snapshot.child("productStock").getValue().toString();
                String category = snapshot.child("productCategory").getValue().toString();
                pIdLink = snapshot.child("productId").getValue().toString();

                ArrayAdapter<CharSequence> arrayAdapterCategory = ArrayAdapter.createFromResource(getApplicationContext(),
                        R.array.categoryAdmin, android.R.layout.simple_list_item_activated_1);
                arrayAdapterCategory.setDropDownViewResource(android.R.layout.simple_list_item_1);
                updateProductCategorySpinner.setAdapter(arrayAdapterCategory);
                updateProductCategorySpinner.setSelection(arrayAdapterCategory.getPosition(category));
                updateProductCategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        categoryLink = parent.getItemAtPosition(position).toString();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });

                Glide.with(getApplicationContext()).load(image).into(updateALogoImage);
                updateProductTitleEt.setText(title);
                updateProductPackingEt.setText(packing);
                updateProductDescriptionEt.setText(description);
                updateProductCostPriceEt.setText(costPrice);
                updateProductSalePriceEt.setText(salePrice);
                updateUnitEt.setText(unit);
                updateProductCompanyBrandEt.setText(companyBrand);
                updateProductStockEt.setText(stock);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        updateALogoBtn.setOnClickListener(view -> chooseImage());

        updatePublishBtn.setOnClickListener(view -> updateFunction());

    }


    private void updateFunction() {
        loader.setTitle("Updating...");
        loader.setCanceledOnTouchOutside(false);
        loader.show();

        String titleP = updateProductTitleEt.getText().toString();
        String packingP = updateProductPackingEt.getText().toString();
        String descriptionP = updateProductDescriptionEt.getText().toString();
        String costP = updateProductCostPriceEt.getText().toString();
        String saleP = updateProductSalePriceEt.getText().toString();
        String companyBrandP = updateProductCompanyBrandEt.getText().toString();
        String stockP = updateProductStockEt.getText().toString();
        String unitP = updateUnitEt.getText().toString();

        if (mImageUri == null) {
            Map<String, Object> map = new HashMap<>();
            map.put("productTitle", titleP);
            map.put("productPacking", packingP);
            map.put("productDescription", descriptionP);
            map.put("productUnit", unitP);
            map.put("productCostPrice", costP);
            map.put("productSalePrice", saleP);
            map.put("productCompanyBrand", companyBrandP);
            map.put("productStock", stockP);
            map.put("productCategory", categoryLink);
            map.put("productId", pIdLink);

            reference.child(pos).updateChildren(map)
                    .addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            loader.dismiss();
                            Toast.makeText(getApplicationContext(), "Product Successfully Update!", Toast.LENGTH_SHORT).show();
                        } else {
                            loader.dismiss();
                            Toast.makeText(getApplicationContext(), "Product Failed to Update!", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            StorageReference fileReference = mStorageRef.child(pIdLink + "." + getFileExtension(mImageUri));
            fileReference.putFile(mImageUri).addOnSuccessListener(taskSnapshot -> {
                Task<Uri> task = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                task.addOnSuccessListener(uri -> {
                    String imageLink = uri.toString();

                    Map<String, Object> map = new HashMap<>();
                    map.put("productImage", imageLink);
                    map.put("productTitle", titleP);
                    map.put("productPacking", packingP);
                    map.put("productDescription", descriptionP);
                    map.put("productUnit", unitP);
                    map.put("productCostPrice", costP);
                    map.put("productSalePrice", saleP);
                    map.put("productCompanyBrand", companyBrandP);
                    map.put("productStock", stockP);
                    map.put("productCategory", categoryLink);
                    map.put("productId", pIdLink);

                    reference.child(pos).updateChildren(map)
                            .addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {
                                    loader.dismiss();
                                    Toast.makeText(getApplicationContext(), "Product Successfully Update!", Toast.LENGTH_SHORT).show();
                                } else {
                                    loader.dismiss();
                                    Toast.makeText(getApplicationContext(), "Product Failed to Update!", Toast.LENGTH_SHORT).show();
                                }
                            });
                });
            });
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
            Glide.with(this).load(mImageUri).into(updateALogoImage);
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

}