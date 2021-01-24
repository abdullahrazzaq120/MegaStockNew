package com.sortscript.megastock;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class DisplayProductDetailsActivity extends AppCompatActivity {

    String product_key;
    DatabaseReference reference, reference2;
    ImageView imageOpenIv;
    String nameP, descriptionP, categoryP, unitP, salePriceP, packingP, stockQuantityP, brandP, imageP, pidP;
    TextView nameOpenTv, categoryOpenTv, unitOpen, stockQuantityOpenTv, quantityOpenTv, salePriceOpenTv, packingOpenTv,
            brandOpenTv, descriptionOpenTv, addToCartOpenBtn, incrementOpenBtn, decrementOpenBtn, placeOrderOpenBtn;
    ProgressDialog loader, loader2;
    int quantity = 1;
    int priceSingleInt;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_product_details);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        loader = new ProgressDialog(DisplayProductDetailsActivity.this);
        loader2 = new ProgressDialog(DisplayProductDetailsActivity.this, R.style.CustomDialogTheme);
        mAuth = FirebaseAuth.getInstance();

        product_key = getIntent().getStringExtra("ProductKeyUser");
        reference = FirebaseDatabase.getInstance().getReference().child("Products");
        reference2 = FirebaseDatabase.getInstance().getReference().child("Members").child("Customers");

        imageOpenIv = findViewById(R.id.imgOpenId);
        nameOpenTv = findViewById(R.id.nameOpenId);
        categoryOpenTv = findViewById(R.id.categoryOpenId);
        stockQuantityOpenTv = findViewById(R.id.stockQuantityOpenId);
        salePriceOpenTv = findViewById(R.id.salePriceOpenId);
        descriptionOpenTv = findViewById(R.id.descriptionOpenId);
        brandOpenTv = findViewById(R.id.brandOpenId);
        unitOpen = findViewById(R.id.unitOpenId);
        packingOpenTv = findViewById(R.id.packingOpenId);

        quantityOpenTv = findViewById(R.id.quantityOpenId);

        incrementOpenBtn = findViewById(R.id.incrementOpenBtnId);
        decrementOpenBtn = findViewById(R.id.decrementOpenBtnId);
        addToCartOpenBtn = findViewById(R.id.addToCartOpenBtnId);
        placeOrderOpenBtn = findViewById(R.id.placeOrderOpenBtnId);

        reference.child(product_key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                imageP = snapshot.child("productImage").getValue().toString();
                nameP = snapshot.child("productTitle").getValue().toString();
                categoryP = snapshot.child("productCategory").getValue().toString();
                descriptionP = snapshot.child("productDescription").getValue().toString();
                packingP = snapshot.child("productPacking").getValue().toString();
                unitP = snapshot.child("productUnit").getValue().toString();
                stockQuantityP = snapshot.child("productStock").getValue().toString();
                salePriceP = snapshot.child("productSalePrice").getValue().toString();
                brandP = snapshot.child("productCompanyBrand").getValue().toString();
                pidP = snapshot.child("productId").getValue().toString();

                Glide.with(getApplicationContext()).load(imageP).into(imageOpenIv);
                nameOpenTv.setText(nameP);
                categoryOpenTv.setText(categoryP);
                descriptionOpenTv.setText(descriptionP);
                packingOpenTv.setText(packingP);
                unitOpen.setText(unitP);
                stockQuantityOpenTv.setText(stockQuantityP);
                salePriceOpenTv.setText(salePriceP);
                brandOpenTv.setText("By: " + brandP);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        if (quantity == 1) {
            decrementOpenBtn.setEnabled(false);
        }

        decrementOpenBtn.setOnClickListener(v -> {
            loader2.show();

            new Handler().postDelayed(() -> {
                if (quantity > 1) {
                    decrementOpenBtn.setEnabled(true);
                    incrementOpenBtn.setEnabled(true);
                    quantity = quantity - 1;
                    quantityOpenTv.setText(String.valueOf(quantity));

                    priceSingleInt = Integer.parseInt(salePriceP);

                    int tp = priceSingleInt * quantity;
                    String tpString = String.valueOf(tp);
                    salePriceOpenTv.setText(tpString);
                } else {
                    decrementOpenBtn.setEnabled(false);
                }

                loader2.dismiss();
            }, 600);
        });

        incrementOpenBtn.setOnClickListener(v -> {
            loader2.show();

            new Handler().postDelayed(() -> {
                quantity = quantity + 1;
                quantityOpenTv.setText(String.valueOf(quantity));
                decrementOpenBtn.setEnabled(true);

                priceSingleInt = Integer.parseInt(salePriceP);

                int tp = priceSingleInt * quantity;
                String tpString = String.valueOf(tp);
                salePriceOpenTv.setText(tpString);

                if (quantity > 1) {
                    decrementOpenBtn.setEnabled(true);
                }

                if (quantity == Integer.parseInt(stockQuantityP)) {
                    incrementOpenBtn.setEnabled(false);
                }

                loader2.dismiss();
            }, 600);
        });

        addToCartOpenBtn.setOnClickListener(v -> addToCartFunction());

        placeOrderOpenBtn.setOnClickListener(view -> placeOrderFunction());

    }

    private void placeOrderFunction() {
        Intent intent = new Intent(getApplicationContext(), ProceedToCheckutActivity.class);
        intent.putExtra("proceedProductKey", product_key);
        intent.putExtra("proceedProductTotal", salePriceOpenTv.getText().toString());
        intent.putExtra("proceedProductQuantity", quantityOpenTv.getText().toString());
        startActivity(intent);
    }

    private void addToCartFunction() {
        loader2.setMessage("Adding to Cart");
        loader2.show();

        Map<String, Object> map = new HashMap<>();
        map.put("cartName", nameP);
        map.put("cartImage", imageP);
        map.put("cartPacking", packingP);
        map.put("cartBrand", brandP);
        map.put("cartStockQuantity", stockQuantityP);
        map.put("cartNodeKey", product_key);
        map.put("cartCategory", categoryP);
        map.put("cartPrice", salePriceP);
        map.put("cartPId", pidP);

        reference2.child(mAuth.getCurrentUser().getUid()).child("MyCart").child(pidP).setValue(map)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        loader2.dismiss();
                        Toast.makeText(getApplicationContext(), "Added", Toast.LENGTH_SHORT).show();
                    } else {
                        loader2.dismiss();
                        Toast.makeText(getApplicationContext(), "Failed to add product", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}