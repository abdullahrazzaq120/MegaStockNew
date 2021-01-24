package com.sortscript.megastock.Vendor.ui_vendor.add_products;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.sortscript.megastock.R;
import com.sortscript.megastock.Vendor.VendorMenu;
import com.sortscript.megastock.Vendor.VendorProfileActivity;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import static android.app.Activity.RESULT_OK;

public class AddProductsFragment extends Fragment {

    private AddProductsViewModel addProductsViewModel;
    EditText addProductTitleEt, addProductPackingEt, addProductDescriptionEt, addProductCostPriceEt, addProductSalePriceEt,
            addProductCompanyBrandEt, addProductStockEt;
    Spinner addProductUnitSpinner, addProductCategorySpinner;
    CircularImageView addALogoImage;
    Button addALogoBtn, addPublishBtn;
    DatabaseReference reference;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri mImageUri;
    ProgressDialog loader;
    StorageReference mStorageRef;
    String categoryLink, unitLink;
    NumberPicker addProductUnitNumberNp;
    int unitCount;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        addProductsViewModel = new ViewModelProvider(this).get(AddProductsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_add_products, container, false);

        loader = new ProgressDialog(getActivity(), R.style.CustomDialogTheme);
        reference = FirebaseDatabase.getInstance().getReference().child("Products");
        mStorageRef = FirebaseStorage.getInstance().getReference("Products");
        addProductTitleEt = root.findViewById(R.id.addProductTitleEtId);
        addProductPackingEt = root.findViewById(R.id.addProductPackingEtId);
        addProductDescriptionEt = root.findViewById(R.id.addProductDescriptionEtId);
        addProductCostPriceEt = root.findViewById(R.id.addProductCostPriceEtId);
        addProductSalePriceEt = root.findViewById(R.id.addProductSalePriceEtId);
        addProductCompanyBrandEt = root.findViewById(R.id.addProductCompanyBrandEtId);
        addProductTitleEt = root.findViewById(R.id.addProductTitleEtId);
        addProductStockEt = root.findViewById(R.id.addProductStockEtId);
        addProductUnitSpinner = root.findViewById(R.id.addProductUnitSpinnerId);
        addProductCategorySpinner = root.findViewById(R.id.addProductCategorySpinnerId);
        addALogoImage = root.findViewById(R.id.addALogoImageId);
        addALogoBtn = root.findViewById(R.id.addALogoBtnId);
        addPublishBtn = root.findViewById(R.id.addPublishBtnId);
        addProductUnitNumberNp = root.findViewById(R.id.addProductUnitNumberNpId);

        addProductUnitNumberNp.setMinValue(1);
        addProductUnitNumberNp.setMaxValue(100);

        ArrayAdapter<CharSequence> arrayAdapterCategory = ArrayAdapter.createFromResource(getActivity(),
                R.array.categoryAdmin, android.R.layout.simple_list_item_activated_1);
        arrayAdapterCategory.setDropDownViewResource(android.R.layout.simple_list_item_1);
        addProductCategorySpinner.setAdapter(arrayAdapterCategory);
        addProductCategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                categoryLink = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        ArrayAdapter<CharSequence> arrayAdapterUnit = ArrayAdapter.createFromResource(getActivity(),
                R.array.unitAdmin, android.R.layout.simple_list_item_activated_1);
        arrayAdapterUnit.setDropDownViewResource(android.R.layout.simple_list_item_1);
        addProductUnitSpinner.setAdapter(arrayAdapterUnit);
        addProductUnitSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                unitLink = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        addALogoBtn.setOnClickListener(view -> chooseImage());

        addPublishBtn.setOnClickListener(view -> publishFunction());

        return root;
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
            Glide.with(this).load(mImageUri).into(addALogoImage);
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getActivity().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private String generateString1(int length1) {

        char[] chars = "abcdefghi01234jklmno56789pqrstuvwxyz0123456789".toCharArray();
        StringBuilder stringBuilder = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length1; i++) {
            char c = chars[random.nextInt(chars.length)];
            stringBuilder.append(c);
        }

        return stringBuilder.toString();
    }

    private String generateString2(int length2) {

        char[] chars = "abcdefghi01234jklmno56789pqrstuvwxyz0123456789".toCharArray();
        StringBuilder stringBuilder = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length2; i++) {
            char c = chars[random.nextInt(chars.length)];
            stringBuilder.append(c);
        }

        return stringBuilder.toString();
    }

    private void publishFunction() {


        loader.setTitle("Publishing");
        loader.setCanceledOnTouchOutside(false);
        loader.show();

        unitCount = addProductUnitNumberNp.getValue();

        String unitCountString = String.valueOf(unitCount);
        String titleP = addProductTitleEt.getText().toString();
        String packingP = addProductPackingEt.getText().toString();
        String descriptionP = addProductDescriptionEt.getText().toString();
        String costP = addProductCostPriceEt.getText().toString();
        String saleP = addProductSalePriceEt.getText().toString();
        String companyBrandP = addProductCompanyBrandEt.getText().toString();
        String stockP = addProductStockEt.getText().toString();
        String pIdLink = generateString1(4).concat(generateString2(4)).trim();

        if (mImageUri == null) {
            Toast.makeText(getActivity(), "Select a Logo", Toast.LENGTH_SHORT).show();
            loader.dismiss();
        } else if (titleP.isEmpty()) {
            addProductTitleEt.setError("Title Required!");
            loader.dismiss();
        } else if (packingP.isEmpty()) {
            addProductPackingEt.setError("Packing Required!");
            loader.dismiss();
        } else if (descriptionP.isEmpty()) {
            addProductDescriptionEt.setError("Description Required!");
            loader.dismiss();
        } else if (costP.isEmpty()) {
            addProductCostPriceEt.setError("Cost Price Required!");
            loader.dismiss();
        } else if (saleP.isEmpty()) {
            addProductSalePriceEt.setError("Sale Required!");
            loader.dismiss();
        } else if (companyBrandP.isEmpty()) {
            addProductCompanyBrandEt.setError("Brand Name Required!");
            loader.dismiss();
        } else if (stockP.isEmpty()) {
            addProductStockEt.setError("Item Stock Required!");
            loader.dismiss();
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
                    map.put("productUnit", unitCountString + " " + unitLink);
                    map.put("productCostPrice", costP);
                    map.put("productSalePrice", saleP);
                    map.put("productCompanyBrand", companyBrandP);
                    map.put("productStock", stockP);
                    map.put("productCategory", categoryLink);
                    map.put("productId", pIdLink);

                    reference.push().setValue(map)
                            .addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {
                                    loader.dismiss();
                                    Toast.makeText(getActivity(), "Product Successfully Published!", Toast.LENGTH_SHORT).show();
                                    emptyFieldsFunction();
                                } else {
                                    loader.dismiss();
                                    Toast.makeText(getActivity(), "Product Failed to Publish!", Toast.LENGTH_SHORT).show();
                                }
                            });
                });
            });
        }

    }

    private void emptyFieldsFunction() {
        addALogoImage.setImageResource(R.drawable.ic_baseline_image_24);
        mImageUri = null;
        addProductTitleEt.setText("");
        addProductPackingEt.setText("");
        addProductDescriptionEt.setText("");
        addProductCostPriceEt.setText("");
        addProductSalePriceEt.setText("");
        addProductCompanyBrandEt.setText("");
        addProductStockEt.setText("");
        addProductUnitNumberNp.setValue(1);
    }

}